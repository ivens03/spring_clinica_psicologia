package psicologia.clinica.clinica.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClinicaRepositoryTest {

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    @Test
    @DisplayName("Deve persistir uma clínica com sucesso usando identificador fiscal como PK")
    void devePersistirClinicaComSucesso() {
        // Arrange
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("12345678901")
                .nomeExibicao("Consultório do Dr. Ivens")
                .tipoPessoa(TipoPessoa.FISICA)
                .tipoClinica(TipoClinica.EMPRESA)
                .build();

        // Act
        Clinica persistida = clinicaRepository.save(clinica);
        entityManager.flush(); // Força a sincronização com o banco para preencher campos de auditoria

        // Assert
        assertThat(persistida.getIdentificadorFiscal()).isEqualTo("12345678901");
        assertThat(persistida.getCriadoEm()).isNotNull();
        assertThat(persistida.getVersao()).isZero();
    }

    @Test
    @DisplayName("Deve disparar exceção ao tentar atualizar registro com versão desatualizada (Optimistic Locking)")
    void deveDispararExcecaoOptimisticLocking() {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("55566677788")
                .nomeExibicao("Clinica Lock")
                .tipoPessoa(TipoPessoa.FISICA)
                .tipoClinica(TipoClinica.EMPRESA)
                .build();
        clinicaRepository.save(clinica);
        entityManager.flush();
        entityManager.clear(); // Limpa o cache para forçar carregamento do banco

        Clinica instancia1 = clinicaRepository.findById("55566677788").get();
        Clinica instancia2 = clinicaRepository.findById("55566677788").get();

        // Simula atualização pela instancia 1
        instancia1.setNomeExibicao("Nome Atualizado 1");
        clinicaRepository.save(instancia1);
        entityManager.flush(); // Aqui a versão no banco sobe para 1

        // Tenta atualizar pela instancia 2 que ainda tem versão 0
        instancia2.setNomeExibicao("Nome Atualizado 2");
        
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
            clinicaRepository.save(instancia2);
            entityManager.flush(); // Deve falhar aqui ao sincronizar com o banco
        }).isInstanceOf(org.springframework.orm.ObjectOptimisticLockingFailureException.class);
    }
}
