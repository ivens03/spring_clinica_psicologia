package psicologia.clinica.acesso.exception;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais inválidas.");
    }
}
