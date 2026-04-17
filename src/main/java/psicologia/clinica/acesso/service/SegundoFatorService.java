package psicologia.clinica.acesso.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.acesso.exception.SegundoFatorInvalidoException;
import psicologia.clinica.acesso.model.SegundoFatorDesafio;
import psicologia.clinica.acesso.repository.SegundoFatorDesafioRepository;
import psicologia.clinica.acesso.security.TokenHashService;
import psicologia.clinica.infrastructure.config.env.DotEnvReader;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SegundoFatorService {

    private static final int CODIGO_LIMITE_EXCLUSIVO = 1_000_000;
    private static final long DESAFIO_EXPIRA_EM_SEGUNDOS = 300;
    private static final String DEV_2FA_CODE_KEY = "DEV_2FA_CODE";

    private final SegundoFatorDesafioRepository desafioRepository;
    private final TokenHashService tokenHashService;
    private final Environment environment;
    private final SecureRandom secureRandom = new SecureRandom();

    public boolean obrigatorio(PerfilRoot perfilRoot, SubPerfil subPerfil) {
        return perfilRoot == PerfilRoot.GESTOR_SISTEMA
                || perfilRoot == PerfilRoot.GESTOR_CLINICA
                || subPerfil == SubPerfil.PROFISSIONAL_SAUDE;
    }

    @Transactional
    public DesafioSegundoFator iniciar(Usuario usuario, String ip) {
        String codigo = codigo();
        LocalDateTime agora = LocalDateTime.now();

        SegundoFatorDesafio desafio = desafioRepository.save(SegundoFatorDesafio.builder()
                .id(UUID.randomUUID())
                .usuarioCpf(usuario.getCpf())
                .codigoHash(tokenHashService.sha256(codigo))
                .criadoEm(agora)
                .expiraEm(agora.plusSeconds(DESAFIO_EXPIRA_EM_SEGUNDOS))
                .ip(ip)
                .build());

        return new DesafioSegundoFator(desafio.getId(), codigoDev(codigo));
    }

    @Transactional
    public SegundoFatorDesafio confirmar(String desafioId, String codigo) {
        LocalDateTime agora = LocalDateTime.now();
        UUID id = parseUuid(desafioId);
        SegundoFatorDesafio desafio = desafioRepository.findById(id)
                .orElseThrow(SegundoFatorInvalidoException::new);

        if (desafio.usado()
                || desafio.expirado(agora)
                || !tokenHashService.sha256(codigo).equals(desafio.getCodigoHash())) {
            throw new SegundoFatorInvalidoException();
        }

        desafio.setUsadoEm(agora);
        return desafio;
    }

    private String codigo() {
        return Optional.ofNullable(environment.getProperty(DEV_2FA_CODE_KEY))
                .filter(value -> value.matches("\\d{6}"))
                .or(() -> DotEnvReader.read(DEV_2FA_CODE_KEY).filter(value -> value.matches("\\d{6}")))
                .orElseGet(() -> "%06d".formatted(secureRandom.nextInt(CODIGO_LIMITE_EXCLUSIVO)));
    }

    private String codigoDev(String codigo) {
        boolean dev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        return dev ? codigo : null;
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new SegundoFatorInvalidoException();
        }
    }

    public record DesafioSegundoFator(UUID id, String codigoDev) {
    }
}
