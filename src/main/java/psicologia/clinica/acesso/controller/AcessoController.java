package psicologia.clinica.acesso.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import psicologia.clinica.acesso.dtos.AcessoRequestDTO;
import psicologia.clinica.acesso.dtos.AcessoResponseDTO;
import psicologia.clinica.acesso.dtos.ConfirmarSegundoFatorRequestDTO;
import psicologia.clinica.acesso.dtos.RefreshTokenRequestDTO;
import psicologia.clinica.acesso.dtos.TokenResponseDTO;
import psicologia.clinica.acesso.service.AcessoService;

@RestController
@RequestMapping("/acesso")
@RequiredArgsConstructor
public class AcessoController {

    private final AcessoService acessoService;

    @PostMapping("/entrar")
    public AcessoResponseDTO entrar(@RequestBody @Valid AcessoRequestDTO request, HttpServletRequest httpRequest) {
        return acessoService.entrar(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/confirmar-2fa")
    public TokenResponseDTO confirmarSegundoFator(@RequestBody @Valid ConfirmarSegundoFatorRequestDTO request,
                                                  HttpServletRequest httpRequest) {
        return acessoService.confirmarSegundoFator(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/refresh")
    public TokenResponseDTO refresh(@RequestBody @Valid RefreshTokenRequestDTO request,
                                    HttpServletRequest httpRequest) {
        return acessoService.refresh(request, httpRequest.getRemoteAddr());
    }
}
