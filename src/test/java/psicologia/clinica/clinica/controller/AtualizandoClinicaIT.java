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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para atualização de clínicas")
class AtualizandoClinicaIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Atualizando clínica com sucesso")
    void atualizandoClinicaComSucesso() throws Exception {
        ClinicaRequestDTO createDTO = new ClinicaRequestDTO(
                "11122233344", "Clínica Original", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);
        mockMvc.perform(post("/clinicas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDTO)));

        ClinicaRequestDTO updateDTO = new ClinicaRequestDTO(
                "11122233344", "Clínica Atualizada", TipoPessoa.JURIDICA, "CRP-123", TipoClinica.EMPRESA_ESCOLAR);

        mockMvc.perform(put("/clinicas/11122233344")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeExibicao").value("Clínica Atualizada"))
                .andExpect(jsonPath("$.registroConselhoClinica").value("CRP-123"));
    }
}
