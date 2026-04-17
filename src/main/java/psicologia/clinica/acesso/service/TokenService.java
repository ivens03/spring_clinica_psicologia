package psicologia.clinica.acesso.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.acesso.dtos.TokenResponseDTO;
import psicologia.clinica.acesso.exception.RefreshTokenInvalidoException;
import psicologia.clinica.acesso.model.RefreshToken;
import psicologia.clinica.acesso.repository.RefreshTokenRepository;
import psicologia.clinica.acesso.security.JwtService;
import psicologia.clinica.acesso.security.TokenHashService;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;
import psicologia.clinica.usuario.repository.FuncionarioRepository;
import psicologia.clinica.usuario.repository.UsuarioRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    public static final long REFRESH_TOKEN_SECONDS = 2_592_000;

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final TokenHashService tokenHashService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public TokenResponseDTO emitir(Usuario usuario, SubPerfil subPerfil, String ip, String rota) {
        String accessToken = jwtService.emitir(usuario, subPerfil);
        String refreshToken = gerarTokenSeguro();
        LocalDateTime agora = LocalDateTime.now();

        refreshTokenRepository.save(RefreshToken.builder()
                .id(UUID.randomUUID())
                .usuarioCpf(usuario.getCpf())
                .tokenHash(tokenHashService.sha256(refreshToken))
                .emitidoEm(agora)
                .expiraEm(agora.plusSeconds(REFRESH_TOKEN_SECONDS))
                .ip(ip)
                .build());

        return new TokenResponseDTO(
                "Bearer",
                accessToken,
                JwtService.ACCESS_TOKEN_SECONDS,
                refreshToken,
                REFRESH_TOKEN_SECONDS,
                rota
        );
    }

    @Transactional
    public TokenResponseDTO refresh(String refreshToken, String ip, String rota) {
        LocalDateTime agora = LocalDateTime.now();
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHashService.sha256(refreshToken))
                .orElseThrow(RefreshTokenInvalidoException::new);

        if (token.revogado() || token.expirado(agora)) {
            throw new RefreshTokenInvalidoException();
        }

        token.setRevogadoEm(agora);

        Usuario usuario = usuarioRepository.findById(token.getUsuarioCpf())
                .orElseThrow(RefreshTokenInvalidoException::new);
        SubPerfil subPerfil = funcionarioRepository.findById(usuario.getCpf())
                .map(funcionario -> funcionario.getSubPerfil())
                .orElse(null);

        return emitir(usuario, subPerfil, ip, rota);
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
