package psicologia.clinica.acesso.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psicologia.clinica.acesso.dtos.AcessoRequestDTO;
import psicologia.clinica.acesso.dtos.AcessoResponseDTO;
import psicologia.clinica.acesso.dtos.ConfirmarSegundoFatorRequestDTO;
import psicologia.clinica.acesso.dtos.DirecionamentoAcessoDTO;
import psicologia.clinica.acesso.dtos.RefreshTokenRequestDTO;
import psicologia.clinica.acesso.dtos.TokenResponseDTO;
import psicologia.clinica.acesso.exception.CredenciaisInvalidasException;
import psicologia.clinica.acesso.model.SegundoFatorDesafio;
import psicologia.clinica.infrastructure.auditoria.model.Acesso;
import psicologia.clinica.infrastructure.auditoria.repository.AcessoRepository;
import psicologia.clinica.usuario.model.Funcionario;
import psicologia.clinica.usuario.model.PerfilRoot;
import psicologia.clinica.usuario.model.SubPerfil;
import psicologia.clinica.usuario.model.Usuario;
import psicologia.clinica.usuario.repository.FuncionarioRepository;
import psicologia.clinica.usuario.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AcessoService {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AcessoRepository acessoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SegundoFatorService segundoFatorService;
    private final TokenService tokenService;

    @Transactional
    public AcessoResponseDTO entrar(AcessoRequestDTO request, String ip) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            registrarAcesso(usuario, ip, "FALHA_LOGIN", "Senha inválida.");
            throw new CredenciaisInvalidasException();
        }

        SubPerfil subPerfil = buscarSubPerfil(usuario);
        DirecionamentoAcessoDTO direcionamento = definirDirecionamento(usuario.getPerfilRoot(), subPerfil);

        if (segundoFatorService.obrigatorio(usuario.getPerfilRoot(), subPerfil)) {
            SegundoFatorService.DesafioSegundoFator desafio = segundoFatorService.iniciar(usuario, ip);
            registrarAcesso(usuario, ip, "LOGIN_2FA_SOLICITADO", "Direcionamento: " + direcionamento.nome());
            return AcessoResponseDTO.aguardandoSegundoFator(
                    usuario,
                    subPerfil,
                    direcionamento,
                    desafio.id().toString(),
                    desafio.codigoDev()
            );
        }

        TokenResponseDTO tokens = tokenService.emitir(usuario, subPerfil, ip, direcionamento.rota());
        registrarAcesso(usuario, ip, "LOGIN", "Direcionamento: " + direcionamento.nome());

        return AcessoResponseDTO.autenticado(usuario, subPerfil, direcionamento, tokens);
    }

    @Transactional
    public TokenResponseDTO confirmarSegundoFator(ConfirmarSegundoFatorRequestDTO request, String ip) {
        SegundoFatorDesafio desafio = segundoFatorService.confirmar(request.desafioId(), request.codigo());
        Usuario usuario = usuarioRepository.findById(desafio.getUsuarioCpf())
                .orElseThrow(CredenciaisInvalidasException::new);
        SubPerfil subPerfil = buscarSubPerfil(usuario);
        DirecionamentoAcessoDTO direcionamento = definirDirecionamento(usuario.getPerfilRoot(), subPerfil);
        TokenResponseDTO tokens = tokenService.emitir(usuario, subPerfil, ip, direcionamento.rota());
        registrarAcesso(usuario, ip, "LOGIN_2FA_CONFIRMADO", "Direcionamento: " + direcionamento.nome());
        return tokens;
    }

    @Transactional
    public TokenResponseDTO refresh(RefreshTokenRequestDTO request, String ip) {
        return tokenService.refresh(request.refreshToken(), ip, "/inicio");
    }

    private SubPerfil buscarSubPerfil(Usuario usuario) {
        return funcionarioRepository.findById(usuario.getCpf())
                .map(Funcionario::getSubPerfil)
                .orElse(null);
    }

    private DirecionamentoAcessoDTO definirDirecionamento(PerfilRoot perfilRoot, SubPerfil subPerfil) {
        if (perfilRoot == PerfilRoot.GESTOR_SISTEMA) {
            return new DirecionamentoAcessoDTO("PAINEL_GESTOR_SISTEMA", "/gestao/sistema");
        }

        if (perfilRoot == PerfilRoot.GESTOR_CLINICA) {
            return new DirecionamentoAcessoDTO("PAINEL_GESTOR_CLINICA", "/gestao/clinica");
        }

        if (subPerfil == SubPerfil.PROFISSIONAL_SAUDE) {
            return new DirecionamentoAcessoDTO("PAINEL_PROFISSIONAL_SAUDE", "/atendimento/pacientes");
        }

        if (subPerfil == SubPerfil.ESTAGIARIO) {
            return new DirecionamentoAcessoDTO("PAINEL_ESTAGIARIO", "/supervisao/estagiario");
        }

        if (subPerfil == SubPerfil.SECRETARIA || subPerfil == SubPerfil.ATENDENTE) {
            return new DirecionamentoAcessoDTO("PAINEL_ATENDIMENTO", "/atendimento/agenda");
        }

        return new DirecionamentoAcessoDTO("PAINEL_USUARIO", "/inicio");
    }

    private void registrarAcesso(Usuario usuario, String ip, String acao, String detalhes) {
        acessoRepository.save(Acesso.builder()
                .usuarioCpf(usuario.getCpf())
                .clinicaId(usuario.getClinica().getIdentificadorFiscal())
                .ip(ip)
                .acao(acao)
                .detalhes(detalhes)
                .build());
    }

}
