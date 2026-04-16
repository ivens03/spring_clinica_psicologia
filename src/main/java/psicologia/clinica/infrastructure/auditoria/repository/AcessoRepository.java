package psicologia.clinica.infrastructure.auditoria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import psicologia.clinica.infrastructure.auditoria.model.Acesso;

import java.util.UUID;

@Repository
public interface AcessoRepository extends JpaRepository<Acesso, UUID> {
}
