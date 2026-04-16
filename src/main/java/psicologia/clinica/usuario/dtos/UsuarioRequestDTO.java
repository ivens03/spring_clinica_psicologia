package psicologia.clinica.usuario.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import psicologia.clinica.usuario.model.PerfilRoot;

import java.time.LocalDate;

public record UsuarioRequestDTO(
    @NotBlank @Size(min = 11, max = 11)
    String cpf,
    @NotBlank
    String nome,
    @NotNull
    LocalDate dataNascimento,
    @NotBlank @Email
    String email,
    @NotBlank
    String senha,
    @NotNull
    PerfilRoot perfilRoot,
    @NotBlank
    String clinicaId,
    
    // Campos opcionais para perfis específicos
    String cargo, // Gestor
    psicologia.clinica.usuario.model.SubPerfil subPerfil, // Funcionario
    String conselhoClasse, // Funcionario
    String registroConselho, // Funcionario
    String supervisorCpf, // Estagiario
    String instituicaoEnsino, // Estagiario
    Integer periodoAtual // Estagiario
) {}
