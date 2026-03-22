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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para criação de clínicas")
class CriandoClinicaIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Criando uma clínica com sucesso")
    void criandoClinicaComSucesso() throws Exception {
        ClinicaRequestDTO requestDTO = new ClinicaRequestDTO(
                "45678912300", "Clínica Vida Ativa", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);

        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identificadorFiscal").value("45678912300"))
                .andExpect(jsonPath("$.nomeExibicao").value("Clínica Vida Ativa"));
    }

    @Test
    @DisplayName("Falhando ao criar clínica com identificador de tamanho inválido")
    void falhandoComTamanhoIdentificadorInvalido() throws Exception {
        ClinicaRequestDTO requestDTO = new ClinicaRequestDTO(
                "123", "Clínica Erro", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);

        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Erro de validação")));
    }
}
