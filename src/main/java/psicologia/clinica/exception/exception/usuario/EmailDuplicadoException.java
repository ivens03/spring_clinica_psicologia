package psicologia.clinica.exception.exception.usuario;

import psicologia.clinica.exception.exception.BusinessException;

public class EmailDuplicadoException extends BusinessException {

    public EmailDuplicadoException(String email) {
        super("Já existe um usuário cadastrado com o e-mail informado: " + email);
    }
}
