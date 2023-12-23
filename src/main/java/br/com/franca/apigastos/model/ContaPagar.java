package br.com.franca.apigastos.model;

import br.com.franca.apigastos.enums.StatusContaPagar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conta_pagar")
@SequenceGenerator(name = "seq_conta_pagar", sequenceName = "seq_conta_pagar", allocationSize = 1, initialValue = 1)
public class ContaPagar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_conta_pagar")
    private Long id;


    @Column(name = "mes_referencia")
    private String mesReferencia; // Campo para armazenar o mês de referência


    @ManyToOne(targetEntity = Pessoa.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa", nullable = false, foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_pessoa"))
    private Pessoa pessoa;

    @OneToMany(mappedBy = "contaPagar", cascade = CascadeType.ALL)
    private List<Gasto> gastos = new ArrayList<>();

    private String descricao;

    private BigDecimal valor;

    @Column(name = "dt_inclusao", columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private LocalDate dataInclusao;

    @Column(name = "dt_vencimento", columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusContaPagar status;

    public void adicionarGasto(Gasto gasto) {
        if (gastos == null) {
            gastos = new ArrayList<>();
        }
        gastos.add(gasto);
        gasto.setContaPagar(this);
    }

    public void atualizarValorTotal() {
        // Inicializa o valor total como zero
        BigDecimal novoValorTotal = BigDecimal.ZERO;

        // Soma os valores dos gastos associados
        for (Gasto gasto : gastos) {
            novoValorTotal = novoValorTotal.add(gasto.getValorTotal());
        }

        // Define o novo valor total na conta a pagar
        this.valor = novoValorTotal;
    }
}
