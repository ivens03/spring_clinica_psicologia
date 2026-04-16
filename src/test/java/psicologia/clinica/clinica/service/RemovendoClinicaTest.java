package psicologia.clinica.clinica.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.exception.exception.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de serviço para remoção de clínicas")
class RemovendoClinicaTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ClinicaService clinicaService;

    @Test
    @DisplayName("Removendo clínica com sucesso (Soft Delete)")
    void removendoClinicaComSucesso() {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("123")
                .ativo(true)
                .build();
        
        when(clinicaRepository.findById("123")).thenReturn(java.util.Optional.of(clinica));
        when(clinicaRepository.save(any(Clinica.class))).thenReturn(clinica);

        clinicaService.deletar("123");

        verify(clinicaRepository, times(1)).save(argThat(c -> !c.isAtivo() && c.getExcluidoEm() != null));
    }

    @Test
    @DisplayName("Lançando exceção ao remover clínica inexistente")
    void lancandoExcecaoAoRemoverInexistente() {
        when(clinicaRepository.findById("123")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> clinicaService.deletar("123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
