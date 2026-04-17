package psicologia.clinica.infrastructure.config.dev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.usuario.model.Estagiario;
import psicologia.clinica.usuario.model.Funcionario;
import psicologia.clinica.usuario.model.Gestor;
import psicologia.clinica.usuario.model.Usuario;
import psicologia.clinica.usuario.repository.EstagiarioRepository;
import psicologia.clinica.usuario.repository.FuncionarioRepository;
import psicologia.clinica.usuario.repository.GestorRepository;
import psicologia.clinica.usuario.repository.UsuarioRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de carga de usuários de desenvolvimento")
class CriandoUsuariosDevSeederTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private GestorRepository gestorRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private EstagiarioRepository estagiarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Criando usuários de desenvolvimento com senha criptografada")
    void criandoUsuariosDevComSenhaCriptografada() throws Exception {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal(DevUsuariosSeeder.CLINICA_DEV_ID)
                .nomeExibicao("Clinica Desenvolvimento")
                .build();

        when(clinicaRepository.findById(DevUsuariosSeeder.CLINICA_DEV_ID)).thenReturn(Optional.of(clinica));
        when(usuarioRepository.existsById(any(String.class))).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(funcionarioRepository.findById("00000000003")).thenReturn(Optional.of(Funcionario.builder()
                .usuario(Usuario.builder().cpf("00000000003").build())
                .build()));
        MockEnvironment environment = new MockEnvironment()
                .withProperty(DevUsuariosSeeder.DEV_USER_PASSWORD_KEY, "SenhaEnv@123");
        when(passwordEncoder.encode("SenhaEnv@123")).thenReturn("$argon2id$hash-dev");

        DevUsuariosSeeder seeder = new DevUsuariosSeeder(
                clinicaRepository,
                usuarioRepository,
                gestorRepository,
                funcionarioRepository,
                estagiarioRepository,
                passwordEncoder,
                environment
        );

        seeder.run(new DefaultApplicationArguments());

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository, times(6)).save(usuarioCaptor.capture());
        verify(gestorRepository, times(2)).save(any(Gestor.class));
        verify(funcionarioRepository, times(4)).save(any(Funcionario.class));
        verify(estagiarioRepository).save(any(Estagiario.class));

        assertThat(usuarioCaptor.getAllValues())
                .hasSize(6)
                .allSatisfy(usuario -> {
                    assertThat(usuario.getSenha()).isEqualTo("$argon2id$hash-dev");
                    assertThat(usuario.getSenha()).isNotEqualTo("SenhaEnv@123");
                    assertThat(usuario.getClinica()).isSameAs(clinica);
                });
    }

    @Test
    @DisplayName("Atualizando usuário de desenvolvimento já existente com variáveis de ambiente")
    void atualizandoUsuarioDevExistenteComVariaveisDeAmbiente() throws Exception {
        Clinica clinica = Clinica.builder()
                .identificadorFiscal(DevUsuariosSeeder.CLINICA_DEV_ID)
                .nomeExibicao("Clinica Desenvolvimento")
                .build();

        when(clinicaRepository.findById(DevUsuariosSeeder.CLINICA_DEV_ID)).thenReturn(Optional.of(clinica));
        when(usuarioRepository.existsById(any(String.class))).thenReturn(true);
        when(usuarioRepository.findById(any(String.class))).thenAnswer(invocation -> Optional.of(Usuario.builder()
                .cpf(invocation.getArgument(0))
                .email("email.antigo@clinica.local")
                .senha("$argon2id$hash-antigo")
                .clinica(clinica)
                .build()));
        when(passwordEncoder.matches("SenhaEnv@123", "$argon2id$hash-antigo")).thenReturn(false);
        when(passwordEncoder.encode("SenhaEnv@123")).thenReturn("$argon2id$hash-novo");
        when(funcionarioRepository.findById("00000000003")).thenReturn(Optional.of(Funcionario.builder()
                .usuario(Usuario.builder().cpf("00000000003").build())
                .build()));

        MockEnvironment environment = new MockEnvironment()
                .withProperty(DevUsuariosSeeder.DEV_USER_PASSWORD_KEY, "SenhaEnv@123")
                .withProperty("DEV_GESTOR_SISTEMA_EMAIL", "gestor.sistema.env@clinica.local")
                .withProperty("DEV_GESTOR_CLINICA_EMAIL", "gestor.clinica.env@clinica.local")
                .withProperty("DEV_PROFISSIONAL_EMAIL", "profissional.env@clinica.local")
                .withProperty("DEV_ATENDENTE_EMAIL", "atendente.env@clinica.local")
                .withProperty("DEV_SECRETARIA_EMAIL", "secretaria.env@clinica.local")
                .withProperty("DEV_ESTAGIARIO_EMAIL", "estagiario.env@clinica.local");

        DevUsuariosSeeder seeder = new DevUsuariosSeeder(
                clinicaRepository,
                usuarioRepository,
                gestorRepository,
                funcionarioRepository,
                estagiarioRepository,
                passwordEncoder,
                environment
        );

        seeder.run(new DefaultApplicationArguments());

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(usuarioRepository, times(6)).atualizarCredenciaisDev(
                any(String.class),
                any(String.class),
                eq("$argon2id$hash-novo")
        );
        verify(gestorRepository, never()).save(any(Gestor.class));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }
}
