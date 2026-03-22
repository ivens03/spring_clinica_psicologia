package psicologia.clinica.clinica.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.exception.exception.BusinessException;
import psicologia.clinica.exception.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    @Transactional
    public Clinica salvar(ClinicaRequestDTO request) {
        validarIdentificador(request.identificadorFiscal());
        
        if (clinicaRepository.existsById(request.identificadorFiscal())) {
            throw new BusinessException("Clínica já cadastrada com este identificador fiscal.");
        }

        Clinica clinica = Clinica.builder()
                .identificadorFiscal(request.identificadorFiscal())
                .nomeExibicao(request.nomeExibicao())
                .tipoPessoa(request.tipoPessoa())
                .registroConselhoClinica(request.registroConselhoClinica())
                .tipoClinica(request.tipoClinica())
                .build();

        return clinicaRepository.save(clinica);
    }

    @Transactional(readOnly = true)
    public List<Clinica> listarTodos() {
        return clinicaRepository.findAll();
    }

    @Transactional
    public Clinica atualizar(String id, ClinicaRequestDTO request) {
        Clinica clinicaExistente = buscarPorId(id);
        
        clinicaExistente.setNomeExibicao(request.nomeExibicao());
        clinicaExistente.setTipoPessoa(request.tipoPessoa());
        clinicaExistente.setTipoClinica(request.tipoClinica());
        clinicaExistente.setRegistroConselhoClinica(request.registroConselhoClinica());

        return clinicaRepository.save(clinicaExistente);
    }

    @Transactional
    public void deletar(String id) {
        if (!clinicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não é possível deletar: Clínica não encontrada com o identificador: " + id);
        }
        clinicaRepository.deleteById(id);
    }

    public Clinica buscarPorId(String id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada com o identificador: " + id));
    }

    private void validarIdentificador(String identificador) {
        if (identificador == null || (identificador.length() != 11 && identificador.length() != 14)) {
            throw new BusinessException("Identificador fiscal inválido. Deve ter 11 (CPF) ou 14 (CNPJ) dígitos.");
        }
    }
}
