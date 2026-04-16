package psicologia.clinica.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import psicologia.clinica.usuario.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    boolean existsByEmail(String email);
}
