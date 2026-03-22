package psicologia.clinica.clinica.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para remoção de clínicas")
class RemovendoClinicaIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Removendo clínica com sucesso")
    void removendoClinicaComSucesso() throws Exception {
        ClinicaRequestDTO createDTO = new ClinicaRequestDTO(
                "99988877766", "Clinica Deletar", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);
        mockMvc.perform(post("/clinicas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDTO)));

        mockMvc.perform(delete("/clinicas/99988877766"))
                .andExpect(status().isNoContent());
    }
}
