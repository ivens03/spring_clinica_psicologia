package psicologia.clinica.clinica.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.clinica.repository.ClinicaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de serviço para salvamento de clínicas")
class SalvandoClinicaTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ClinicaService clinicaService;

    @Test
    @DisplayName("Salvando uma nova clínica com sucesso")
    void salvandoClinicaComSucesso() {
        ClinicaRequestDTO request = new ClinicaRequestDTO(
                "12345678000199", "Clínica Central", TipoPessoa.JURIDICA, null, TipoClinica.EMPRESA);

        Clinica clinicaMock = Clinica.builder()
                .identificadorFiscal(request.identificadorFiscal())
                .nomeExibicao(request.nomeExibicao())
                .tipoPessoa(request.tipoPessoa())
                .tipoClinica(request.tipoClinica())
                .build();

        when(clinicaRepository.existsById(anyString())).thenReturn(false);
        when(clinicaRepository.save(any(Clinica.class))).thenReturn(clinicaMock);

        Clinica salva = clinicaService.salvar(request);

        assertThat(salva).isNotNull();
        verify(clinicaRepository, times(1)).save(any(Clinica.class));
    }
}
