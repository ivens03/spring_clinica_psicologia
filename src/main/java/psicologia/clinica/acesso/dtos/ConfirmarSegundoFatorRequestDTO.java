package psicologia.clinica.acesso.dtos;

import jakarta.validation.constraints.NotBlank;

public record ConfirmarSegundoFatorRequestDTO(
        @NotBlank String desafioId,
        @NotBlank String codigo
) {
}
