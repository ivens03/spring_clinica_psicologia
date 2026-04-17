package psicologia.clinica.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final int ARGON2_SALT_LENGTH = 16;
    private static final int ARGON2_HASH_LENGTH = 32;
    private static final int ARGON2_PARALLELISM = 1;
    private static final int ARGON2_MEMORY_KB = 65_536;
    private static final int ARGON2_ITERATIONS = 3;

    private static final String PASSWORD_PEPPER_KEY = "PASSWORD_PEPPER";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(Environment environment) {
        PasswordEncoder argon2id = new Argon2PasswordEncoder(
                ARGON2_SALT_LENGTH,
                ARGON2_HASH_LENGTH,
                ARGON2_PARALLELISM,
                ARGON2_MEMORY_KB,
                ARGON2_ITERATIONS
        );

        return new PepperedPasswordEncoder(argon2id, resolvePasswordPepper(environment));
    }

    private String resolvePasswordPepper(Environment environment) {
        return Optional.ofNullable(environment.getProperty(PASSWORD_PEPPER_KEY))
                .filter(this::hasText)
                .or(this::readPasswordPepperFromDotEnv)
                .orElseThrow(() -> new IllegalStateException(
                        "PASSWORD_PEPPER deve ser configurado como variável de ambiente ou no arquivo .env local."
                ));
    }

    private Optional<String> readPasswordPepperFromDotEnv() {
        Path dotEnvPath = Path.of(".env");
        if (Files.notExists(dotEnvPath)) {
            return Optional.empty();
        }

        try {
            return Files.readAllLines(dotEnvPath, StandardCharsets.UTF_8)
                    .stream()
                    .map(String::trim)
                    .filter(line -> line.startsWith(PASSWORD_PEPPER_KEY + "="))
                    .map(line -> line.substring((PASSWORD_PEPPER_KEY + "=").length()).trim())
                    .filter(this::hasText)
                    .findFirst();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler o arquivo .env local.", exception);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    static class PepperedPasswordEncoder implements PasswordEncoder {

        private final PasswordEncoder delegate;
        private final byte[] pepperBytes;

        PepperedPasswordEncoder(PasswordEncoder delegate, String pepper) {
            this.delegate = Objects.requireNonNull(delegate);
            this.pepperBytes = Objects.requireNonNull(pepper).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String encode(CharSequence rawPassword) {
            return delegate.encode(applyPepper(rawPassword));
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return delegate.matches(applyPepper(rawPassword), encodedPassword);
        }

        private String applyPepper(CharSequence rawPassword) {
            Objects.requireNonNull(rawPassword, "A senha não pode ser nula.");

            try {
                Mac mac = Mac.getInstance(HMAC_ALGORITHM);
                mac.init(new SecretKeySpec(pepperBytes, HMAC_ALGORITHM));
                byte[] digest = mac.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(digest);
            } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
                throw new IllegalStateException("Não foi possível aplicar o pepper da senha.", exception);
            }
        }
    }
}
