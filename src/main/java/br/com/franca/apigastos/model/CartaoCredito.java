package br.com.franca.apigastos.model;


import br.com.franca.apigastos.enums.StatusCartaoCredito;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cartao_credito")
@SequenceGenerator(name = "seq_cartao_credito", sequenceName = "seq_cartao_credito", allocationSize = 1, initialValue = 1)
public class CartaoCredito implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cartao_credito")
    private Long id;

    @CreditCardNumber(message = "O numero do cartão não é válido")
    @NotBlank(message = "O numero do cartão não pode ser em branco")
    @Column(name = "numero_cartao", nullable = false)
    private String numeroCartao;

    @NotBlank(message = "O nome do titular não pode ser em branco")
    @Column(name = "titular_cartao", nullable = false)
    private String titular;

    @Column(name = "dt_vencimento", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCartaoCredito status;

    @OneToMany(mappedBy = "cartaoCredito", cascade = CascadeType.ALL)
    private List<Gasto> gastos;

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

}