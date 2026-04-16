package psicologia.clinica.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "funcionarios", schema = "perfis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Funcionario {

    @Id
    @Column(name = "usuario_cpf", length = 11)
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    private String usuarioCpf;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "usuario_cpf")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_perfil", nullable = false, length = 50)
    private SubPerfil subPerfil;

    @Column(name = "conselho_classe", length = 50)
    private String conselhoClasse;

    @Column(name = "registro_conselho", length = 50)
    private String registroConselho;
}
