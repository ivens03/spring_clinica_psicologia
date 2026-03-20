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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class ClinicaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /clinicas - Deve criar uma clínica com sucesso")
    void deveCriarClinica() throws Exception {
        ClinicaRequestDTO requestDTO = new ClinicaRequestDTO(
                "45678912300", "Clínica Vida Ativa", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);

        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identificadorFiscal").value("45678912300"))
                .andExpect(jsonPath("$.nomeExibicao").value("Clínica Vida Ativa"))
                .andExpect(jsonPath("$.versao").doesNotExist());
    }

    @Test
    @DisplayName("PUT /clinicas/{id} - Deve atualizar clínica com sucesso")
    void deveAtualizarClinica() throws Exception {
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

    @Test
    @DisplayName("DELETE /clinicas/{id} - Deve remover clínica com sucesso")
    void deveRemoverClinica() throws Exception {
        ClinicaRequestDTO createDTO = new ClinicaRequestDTO(
                "99988877766", "Clinica Deletar", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);
        mockMvc.perform(post("/clinicas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDTO)));

        mockMvc.perform(delete("/clinicas/99988877766"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clinicas/99988877766"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /clinicas - Deve falhar com identificador de tamanho inválido")
    void deveFalharTamanhoIdentificadorInvalido() throws Exception {
        ClinicaRequestDTO requestDTO = new ClinicaRequestDTO(
                "123", "Clínica Erro", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);

        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Erro de validação")));
    }

    @Test
    @DisplayName("GET /v3/api-docs - Deve retornar a documentação OpenAPI com sucesso")
    void deveRetornarDocumentacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Clínica de Psicologia API"))
                .andExpect(jsonPath("$.paths['/clinicas']").exists());
    }

    @Test
    @DisplayName("GET /clinicas/{id} - Deve retornar 404 para clínica inexistente")
    void deveRetornar404ParaClinicaInexistente() throws Exception {
        mockMvc.perform(get("/clinicas/00000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Clínica não encontrada com o identificador: 00000000000"));
    }
}
