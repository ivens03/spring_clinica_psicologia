package psicologia.clinica.clinica.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinicas", schema = "clinicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@org.hibernate.annotations.Where(clause = "ativo = true")
public class Clinica {

    @Id
    @Column(name = "identificador_fiscal", length = 14)
    private String identificadorFiscal;

    @Column(name = "nome_exibicao", nullable = false)
    private String nomeExibicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false, length = 20)
    private TipoPessoa tipoPessoa;

    @Column(name = "registro_conselho_clinica", length = 50)
    private String registroConselhoClinica;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_clinica", nullable = false, length = 20)
    private TipoClinica tipoClinica;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    // Auditoria
    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "excluido_em")
    private LocalDateTime excluidoEm;

    @Version
    @Builder.Default
    private Integer versao = 0;
}
