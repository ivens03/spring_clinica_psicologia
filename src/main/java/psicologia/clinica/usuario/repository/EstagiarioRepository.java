package psicologia.clinica.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import psicologia.clinica.usuario.model.Estagiario;

@Repository
public interface EstagiarioRepository extends JpaRepository<Estagiario, String> {
}
