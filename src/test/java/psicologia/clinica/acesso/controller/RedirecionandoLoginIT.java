package psicologia.clinica.acesso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;

import java.time.LocalDate;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para redirecionamento do login")
class RedirecionandoLoginIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Redirecionando gestor da clínica para tela administrativa")
    void redirecionandoGestorClinicaParaTelaAdministrativa() throws Exception {
        criarClinica("33445566000188");
        criarUsuario("33445566000", "gestor.web@clinica.local", PerfilRoot.GESTOR_CLINICA, null, "33445566000188");

        MvcResult login = mockMvc.perform(post("/login")
                        .param("email", "gestor.web@clinica.local")
                        .param("senha", "Senha@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login/2fa?desafio=*&codigoDev=123456"))
                .andReturn();

        Cookie accessToken = confirmarSegundoFator(login, "/gestao/clinica");

        mockMvc.perform(get("/gestao/clinica")
                        .cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>GESTOR_CLINICA</h1>")));
    }

    @Test
    @DisplayName("Redirecionando profissional de saúde para tela de atendimento")
    void redirecionandoProfissionalSaudeParaTelaAtendimento() throws Exception {
        criarClinica("33445566000199");
        criarUsuario("33445566011", "profissional.web@clinica.local", PerfilRoot.FUNCIONARIO,
                SubPerfil.PROFISSIONAL_SAUDE, "33445566000199");

        MvcResult login = mockMvc.perform(post("/login")
                        .param("email", "profissional.web@clinica.local")
                        .param("senha", "Senha@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login/2fa?desafio=*&codigoDev=123456"))
                .andReturn();

        Cookie accessToken = confirmarSegundoFator(login, "/atendimento/pacientes");

        mockMvc.perform(get("/atendimento/pacientes")
                        .cookie(accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1>PROFISSIONAL_SAUDE</h1>")));
    }

    @Test
    @DisplayName("Retornando para login quando credenciais são inválidas")
    void retornandoParaLoginQuandoCredenciaisInvalidas() throws Exception {
        mockMvc.perform(post("/login")
                        .param("email", "inexistente@clinica.local")
                        .param("senha", "SenhaErrada@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?erro"));
    }

    private void criarClinica(String identificadorFiscal) throws Exception {
        ClinicaRequestDTO request = new ClinicaRequestDTO(
                identificadorFiscal,
                "Clínica Login",
                TipoPessoa.JURIDICA,
                null,
                TipoClinica.EMPRESA
        );

        mockMvc.perform(post("/clinicas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private Cookie confirmarSegundoFator(MvcResult login, String destino) throws Exception {
        String location = login.getResponse().getRedirectedUrl();
        String desafio = parametro(location, "desafio");

        MvcResult segundoFator = mockMvc.perform(post("/login/2fa")
                        .param("desafioId", desafio)
                        .param("codigo", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(destino))
                .andReturn();

        return Arrays.stream(segundoFator.getResponse().getCookies())
                .filter(cookie -> "ACCESS_TOKEN".equals(cookie.getName()))
                .findFirst()
                .orElseThrow();
    }

    private String parametro(String location, String nome) {
        String query = URI.create(location).getQuery();
        return Arrays.stream(query.split("&"))
                .map(parametro -> parametro.split("=", 2))
                .filter(partes -> partes.length == 2 && nome.equals(partes[0]))
                .map(partes -> URLDecoder.decode(partes[1], StandardCharsets.UTF_8))
                .findFirst()
                .orElseThrow();
    }

    private void criarUsuario(String cpf, String email, PerfilRoot perfilRoot, SubPerfil subPerfil, String clinicaId) throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO(
                cpf,
                "Usuário Login",
                LocalDate.of(1990, 1, 1),
                email,
                "Senha@123",
                perfilRoot,
                clinicaId,
                perfilRoot == PerfilRoot.GESTOR_CLINICA ? "Gestor da Clínica" : null,
                subPerfil,
                subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "CRP" : null,
                subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "LOGIN-0001" : null,
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
