package psicologia.clinica.clinica.dtos;

import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;

public record ClinicaResponseDTO(
    String identificadorFiscal,
    String nomeExibicao,
    TipoPessoa tipoPessoa,
    String registroConselhoClinica,
    TipoClinica tipoClinica,
    boolean ativo
) {
    public static ClinicaResponseDTO fromEntity(Clinica clinica) {
        return new ClinicaResponseDTO(
            clinica.getIdentificadorFiscal(),
            clinica.getNomeExibicao(),
            clinica.getTipoPessoa(),
            clinica.getRegistroConselhoClinica(),
            clinica.getTipoClinica(),
            clinica.isAtivo()
        );
    }
}
