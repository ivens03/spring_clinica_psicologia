package psicologia.clinica.acesso.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank String refreshToken
) {
}
