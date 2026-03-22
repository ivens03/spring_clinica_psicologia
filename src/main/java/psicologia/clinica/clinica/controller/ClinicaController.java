package psicologia.clinica.clinica.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import psicologia.clinica.clinica.dtos.ClinicaRequestDTO;
import psicologia.clinica.clinica.dtos.ClinicaResponseDTO;
import psicologia.clinica.clinica.service.ClinicaService;

import java.util.List;

@RestController
@RequestMapping("/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService clinicaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicaResponseDTO criar(@RequestBody @Valid ClinicaRequestDTO request) {
        return ClinicaResponseDTO.fromEntity(clinicaService.salvar(request));
    }

    @GetMapping
    public List<ClinicaResponseDTO> listar() {
        return clinicaService.listarTodos().stream()
                .map(ClinicaResponseDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ClinicaResponseDTO buscar(@PathVariable String id) {
        return ClinicaResponseDTO.fromEntity(clinicaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ClinicaResponseDTO atualizar(@PathVariable String id, @RequestBody @Valid ClinicaRequestDTO request) {
        return ClinicaResponseDTO.fromEntity(clinicaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable String id) {
        clinicaService.deletar(id);
    }
}
