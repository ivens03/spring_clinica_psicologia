package psicologia.clinica.usuario.dtos;

import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.Usuario;

import java.time.LocalDate;

public record UsuarioResponseDTO(
    String cpf,
    String nome,
    LocalDate dataNascimento,
    String email,
    PerfilRoot perfilRoot,
    psicologia.clinica.usuario.model.SubPerfil subPerfil,
    String clinicaId,
    boolean ativo
) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return fromEntity(usuario, null);
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario, psicologia.clinica.usuario.model.SubPerfil subPerfil) {
        return new UsuarioResponseDTO(
            usuario.getCpf(),
            usuario.getNomeCompleto(),
            usuario.getDataNascimento(),
            usuario.getEmail(),
            usuario.getPerfilRoot(),
            subPerfil,
            usuario.getClinica().getIdentificadorFiscal(),
            usuario.isAtivo()
        );
    }
}
