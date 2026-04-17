package psicologia.clinica.acesso.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import psicologia.clinica.infrastructure.config.env.DotEnvReader;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    public static final long ACCESS_TOKEN_SECONDS = 900;
    private static final String JWT_SECRET_KEY = "JWT_SECRET";
    private static final String PASSWORD_PEPPER_KEY = "PASSWORD_PEPPER";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper;
    private final byte[] secret;

    public JwtService(ObjectMapper objectMapper, Environment environment) {
        this.objectMapper = objectMapper;
        this.secret = resolveSecret(environment).getBytes(StandardCharsets.UTF_8);
    }

    public String emitir(Usuario usuario, SubPerfil subPerfil) {
        Instant agora = Instant.now();
        List<String> roles = PerfilAuthorities.roles(usuario.getPerfilRoot(), subPerfil);

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", usuario.getCpf());
        payload.put("email", usuario.getEmail());
        payload.put("nome", usuario.getNomeCompleto());
        payload.put("clinicaId", usuario.getClinica().getIdentificadorFiscal());
        payload.put("roles", roles);
        payload.put("iat", agora.getEpochSecond());
        payload.put("exp", agora.plusSeconds(ACCESS_TOKEN_SECONDS).getEpochSecond());

        String unsignedToken = encode(header) + "." + encode(payload);
        return unsignedToken + "." + assinar(unsignedToken);
    }

    public Optional<JwtClaims> validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return Optional.empty();
            }

            String unsignedToken = partes[0] + "." + partes[1];
            if (!MessageDigestTimingSafe.equals(assinar(unsignedToken), partes[2])) {
                return Optional.empty();
            }

            Map<String, Object> payload = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(partes[1]),
                    new TypeReference<>() {
                    }
            );

            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() <= Instant.now().getEpochSecond()) {
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) payload.get("roles");

            return Optional.of(new JwtClaims(
                    (String) payload.get("sub"),
                    (String) payload.get("email"),
                    (String) payload.get("nome"),
                    (String) payload.get("clinicaId"),
                    roles == null ? List.of() : roles
            ));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String encode(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível serializar o JWT.", exception);
        }
    }

    private String assinar(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível assinar o JWT.", exception);
        }
    }

    private String resolveSecret(Environment environment) {
        return Optional.ofNullable(environment.getProperty(JWT_SECRET_KEY))
                .filter(value -> !value.isBlank())
                .or(() -> DotEnvReader.read(JWT_SECRET_KEY))
                .or(() -> Optional.ofNullable(environment.getProperty(PASSWORD_PEPPER_KEY)).filter(value -> !value.isBlank()))
                .or(() -> DotEnvReader.read(PASSWORD_PEPPER_KEY))
                .orElseThrow(() -> new IllegalStateException(
                        "JWT_SECRET deve ser configurado como variável de ambiente ou no arquivo .env local."
                ));
    }

    private static final class MessageDigestTimingSafe {
        private MessageDigestTimingSafe() {
        }

        static boolean equals(String expected, String actual) {
            return java.security.MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    actual.getBytes(StandardCharsets.UTF_8)
            );
        }
    }
}
