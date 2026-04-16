package psicologia.clinica.usuario.controller;

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
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para criação de usuários")
class CriandoUsuarioIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private psicologia.clinica.infrastructure.auditoria.repository.AcessoRepository acessoRepository;

    @Test
    @DisplayName("Criando usuário gestor com sucesso")
    void criandoUsuarioGestorComSucesso() throws Exception {
        // Primeiro criar uma clínica
        String clinicaId = "12345678901";
        ClinicaRequestDTO clinicaDTO = new ClinicaRequestDTO(
                clinicaId, "Clínica Teste", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);
        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clinicaDTO)))
                .andExpect(status().isCreated());

        // Criar usuário vinculado à clínica
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO(
                "11122233344",
                "Usuário Teste",
                LocalDate.of(1990, 1, 1),
                "teste@clinica.com",
                "Senha@123",
                PerfilRoot.GESTOR_CLINICA,
                clinicaId,
                "Dono", null, null, null, null, null, null
        );

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("11122233344"))
                .andExpect(jsonPath("$.nome").value("Usuário Teste"))
                .andExpect(jsonPath("$.perfilRoot").value("GESTOR_CLINICA"));
        
        // Verificar log
        org.assertj.core.api.Assertions.assertThat(acessoRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("Criando usuário profissional de saúde com sucesso")
    void criandoUsuarioProfissionalComSucesso() throws Exception {
        String clinicaId = "99887766554";
        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ClinicaRequestDTO(
                        clinicaId, "Clínica Saúde", TipoPessoa.FISICA, null, TipoClinica.EMPRESA))));

        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO(
                "99944455522",
                "Doutor Teste",
                LocalDate.of(1985, 5, 20),
                "doutor@clinica.com",
                "Senha@123",
                PerfilRoot.GESTOR_CLINICA, // Gestor também pode ser profissional
                clinicaId,
                null,
                SubPerfil.PROFISSIONAL_SAUDE,
                "CRP",
                "12345",
                null, null, null
        );

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("99944455522"))
                .andExpect(jsonPath("$.subPerfil").value("PROFISSIONAL_SAUDE"));
    }
}
