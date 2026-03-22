package psicologia.clinica.clinica.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @DisplayName("Removendo clínica com sucesso")
    void removendoClinicaComSucesso() {
        when(clinicaRepository.existsById("123")).thenReturn(true);
        doNothing().when(clinicaRepository).deleteById("123");

        clinicaService.deletar("123");

        verify(clinicaRepository, times(1)).deleteById("123");
    }

    @Test
    @DisplayName("Lançando exceção ao remover clínica inexistente")
    void lancandoExcecaoAoRemoverInexistente() {
        when(clinicaRepository.existsById("123")).thenReturn(false);

        assertThatThrownBy(() -> clinicaService.deletar("123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
