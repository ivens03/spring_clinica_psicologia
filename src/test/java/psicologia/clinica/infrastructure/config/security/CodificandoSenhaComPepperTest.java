package psicologia.clinica.infrastructure.config.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de codificação de senha com pepper")
class CodificandoSenhaComPepperTest {

    @Test
    @DisplayName("Codificando senha com Argon2id e pepper")
    void codificandoSenhaComArgon2idEPepper() {
        PasswordEncoder passwordEncoder = passwordEncoderComPepper("pepper-test-local");

        String senhaCodificada = passwordEncoder.encode("Senha@123456");

        assertThat(senhaCodificada).startsWith("$argon2id$");
        assertThat(senhaCodificada).isNotEqualTo("Senha@123456");
        assertThat(passwordEncoder.matches("Senha@123456", senhaCodificada)).isTrue();
        assertThat(passwordEncoder.matches("SenhaErrada@123456", senhaCodificada)).isFalse();
    }

    @Test
    @DisplayName("Rejeitando senha quando pepper muda")
    void rejeitandoSenhaQuandoPepperMuda() {
        PasswordEncoder passwordEncoderOriginal = passwordEncoderComPepper("pepper-original");
        PasswordEncoder passwordEncoderComOutroPepper = passwordEncoderComPepper("pepper-alterado");

        String senhaCodificada = passwordEncoderOriginal.encode("Senha@123456");

        assertThat(passwordEncoderComOutroPepper.matches("Senha@123456", senhaCodificada)).isFalse();
    }

    private PasswordEncoder passwordEncoderComPepper(String pepper) {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("PASSWORD_PEPPER", pepper);

        return new SecurityConfig().passwordEncoder(environment);
    }
}
