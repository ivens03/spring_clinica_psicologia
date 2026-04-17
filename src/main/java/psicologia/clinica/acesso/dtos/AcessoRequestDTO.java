package psicologia.clinica.acesso.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AcessoRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}
