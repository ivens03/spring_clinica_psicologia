package psicologia.clinica.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gestores", schema = "perfis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gestor {

    @Id
    @Column(name = "usuario_cpf", length = 11)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_cpf")
    private Usuario usuario;

    @Column(name = "cargo", length = 100)
    private String cargo;
}
