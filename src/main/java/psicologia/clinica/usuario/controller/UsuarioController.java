package psicologia.clinica.usuario.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import psicologia.clinica.usuario.dtos.UsuarioRequestDTO;
import psicologia.clinica.usuario.dtos.UsuarioResponseDTO;
import psicologia.clinica.usuario.model.Usuario;
import psicologia.clinica.usuario.service.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar(@RequestBody @Valid UsuarioRequestDTO request) {
        Usuario usuario = usuarioService.salvar(request);
        return UsuarioResponseDTO.fromEntity(usuario, request.subPerfil());
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listarTodos().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{cpf}")
    public UsuarioResponseDTO buscar(@PathVariable String cpf) {
        return UsuarioResponseDTO.fromEntity(usuarioService.buscarPorCpf(cpf));
    }

    @DeleteMapping("/{cpf}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String cpf) {
        usuarioService.deletar(cpf);
    }
}
