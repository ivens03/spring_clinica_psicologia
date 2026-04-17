package psicologia.clinica.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import psicologia.clinica.usuario.model.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE usuarios.usuarios
               SET email = :email,
                   senha = :senha,
                   atualizado_em = NOW()
             WHERE cpf = :cpf
            """, nativeQuery = true)
    int atualizarCredenciaisDev(@Param("cpf") String cpf, @Param("email") String email, @Param("senha") String senha);
}
