package psicologia.clinica.clinica.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;

public record ClinicaRequestDTO(
    @NotBlank @Size(min = 11, max = 14)
    String identificadorFiscal,
    
    @NotBlank
    String nomeExibicao,
    
    @NotNull
    TipoPessoa tipoPessoa,
    
    String registroConselhoClinica,
    
    @NotNull
    TipoClinica tipoClinica
) {}
