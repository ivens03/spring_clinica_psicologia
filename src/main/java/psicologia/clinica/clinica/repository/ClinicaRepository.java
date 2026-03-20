package psicologia.clinica.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import psicologia.clinica.clinica.model.Clinica;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, String> {
}
