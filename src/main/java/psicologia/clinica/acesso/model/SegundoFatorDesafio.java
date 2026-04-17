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
@Table(name = "segundo_fator_desafios", schema = "autenticacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegundoFatorDesafio {

    @Id
    private UUID id;

    @Column(name = "usuario_cpf", nullable = false, length = 11)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @Column(name = "codigo_hash", nullable = false, length = 128)
    private String codigoHash;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado_em")
    private LocalDateTime usadoEm;

    @Column(length = 45)
    private String ip;

    public boolean expirado(LocalDateTime agora) {
        return !expiraEm.isAfter(agora);
    }

    public boolean usado() {
        return usadoEm != null;
    }
}
