package psicologia.clinica.acesso.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
import psicologia.clinica.usuario.model.SubPerfil;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para JWT, refresh token, segundo fator e RBAC")
class AutenticandoComJwtRefreshSegundoFatorERbacIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Autenticando gestor com segundo fator, rotacionando refresh token e aplicando RBAC")
    void autenticandoGestorComSegundoFatorRefreshTokenERbac() throws Exception {
        criarClinica("55667788000111");
        criarUsuario("55667788000", "gestor.jwt@clinica.local", PerfilRoot.GESTOR_CLINICA, null, "55667788000111");

        JsonNode inicio = entrar("gestor.jwt@clinica.local", "Senha@123");

        assertThat(inicio.get("segundoFatorObrigatorio").asBoolean()).isTrue();
        assertThat(inicio.get("desafioSegundoFatorId").asText()).isNotBlank();
        assertThat(inicio.get("codigoSegundoFatorDev").asText()).hasSize(6);
        assertThat(inicio.hasNonNull("accessToken")).isFalse();
        assertThat(inicio.hasNonNull("refreshToken")).isFalse();

        JsonNode tokens = confirmarSegundoFator(
                inicio.get("desafioSegundoFatorId").asText(),
                inicio.get("codigoSegundoFatorDev").asText()
        );

        String accessToken = tokens.get("accessToken").asText();
        String refreshToken = tokens.get("refreshToken").asText();

        assertThat(accessToken).startsWith("ey");
        assertThat(refreshToken).isNotBlank();

        mockMvc.perform(get("/gestao/clinica")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("<h1>GESTOR_CLINICA</h1>"));

        mockMvc.perform(get("/atendimento/pacientes")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

        JsonNode novosTokens = refresh(refreshToken);

        assertThat(novosTokens.get("accessToken").asText()).startsWith("ey");
        assertThat(novosTokens.get("refreshToken").asText()).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("Recusando acesso a rota protegida sem JWT")
    void recusandoAcessoRotaProtegidaSemJwt() throws Exception {
        mockMvc.perform(get("/gestao/clinica"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Recusando confirmação de segundo fator com código inválido")
    void recusandoSegundoFatorComCodigoInvalido() throws Exception {
        criarClinica("55667788000122");
        criarUsuario("55667788011", "gestor.2fa.invalido@clinica.local", PerfilRoot.GESTOR_CLINICA, null, "55667788000122");

        JsonNode inicio = entrar("gestor.2fa.invalido@clinica.local", "Senha@123");

        mockMvc.perform(post("/acesso/confirmar-2fa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "desafioId": "%s",
                                  "codigo": "000000"
                                }
                                """.formatted(inicio.get("desafioSegundoFatorId").asText())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", startsWith("Código de segundo fator inválido")));
    }

    private JsonNode entrar(String email, String senha) throws Exception {
        AcessoRequestDTO request = new AcessoRequestDTO(email, senha);

        String response = mockMvc.perform(post("/acesso/entrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private JsonNode confirmarSegundoFator(String desafioId, String codigo) throws Exception {
        String response = mockMvc.perform(post("/acesso/confirmar-2fa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "desafioId": "%s",
                                  "codigo": "%s"
                                }
                                """.formatted(desafioId, codigo)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private JsonNode refresh(String refreshToken) throws Exception {
        String response = mockMvc.perform(post("/acesso/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response);
    }

    private void criarClinica(String identificadorFiscal) throws Exception {
        ClinicaRequestDTO request = new ClinicaRequestDTO(
                identificadorFiscal,
                "Clínica JWT",
                TipoPessoa.JURIDICA,
                null,
                TipoClinica.EMPRESA
        );

        mockMvc.perform(post("/clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private void criarUsuario(String cpf, String email, PerfilRoot perfilRoot, SubPerfil subPerfil, String clinicaId) throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                cpf,
                "Usuário JWT",
                LocalDate.of(1990, 1, 1),
                email,
                "Senha@123",
                perfilRoot,
                clinicaId,
                perfilRoot == PerfilRoot.GESTOR_CLINICA ? "Gestor da Clínica" : null,
                subPerfil,
                subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "CRP" : null,
                subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "JWT-0001" : null,
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
