package psicologia.clinica.acesso.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import psicologia.clinica.acesso.dtos.AcessoRequestDTO;
import psicologia.clinica.acesso.dtos.AcessoResponseDTO;
import psicologia.clinica.acesso.dtos.ConfirmarSegundoFatorRequestDTO;
import psicologia.clinica.acesso.dtos.TokenResponseDTO;
import psicologia.clinica.acesso.exception.CredenciaisInvalidasException;
import psicologia.clinica.acesso.exception.SegundoFatorInvalidoException;
import psicologia.clinica.acesso.service.AcessoService;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final AcessoService acessoService;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("acesso", new AcessoRequestDTO("", ""));
        return "login";
    }

    @PostMapping("/login")
    public String entrar(@ModelAttribute("acesso") @Valid AcessoRequestDTO request,
                         BindingResult bindingResult,
                         HttpServletRequest httpRequest,
                         HttpServletResponse httpResponse) {
        if (bindingResult.hasErrors()) {
            return "redirect:/login?erro";
        }

        try {
            AcessoResponseDTO acesso = acessoService.entrar(request, httpRequest.getRemoteAddr());
            if (acesso.segundoFatorObrigatorio()) {
                String destino = "/login/2fa?desafio=" + acesso.desafioSegundoFatorId();
                if (acesso.codigoSegundoFatorDev() != null) {
                    destino += "&codigoDev=" + acesso.codigoSegundoFatorDev();
                }
                return "redirect:" + destino;
            }
            adicionarCookies(httpResponse, acesso.accessToken(), acesso.refreshToken());
            return "redirect:" + acesso.rota();
        } catch (CredenciaisInvalidasException exception) {
            return "redirect:/login?erro";
        }
    }

    @GetMapping("/login/2fa")
    public String segundoFator() {
        return "login-2fa";
    }

    @PostMapping("/login/2fa")
    public String confirmarSegundoFator(@ModelAttribute ConfirmarSegundoFatorRequestDTO request,
                                        HttpServletRequest httpRequest,
                                        HttpServletResponse httpResponse) {
        try {
            TokenResponseDTO tokens = acessoService.confirmarSegundoFator(request, httpRequest.getRemoteAddr());
            adicionarCookies(httpResponse, tokens.accessToken(), tokens.refreshToken());
            return "redirect:" + tokens.rota();
        } catch (SegundoFatorInvalidoException exception) {
            return "redirect:/login/2fa?desafio=" + request.desafioId() + "&erro";
        }
    }

    @GetMapping("/gestao/sistema")
    @ResponseBody
    public String gestorSistema() {
        return "<h1>GESTOR_SISTEMA</h1>";
    }

    @GetMapping("/gestao/clinica")
    @ResponseBody
    public String gestorClinica() {
        return "<h1>GESTOR_CLINICA</h1>";
    }

    @GetMapping("/atendimento/pacientes")
    @ResponseBody
    public String profissionalSaude() {
        return "<h1>PROFISSIONAL_SAUDE</h1>";
    }

    @GetMapping("/supervisao/estagiario")
    @ResponseBody
    public String estagiario() {
        return "<h1>ESTAGIARIO</h1>";
    }

    @GetMapping("/atendimento/agenda")
    @ResponseBody
    public String atendimento() {
        return "<h1>ATENDIMENTO</h1>";
    }

    @GetMapping("/inicio")
    @ResponseBody
    public String usuario() {
        return "<h1>USUARIO</h1>";
    }

    private void adicionarCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build()
                .toString());
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .sameSite("Strict")
                .path("/")
                .build()
                .toString());
    }
}
