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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    @Autowired
    private psicologia.clinica.clinica.repository.ClinicaRepository clinicaRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    @DisplayName("Removendo clínica com sucesso (Soft Delete)")
    void removendoClinicaComSucesso() throws Exception {
        String id = "99988877766";
        ClinicaRequestDTO createDTO = new ClinicaRequestDTO(
                id, "Clinica Deletar", TipoPessoa.FISICA, null, TipoClinica.EMPRESA);
        mockMvc.perform(post("/clinicas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/clinicas/" + id)
                        .with(user("gestor").roles("GESTOR_CLINICA")))
                .andExpect(status().isNoContent());

        // Limpar o contexto de persistência para garantir que a próxima busca vá ao banco
        entityManager.flush();
        entityManager.clear();

        // Verificar que não é mais encontrada via GET (por causa do @Where e busca no service)
        mockMvc.perform(get("/clinicas/" + id)
                        .with(user("gestor").roles("GESTOR_CLINICA")))
                .andExpect(status().isNotFound());
    }
}
