package psicologia.clinica.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import psicologia.clinica.acesso.security.JwtAuthenticationFilter;
import psicologia.clinica.infrastructure.config.env.DotEnvReader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> response.sendError(401))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/login/2fa",
                                "/acesso/entrar",
                                "/acesso/confirmar-2fa",
                                "/acesso/refresh",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/clinicas", "/usuarios").permitAll()
                        .requestMatchers("/gestao/sistema").hasRole("GESTOR_SISTEMA")
                        .requestMatchers("/gestao/clinica").hasRole("GESTOR_CLINICA")
                        .requestMatchers("/atendimento/pacientes").hasRole("PROFISSIONAL_SAUDE")
                        .requestMatchers("/supervisao/estagiario").hasRole("ESTAGIARIO")
                        .requestMatchers("/atendimento/agenda").hasAnyRole("ATENDENTE", "SECRETARIA")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/usuarios/**").hasAnyRole("GESTOR_SISTEMA", "GESTOR_CLINICA")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/usuarios/**").hasAnyRole("GESTOR_SISTEMA", "GESTOR_CLINICA")
                        .requestMatchers("/clinicas/**").hasAnyRole("GESTOR_SISTEMA", "GESTOR_CLINICA")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
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

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Autenticação deve usar o fluxo JWT da aplicação.");
        };
    }

    private String resolvePasswordPepper(Environment environment) {
        return Optional.ofNullable(environment.getProperty(PASSWORD_PEPPER_KEY))
                .filter(this::hasText)
                .or(() -> DotEnvReader.read(PASSWORD_PEPPER_KEY))
                .orElseThrow(() -> new IllegalStateException(
                        "PASSWORD_PEPPER deve ser configurado como variável de ambiente ou no arquivo .env local."
                ));
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
