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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de serviço para busca de clínicas")
class BuscandoClinicaPorIdTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ClinicaService clinicaService;

    @Test
    @DisplayName("Buscando uma clínica por id com sucesso")
    void buscandoPorIdComSucesso() {
        Clinica clinica = Clinica.builder().identificadorFiscal("123").build();
        when(clinicaRepository.findById("123")).thenReturn(Optional.of(clinica));

        Clinica encontrada = clinicaService.buscarPorId("123");

        assertThat(encontrada.getIdentificadorFiscal()).isEqualTo("123");
    }

    @Test
    @DisplayName("Lançando exceção ao buscar clínica inexistente")
    void lancandoExcecaoAoBuscarInexistente() {
        when(clinicaRepository.findById("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicaService.buscarPorId("123"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
