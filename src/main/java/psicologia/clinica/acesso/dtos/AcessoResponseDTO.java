package psicologia.clinica.acesso.dtos;

import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;

public record AcessoResponseDTO(
        String nome,
        String email,
        PerfilRoot perfilRoot,
        SubPerfil subPerfil,
        String direcionamento,
        String rota,
        boolean segundoFatorObrigatorio,
        String desafioSegundoFatorId,
        String codigoSegundoFatorDev,
        String tokenType,
        String accessToken,
        long accessTokenExpiraEmSegundos,
        String refreshToken,
        long refreshTokenExpiraEmSegundos
) {
    public static AcessoResponseDTO aguardandoSegundoFator(Usuario usuario, SubPerfil subPerfil,
                                                           DirecionamentoAcessoDTO direcionamento,
                                                           String desafioId,
                                                           String codigoSegundoFatorDev) {
        return new AcessoResponseDTO(
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getPerfilRoot(),
                subPerfil,
                direcionamento.nome(),
                direcionamento.rota(),
                true,
                desafioId,
                codigoSegundoFatorDev,
                null,
                null,
                0,
                null,
                0
        );
    }

    public static AcessoResponseDTO autenticado(Usuario usuario, SubPerfil subPerfil,
                                                DirecionamentoAcessoDTO direcionamento,
                                                TokenResponseDTO tokens) {
        return new AcessoResponseDTO(
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getPerfilRoot(),
                subPerfil,
                direcionamento.nome(),
                direcionamento.rota(),
                false,
                null,
                null,
                tokens.tokenType(),
                tokens.accessToken(),
                tokens.accessTokenExpiraEmSegundos(),
                tokens.refreshToken(),
                tokens.refreshTokenExpiraEmSegundos()
        );
    }
}
