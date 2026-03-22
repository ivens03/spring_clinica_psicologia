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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Testes de controle de concorrência de clínicas")
class ValidandoConcorrenciaClinicaTest {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Lançando exceção ao tentar atualizar registro com versão desatualizada")
    void lancandoExcecaoComVersaoDesatualizada() {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("55566677788")
                .nomeExibicao("Clinica Lock")
                .tipoPessoa(TipoPessoa.FISICA)
                .tipoClinica(TipoClinica.EMPRESA)
                .build();
        clinicaRepository.save(clinica);
        entityManager.flush();
        entityManager.clear();

        Clinica clinicaDesatualizada = clinicaRepository.findById("55566677788").orElseThrow();
        entityManager.detach(clinicaDesatualizada);

        Clinica clinicaAtualizada = clinicaRepository.findById("55566677788").orElseThrow();

        clinicaAtualizada.setNomeExibicao("Nome Atualizado 2");
        clinicaRepository.save(clinicaAtualizada);
        entityManager.flush();

        clinicaDesatualizada.setNomeExibicao("Nome Atualizado 1");
        
        assertThatThrownBy(() -> {
            clinicaRepository.save(clinicaDesatualizada);
            entityManager.flush();
        }).isInstanceOf(org.springframework.orm.ObjectOptimisticLockingFailureException.class);
    }
}
