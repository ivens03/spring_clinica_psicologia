package psicologia.clinica.acesso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologia.clinica.acesso.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
