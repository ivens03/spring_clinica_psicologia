package psicologia.clinica.usuario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.clinica.model.Clinica;
import psicologia.clinica.clinica.service.ClinicaService;
import psicologia.clinica.exception.exception.ResourceNotFoundException;
import psicologia.clinica.infrastructure.auditoria.model.Acesso;
import psicologia.clinica.infrastructure.auditoria.repository.AcessoRepository;
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.exception.exception.usuario.CpfDuplicadoException;
import psicologia.clinica.exception.exception.usuario.EmailDuplicadoException;
import psicologia.clinica.exception.exception.usuario.UsuarioException;
import psicologia.clinica.usuario.model.*;
import psicologia.clinica.usuario.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GestorRepository gestorRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EstagiarioRepository estagiarioRepository;
    private final AcessoRepository acessoRepository;
    private final ClinicaService clinicaService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario salvar(UsuarioRequestDTO request) {
        if (usuarioRepository.existsById(request.cpf())) {
            throw new CpfDuplicadoException(request.cpf());
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailDuplicadoException(request.email());
        }

        Clinica clinica = clinicaService.buscarPorId(request.clinicaId());

        Usuario usuario = Usuario.builder()
                .cpf(request.cpf())
                .nomeCompleto(request.nome())
                .dataNascimento(request.dataNascimento())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .perfilRoot(request.perfilRoot())
                .clinica(clinica)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Criar perfil específico
        criarPerfilEspecífico(usuario, request);

        // Log de Auditoria
        acessoRepository.save(Acesso.builder()
                .usuarioCpf(usuario.getCpf())
                .clinicaId(clinica.getIdentificadorFiscal())
                .acao("USUARIO_CRIADO")
                .detalhes("Perfil: " + request.perfilRoot() + (request.subPerfil() != null ? " / " + request.subPerfil() : ""))
                .build());

        return usuario;
    }

    private void criarPerfilEspecífico(Usuario usuario, UsuarioRequestDTO request) {
        if (request.perfilRoot() == PerfilRoot.GESTOR_CLINICA || request.perfilRoot() == PerfilRoot.GESTOR_SISTEMA) {
            gestorRepository.save(Gestor.builder()
                    .usuario(usuario)
                    .cargo(request.cargo())
                    .build());
        }

        if (request.subPerfil() != null) {
            Funcionario funcionario = Funcionario.builder()
                    .usuario(usuario)
                    .subPerfil(request.subPerfil())
                    .conselhoClasse(request.conselhoClasse())
                    .registroConselho(request.registroConselho())
                    .build();
            funcionarioRepository.save(funcionario);

            // Se for estagiário, precisa de vínculo
            if (request.supervisorCpf() != null) {
                Funcionario supervisor = funcionarioRepository.findById(request.supervisorCpf())
                        .orElseThrow(() -> new UsuarioException("Supervisor não encontrado com o CPF informado."));
                
                estagiarioRepository.save(Estagiario.builder()
                        .usuario(usuario)
                        .supervisor(supervisor)
                        .instituicaoEnsino(request.instituicaoEnsino())
                        .periodoAtual(request.periodoAtual())
                        .build());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCpf(String cpf) {
        return usuarioRepository.findById(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o CPF: " + cpf));
    }

    @Transactional
    public void deletar(String cpf) {
        Usuario usuario = buscarPorCpf(cpf);
        usuario.setAtivo(false);
        usuario.setExcluidoEm(java.time.LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}
