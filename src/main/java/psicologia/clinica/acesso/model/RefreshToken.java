package psicologia.clinica.acesso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", schema = "autenticacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private UUID id;

    @Column(name = "usuario_cpf", nullable = false, length = 11)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(name = "emitido_em", nullable = false)
    private LocalDateTime emitidoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "revogado_em")
    private LocalDateTime revogadoEm;

    @Column(length = 45)
    private String ip;

    public boolean expirado(LocalDateTime agora) {
        return !expiraEm.isAfter(agora);
    }

    public boolean revogado() {
        return revogadoEm != null;
    }
}
