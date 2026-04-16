package psicologia.clinica.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import psicologia.clinica.usuario.model.Gestor;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, String> {
}
