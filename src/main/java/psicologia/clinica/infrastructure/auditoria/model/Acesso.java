package psicologia.clinica.infrastructure.auditoria.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "acessos", schema = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acesso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_cpf", length = 11, nullable = false)
    @JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @Column(name = "clinica_id", length = 14, nullable = false)
    private String clinicaId;

    @Builder.Default
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "acao", length = 100)
    private String acao;

    @Column(name = "detalhes", columnDefinition = "TEXT")
    private String detalhes;
}
