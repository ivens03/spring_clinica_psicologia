package psicologia.clinica.clinica.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Testes de integração para documentação da API")
class DocumentacaoAPIIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Retornando a documentação OpenAPI com sucesso")
    void retornandoDocumentacaoOpenApiComSucesso() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Clínica de Psicologia API"))
                .andExpect(jsonPath("$.paths['/clinicas']").exists());
    }
}
