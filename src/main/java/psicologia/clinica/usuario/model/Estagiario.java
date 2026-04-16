package psicologia.clinica.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estagiarios", schema = "perfis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estagiario {

    @Id
    @Column(name = "usuario_cpf", length = 11)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_cpf")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_cpf")
    private Funcionario supervisor;

    @Column(name = "instituicao_ensino", length = 255)
    private String instituicaoEnsino;

    @Column(name = "periodo_atual")
    private Integer periodoAtual;
}
