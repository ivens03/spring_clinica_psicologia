package psicologia.clinica.clinica.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Testes de persistência de clínicas")
class PersistindoClinicaTest {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Persistindo uma clínica com sucesso usando identificador fiscal como chave primária")
    void persistindoClinicaComSucesso() {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("12345678901")
                .nomeExibicao("Consultório do Dr. Ivens")
                .tipoPessoa(TipoPessoa.FISICA)
                .tipoClinica(TipoClinica.EMPRESA)
                .build();

        Clinica persistida = clinicaRepository.save(clinica);
        entityManager.flush();

        assertThat(persistida.getIdentificadorFiscal()).isEqualTo("12345678901");
        assertThat(persistida.getCriadoEm()).isNotNull();
        assertThat(persistida.getVersao()).isZero();
    }
}
