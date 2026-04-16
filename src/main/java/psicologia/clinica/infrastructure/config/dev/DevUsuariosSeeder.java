package psicologia.clinica.infrastructure.config.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
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

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevUsuariosSeeder implements ApplicationRunner {

    public static final String CLINICA_DEV_ID = "00000000000191";
    public static final String SENHA_PADRAO = "Dev@123456";

    private final ClinicaRepository clinicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorRepository gestorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EstagiarioRepository estagiarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Clinica clinica = garantirClinicaDev();

        criarUsuarioDev("00000000001", "Dev Gestor Sistema", "dev.gestor.sistema@clinica.local",
                PerfilRoot.GESTOR_SISTEMA, null, "Administrador de Rede", clinica);
        criarUsuarioDev("00000000002", "Dev Gestor Clinica", "dev.gestor.clinica@clinica.local",
                PerfilRoot.GESTOR_CLINICA, null, "Gestor da Clinica", clinica);
        Usuario profissional = criarUsuarioDev("00000000003", "Dev Profissional Saude", "dev.profissional@clinica.local",
                PerfilRoot.FUNCIONARIO, SubPerfil.PROFISSIONAL_SAUDE, null, clinica);
        criarUsuarioDev("00000000004", "Dev Atendente", "dev.atendente@clinica.local",
                PerfilRoot.FUNCIONARIO, SubPerfil.ATENDENTE, null, clinica);
        criarUsuarioDev("00000000005", "Dev Secretaria", "dev.secretaria@clinica.local",
                PerfilRoot.FUNCIONARIO, SubPerfil.SECRETARIA, null, clinica);
        criarEstagiarioDev("00000000006", "Dev Estagiario", "dev.estagiario@clinica.local", clinica, profissional);
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

    private Usuario criarUsuarioDev(String cpf, String nome, String email, PerfilRoot perfilRoot,
                                 SubPerfil subPerfil, String cargo, Clinica clinica) {
        if (usuarioRepository.existsById(cpf)) {
            return usuarioRepository.findById(cpf).orElseThrow();
        }

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .cpf(cpf)
                .nomeCompleto(nome)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .email(email)
                .senha(passwordEncoder.encode(SENHA_PADRAO))
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

    private void criarEstagiarioDev(String cpf, String nome, String email, Clinica clinica, Usuario supervisorUsuario) {
        if (usuarioRepository.existsById(cpf)) {
            return;
        }

        Funcionario supervisor = funcionarioRepository.findById(supervisorUsuario.getCpf()).orElseThrow();
        Usuario usuario = criarUsuarioDev(cpf, nome, email, PerfilRoot.FUNCIONARIO, SubPerfil.ESTAGIARIO, null, clinica);

        estagiarioRepository.save(Estagiario.builder()
                .usuario(usuario)
                .supervisor(supervisor)
                .instituicaoEnsino("Instituicao Dev")
                .periodoAtual(9)
                .build());
    }
}
