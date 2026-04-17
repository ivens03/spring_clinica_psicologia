package psicologia.clinica.acesso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.acesso.dtos.AcessoRequestDTO;
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.usuario.model.PerfilRoot;

import java.time.LocalDate;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para entrada no sistema")
class EntrandoNoSistemaIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Entrando como gestor da clínica e retornando direcionamento administrativo")
    void entrandoComoGestorClinicaRetornandoDirecionamentoAdministrativo() throws Exception {
        criarClinica("22334455000166");
        criarUsuarioGestorClinica("22334455000", "gestor.acesso@clinica.local", "22334455000166");

        AcessoRequestDTO request = new AcessoRequestDTO("gestor.acesso@clinica.local", "Senha@123");

        mockMvc.perform(post("/acesso/entrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Gestor Acesso"))
                .andExpect(jsonPath("$.email").value("gestor.acesso@clinica.local"))
                .andExpect(jsonPath("$.perfilRoot").value("GESTOR_CLINICA"))
                .andExpect(jsonPath("$.direcionamento").value("PAINEL_GESTOR_CLINICA"))
                .andExpect(jsonPath("$.rota").value("/gestao/clinica"))
                .andExpect(jsonPath("$.cpf").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("Recusando entrada com senha inválida")
    void recusandoEntradaComSenhaInvalida() throws Exception {
        criarClinica("22334455000177");
        criarUsuarioGestorClinica("22334455011", "gestor.senha.invalida@clinica.local", "22334455000177");

        AcessoRequestDTO request = new AcessoRequestDTO("gestor.senha.invalida@clinica.local", "SenhaErrada@123");

        mockMvc.perform(post("/acesso/entrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas."))
                .andExpect(jsonPath("$.correlationId", startsWith("")))
                .andExpect(jsonPath("$.message", not("SenhaErrada@123")));
    }

    private void criarClinica(String identificadorFiscal) throws Exception {
        ClinicaRequestDTO request = new ClinicaRequestDTO(
                identificadorFiscal,
                "Clínica Acesso",
                TipoPessoa.JURIDICA,
                null,
                TipoClinica.EMPRESA
        );

        mockMvc.perform(post("/clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void criarUsuarioGestorClinica(String cpf, String email, String clinicaId) throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                cpf,
                "Gestor Acesso",
                LocalDate.of(1990, 1, 1),
                email,
                "Senha@123",
                PerfilRoot.GESTOR_CLINICA,
                clinicaId,
                "Gestor da Clínica",
                null,
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
