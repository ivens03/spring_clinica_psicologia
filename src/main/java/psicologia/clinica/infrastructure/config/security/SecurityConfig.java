package psicologia.clinica.infrastructure.config.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2idPasswordEncoder();
    }

    // Implementação manual simples do Argon2id usando Bouncy Castle
    // Em um cenário real, poderíamos usar bibliotecas que facilitam isso,
    // mas vamos seguir o que está disponível.
    public static class Argon2idPasswordEncoder implements PasswordEncoder {

        private static final int SALT_LENGTH = 16;
        private static final int HASH_LENGTH = 32;
        private static final int ITERATIONS = 3;
        private static final int MEMORY = 65536;
        private static final int PARALLELISM = 4;

        @Override
        public String encode(CharSequence rawPassword) {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            byte[] hash = generateHash(rawPassword, salt);

            return String.format("$argon2id$v=19$m=%d,t=%d,p=%d$%s$%s",
                    MEMORY, ITERATIONS, PARALLELISM,
                    Base64.getEncoder().withoutPadding().encodeToString(salt),
                    Base64.getEncoder().withoutPadding().encodeToString(hash));
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            if (encodedPassword == null || !encodedPassword.startsWith("$argon2id$")) {
                return false;
            }

            String[] parts = encodedPassword.split("\\$");
            if (parts.length != 6) {
                return false;
            }

            // m=65536,t=3,p=4
            String[] params = parts[3].split(",");
            int m = Integer.parseInt(params[0].split("=")[1]);
            int t = Integer.parseInt(params[1].split("=")[1]);
            int p = Integer.parseInt(params[2].split("=")[1]);

            byte[] salt = Base64.getDecoder().decode(parts[4]);
            byte[] hash = Base64.getDecoder().decode(parts[5]);

            byte[] testHash = generateHash(rawPassword, salt, m, t, p);

            if (hash.length != testHash.length) return false;
            int result = 0;
            for (int i = 0; i < hash.length; i++) {
                result |= hash[i] ^ testHash[i];
            }
            return result == 0;
        }

        private byte[] generateHash(CharSequence password, byte[] salt) {
            return generateHash(password, salt, MEMORY, ITERATIONS, PARALLELISM);
        }

        private byte[] generateHash(CharSequence password, byte[] salt, int m, int t, int p) {
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                    .withIterations(t)
                    .withMemoryAsKB(m)
                    .withParallelism(p)
                    .withSalt(salt)
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] hash = new byte[HASH_LENGTH];
            generator.generateBytes(password.toString().toCharArray(), hash);

            return hash;
        }
    }
}
