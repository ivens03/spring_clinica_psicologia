package psicologia.clinica.acesso.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para exibição do login")
class ExibindoLoginIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Exibindo tela única de entrada")
    void exibindoTelaUnicaDeEntrada() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Entrar na clínica")))
                .andExpect(content().string(containsString("name=\"email\"")))
                .andExpect(content().string(containsString("name=\"senha\"")))
                .andExpect(content().string(containsString("Atendimento com sigilo")));
    }
}
