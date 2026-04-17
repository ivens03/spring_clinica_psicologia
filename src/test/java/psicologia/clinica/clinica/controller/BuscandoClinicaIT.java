package psicologia.clinica.clinica.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para busca de clínicas")
class BuscandoClinicaIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Retornando erro recurso não encontrado para clínica inexistente")
    void retornandoErroRecursoNaoEncontradoParaClinicaInexistente() throws Exception {
        mockMvc.perform(get("/clinicas/00000000000")
                        .with(user("gestor").roles("GESTOR_CLINICA")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Clínica não encontrada com o identificador: 00000000000"));
    }
}
