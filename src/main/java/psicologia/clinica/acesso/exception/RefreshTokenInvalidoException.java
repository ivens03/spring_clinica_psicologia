package psicologia.clinica.acesso.exception;

public class RefreshTokenInvalidoException extends RuntimeException {

    public RefreshTokenInvalidoException() {
        super("Refresh token inválido ou expirado.");
    }
}
