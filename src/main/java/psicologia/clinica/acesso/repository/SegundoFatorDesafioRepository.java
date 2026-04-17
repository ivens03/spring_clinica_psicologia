package psicologia.clinica.acesso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologia.clinica.acesso.model.SegundoFatorDesafio;

import java.util.UUID;

public interface SegundoFatorDesafioRepository extends JpaRepository<SegundoFatorDesafio, UUID> {
}
