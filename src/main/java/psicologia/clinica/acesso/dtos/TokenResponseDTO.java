package psicologia.clinica.acesso.dtos;

public record TokenResponseDTO(
        String tokenType,
        String accessToken,
        long accessTokenExpiraEmSegundos,
        String refreshToken,
        long refreshTokenExpiraEmSegundos,
        String rota
) {
}
