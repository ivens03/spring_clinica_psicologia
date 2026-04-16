package psicologia.clinica.usuario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.repository.UsuarioRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Testes de integração para remoção de usuários")
class RemovendoUsuarioIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Removendo usuário com sucesso (Soft Delete)")
    void removendoUsuarioComSucesso() throws Exception {
        // Criar clínica e usuário
        String clinicaId = "12345678901";
        mockMvc.perform(post("/clinicas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ClinicaRequestDTO(
                        clinicaId, "Clínica Teste", TipoPessoa.FISICA, null, TipoClinica.EMPRESA))));

        String cpf = "11122233344";
        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO(
                cpf, "Usuário Teste", LocalDate.of(1990, 1, 1),
                "teste@clinica.com", "Senha@123", PerfilRoot.GESTOR_CLINICA, clinicaId,
                null, null, null, null, null, null, null
        );
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)));

        // Remover
        mockMvc.perform(delete("/usuarios/" + cpf))
                .andExpect(status().isNoContent());

        // Limpar cache
        entityManager.flush();
        entityManager.clear();

        // Verificar que não é encontrado via GET
        mockMvc.perform(get("/usuarios/" + cpf))
                .andExpect(status().isNotFound());

        // Verificar soft delete no banco (usando findById - deve vir empty por causa do @Where)
        var usuario = usuarioRepository.findById(cpf);
        org.assertj.core.api.Assertions.assertThat(usuario).isEmpty();
    }
}
