package psicologia.clinica.acesso.exception;

public class SegundoFatorInvalidoException extends RuntimeException {

    public SegundoFatorInvalidoException() {
        super("Código de segundo fator inválido ou expirado.");
    }
}
