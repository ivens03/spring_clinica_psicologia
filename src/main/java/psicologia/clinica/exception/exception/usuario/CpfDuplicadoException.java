package psicologia.clinica.exception.exception.usuario;

import psicologia.clinica.exception.exception.BusinessException;

public class CpfDuplicadoException extends BusinessException {

    public CpfDuplicadoException(String cpf) {
        super("Já existe um usuário cadastrado com o CPF informado: " + cpf);
    }
}
