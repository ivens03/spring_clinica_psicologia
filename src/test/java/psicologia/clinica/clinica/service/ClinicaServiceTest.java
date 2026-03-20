package psicologia.clinica.clinica.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.exception.BusinessException;
import psicologia.clinica.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaServiceTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ClinicaService clinicaService;

    @Test
    @DisplayName("Deve salvar uma nova clínica com sucesso")
    void deveSalvarClinicaComSucesso() {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal("12345678000199")
                .nomeExibicao("Clínica Central")
                .tipoPessoa(TipoPessoa.JURIDICA)
                .tipoClinica(TipoClinica.EMPRESA)
                .build();

        when(clinicaRepository.existsById(anyString())).thenReturn(false);
        when(clinicaRepository.save(any(Clinica.class))).thenReturn(clinica);

        Clinica salva = clinicaService.salvar(clinica);

        assertThat(salva).isNotNull();
        verify(clinicaRepository, times(1)).save(clinica);
    }

    @Test
    @DisplayName("Deve buscar uma clínica por id com sucesso")
    void deveBuscarPorIdComSucesso() {
        Clinica clinica = Clinica.builder().identificadorFiscal("123").build();
        when(clinicaRepository.findById("123")).thenReturn(Optional.of(clinica));

        Clinica encontrada = clinicaService.buscarPorId("123");

        assertThat(encontrada.getIdentificadorFiscal()).isEqualTo("123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar clínica inexistente")
    void deveLancarExcecaoAoBuscarInexistente() {
        when(clinicaRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicaService.buscarPorId("123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve deletar clínica com sucesso")
    void deveDeletarClinicaComSucesso() {
        when(clinicaRepository.existsById("123")).thenReturn(true);
        doNothing().when(clinicaRepository).deleteById("123");

        clinicaService.deletar("123");

        verify(clinicaRepository, times(1)).deleteById("123");
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar clínica inexistente")
    void deveLancarExcecaoAoDeletarInexistente() {
        when(clinicaRepository.existsById("123")).thenReturn(false);

        assertThatThrownBy(() -> clinicaService.deletar("123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
