package psicologia.clinica.infrastructure.config.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.model.TipoClinica;
import psicologia.clinica.clinica.model.TipoPessoa;
import psicologia.clinica.clinica.repository.ClinicaRepository;
import psicologia.clinica.usuario.model.Funcionario;
import psicologia.clinica.usuario.model.Gestor;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;
import psicologia.clinica.usuario.model.Estagiario;
import psicologia.clinica.usuario.repository.EstagiarioRepository;
import psicologia.clinica.usuario.repository.FuncionarioRepository;
import psicologia.clinica.usuario.repository.GestorRepository;
import psicologia.clinica.usuario.repository.UsuarioRepository;
import psicologia.clinica.infrastructure.config.env.DotEnvReader;

import java.time.LocalDate;
import java.util.Optional;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevUsuariosSeeder implements ApplicationRunner {

    public static final String CLINICA_DEV_ID = "00000000000191";
    public static final String SENHA_PADRAO = "Dev@123456";
    public static final String DEV_USER_PASSWORD_KEY = "DEV_USER_PASSWORD";

    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorRepository gestorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EstagiarioRepository estagiarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Clinica clinica = garantirClinicaDev();

        String senhaPadrao = property(DEV_USER_PASSWORD_KEY, SENHA_PADRAO);

        criarUsuarioDev("00000000001", "Dev Gestor Sistema", property("DEV_GESTOR_SISTEMA_EMAIL", "dev.gestor.sistema@clinica.local"),
                senhaPadrao,
                PerfilRoot.GESTOR_SISTEMA, null, "Administrador de Rede", clinica);
        criarUsuarioDev("00000000002", "Dev Gestor Clinica", property("DEV_GESTOR_CLINICA_EMAIL", "dev.gestor.clinica@clinica.local"),
                senhaPadrao,
                PerfilRoot.GESTOR_CLINICA, null, "Gestor da Clinica", clinica);
        Usuario profissional = criarUsuarioDev("00000000003", "Dev Profissional Saude", property("DEV_PROFISSIONAL_EMAIL", "dev.profissional@clinica.local"),
                senhaPadrao,
                PerfilRoot.FUNCIONARIO, SubPerfil.PROFISSIONAL_SAUDE, null, clinica);
        criarUsuarioDev("00000000004", "Dev Atendente", property("DEV_ATENDENTE_EMAIL", "dev.atendente@clinica.local"),
                senhaPadrao,
                PerfilRoot.FUNCIONARIO, SubPerfil.ATENDENTE, null, clinica);
        criarUsuarioDev("00000000005", "Dev Secretaria", property("DEV_SECRETARIA_EMAIL", "dev.secretaria@clinica.local"),
                senhaPadrao,
                PerfilRoot.FUNCIONARIO, SubPerfil.SECRETARIA, null, clinica);
        criarEstagiarioDev("00000000006", "Dev Estagiario", property("DEV_ESTAGIARIO_EMAIL", "dev.estagiario@clinica.local"),
                senhaPadrao, clinica, profissional);
    }

    private Clinica garantirClinicaDev() {
        return clinicaRepository.findById(CLINICA_DEV_ID)
                .orElseGet(() -> clinicaRepository.save(Clinica.builder()
                        .identificadorFiscal(CLINICA_DEV_ID)
                        .nomeExibicao("Clinica Desenvolvimento")
                        .tipoPessoa(TipoPessoa.JURIDICA)
                        .tipoClinica(TipoClinica.EMPRESA_ESCOLAR)
                        .build()));
    }

    private Usuario criarUsuarioDev(String cpf, String nome, String email, String senhaPadrao, PerfilRoot perfilRoot,
                                 SubPerfil subPerfil, String cargo, Clinica clinica) {
        if (usuarioRepository.existsById(cpf)) {
            Usuario usuarioExistente = usuarioRepository.findById(cpf).orElseThrow();
            atualizarCredenciaisDevSeNecessario(usuarioExistente, email, senhaPadrao);
            return usuarioExistente;
        }

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .cpf(cpf)
                .nomeCompleto(nome)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .email(email)
                .senha(passwordEncoder.encode(senhaPadrao))
                .perfilRoot(perfilRoot)
                .clinica(clinica)
                .build());

        if (perfilRoot == PerfilRoot.GESTOR_SISTEMA || perfilRoot == PerfilRoot.GESTOR_CLINICA) {
            gestorRepository.save(Gestor.builder()
                    .usuario(usuario)
                    .cargo(cargo)
                    .build());
        }

        if (subPerfil != null) {
            funcionarioRepository.save(Funcionario.builder()
                    .usuario(usuario)
                    .subPerfil(subPerfil)
                    .conselhoClasse(subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "CRP" : null)
                    .registroConselho(subPerfil == SubPerfil.PROFISSIONAL_SAUDE ? "DEV-0001" : null)
                    .build());
        }

        return usuario;
    }

    private void criarEstagiarioDev(String cpf, String nome, String email, String senhaPadrao, Clinica clinica, Usuario supervisorUsuario) {
        Funcionario supervisor = funcionarioRepository.findById(supervisorUsuario.getCpf()).orElseThrow();
        Usuario usuario = criarUsuarioDev(cpf, nome, email, senhaPadrao, PerfilRoot.FUNCIONARIO, SubPerfil.ESTAGIARIO, null, clinica);

        if (estagiarioRepository.existsById(cpf)) {
            return;
        }

        estagiarioRepository.save(Estagiario.builder()
                .usuario(usuario)
                .supervisor(supervisor)
                .instituicaoEnsino("Instituicao Dev")
                .periodoAtual(9)
                .build());
    }

    private void atualizarCredenciaisDevSeNecessario(Usuario usuario, String email, String senhaPadrao) {
        boolean alterado = false;
        String senha = usuario.getSenha();

        if (!email.equals(usuario.getEmail())) {
            usuario.setEmail(email);
            alterado = true;
        }

        if (senha == null || !passwordEncoder.matches(senhaPadrao, senha)) {
            senha = passwordEncoder.encode(senhaPadrao);
            usuario.setSenha(senha);
            alterado = true;
        }

        if (alterado) {
            usuarioRepository.atualizarCredenciaisDev(usuario.getCpf(), usuario.getEmail(), senha);
        }
    }

    private String property(String key, String defaultValue) {
        return Optional.ofNullable(environment.getProperty(key))
                .filter(value -> !value.isBlank())
                .or(() -> DotEnvReader.read(key))
                .orElse(defaultValue);
    }
}
