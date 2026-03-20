package psicologia.clinica.clinica.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.exception.BusinessException;
import psicologia.clinica.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    @Transactional
    public Clinica salvar(Clinica clinica) {
        validarIdentificador(clinica.getIdentificadorFiscal());
        if (clinicaRepository.existsById(clinica.getIdentificadorFiscal())) {
            throw new BusinessException("Clínica já cadastrada com este identificador fiscal.");
        }
        return clinicaRepository.save(clinica);
    }

    @Transactional(readOnly = true)
    public List<Clinica> listarTodos() {
        return clinicaRepository.findAll();
    }

    @Transactional
    public Clinica atualizar(String id, Clinica dadosAtualizados) {
        Clinica clinicaExistente = buscarPorId(id);
        
        clinicaExistente.setNomeExibicao(dadosAtualizados.getNomeExibicao());
        clinicaExistente.setTipoPessoa(dadosAtualizados.getTipoPessoa());
        clinicaExistente.setTipoClinica(dadosAtualizados.getTipoClinica());
        clinicaExistente.setRegistroConselhoClinica(dadosAtualizados.getRegistroConselhoClinica());
        clinicaExistente.setAtivo(dadosAtualizados.isAtivo());

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
