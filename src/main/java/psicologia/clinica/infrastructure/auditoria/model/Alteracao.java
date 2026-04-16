package psicologia.clinica.infrastructure.auditoria.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alteracoes", schema = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_cpf", length = 11, nullable = false)
    @JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @Column(name = "clinica_id", length = 14, nullable = false)
    private String clinicaId;

    @Column(name = "tabela_nome", length = 100)
    private String tabelaNome;

    @Column(name = "registro_id", length = 100)
    private String registroId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valor_antigo", columnDefinition = "jsonb")
    private String valorAntigo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "valor_novo", columnDefinition = "jsonb")
    private String valorNovo;

    @Builder.Default
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();
}
