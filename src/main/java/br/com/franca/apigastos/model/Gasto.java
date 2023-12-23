package br.com.franca.apigastos.model;

import br.com.franca.apigastos.enums.TipoGastoEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gasto")
@SequenceGenerator(name = "seq_gasto", sequenceName = "seq_gasto", allocationSize = 1, initialValue = 1)
public class Gasto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq_gasto")
    private Long id;

    @Column(name = "descrição_gasto", nullable = false)
    private String descricao;

    @Column(name = "dt_compra", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dataCompra;

    @Column(name = "numero_parcelas", nullable = false)
    private int numeroParcelas;

    @Range(min = 1, message = "O valor total da venda deve ser no minimo R$ 1.00")
    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "valor_parcelado", nullable = false)
    private BigDecimal valorParcelado;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_pessoa"))
    private Pessoa pessoa;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cartao", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_cartao"))
    private CartaoCredito cartaoCredito;

    @Column(name = "numero_cartao", nullable = false)
    private String numeroCartao;

    @Setter
    @ManyToOne
    @JoinColumn(name = "id_conta_pagar", nullable = true)
    private ContaPagar contaPagar;

    @Column(name = "parcelado", nullable = false)
    private Boolean parcelado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true,name = "tipo_gasto")
    private TipoGastoEnum status;

    @Column(name = "numero_parcela_atual", nullable = true)
    private Integer numeroParcelaAtual;

    @Column(name = "dt_inclusao", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dataInclusao;

    @Column(name = "mes_vigente_gasto")
    private String mesVigenteGasto;


    public boolean isParcelado() {
        return parcelado;
    }



    @Transient
    public BigDecimal calcularValorParcela() {
        if (numeroParcelas > 0) {
            return valorTotal.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Transient
    public String gerarNumeroCartao() {

        if (cartaoCredito == null) {
            String numeroCompleto = cartaoCredito.getNumeroCartao();
            String ultimosDigitos = numeroCompleto.substring(numeroCompleto.length() - 4);
            String diaVencimento = String.format("%02d", cartaoCredito.getDataVencimento());
            String primeiraLetraTitular = cartaoCredito.getTitular().substring(0, 1).toUpperCase();

            return primeiraLetraTitular + ultimosDigitos + diaVencimento;


        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gasto gasto)) return false;
        return Objects.equals(getId(), gasto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Gasto{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", dataCompra=" + dataCompra +
                ", numeroParcelas=" + numeroParcelas +
                ", valorTotal=" + valorTotal +
                ", valorParcelado=" + valorParcelado +
                ", pessoa=" + (pessoa != null ? pessoa.getId() : null) +
                ", cartaoCredito=" + (cartaoCredito != null ? cartaoCredito.getId() : null) +
                ", numeroCartao='" + numeroCartao + '\'' +
                ", contaPagar=" + (contaPagar != null ? contaPagar.getId() : null) +
                ", parcelado=" + parcelado +
                ", status=" + status +
                '}';
    }
}