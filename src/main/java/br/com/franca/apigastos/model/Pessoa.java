package br.com.franca.apigastos.model;

import br.com.franca.apigastos.enums.StatusPessoaEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pessoa")
@SequenceGenerator(name = "seq_pessoa", sequenceName = "seq_pessoa", allocationSize = 1, initialValue = 1)
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
    private Long id;

    @NotBlank(message = "O nome não pode ser em branco")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Past(message = "A data de nascimento não pode ser futura")
    @Column(name = "data_nascimento", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate dataNascimento;

    @NotBlank(message = "O CPF não pode ser em branco")
    @CPF(message = "O CPF informado não é válido")
    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @NotBlank(message = "O email não pode ser em branco")
    @Email(message = "O email informado não é válido")
    @Column(name = "email", nullable = false)
    private String email;

    private LocalDate dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPessoaEnum status;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
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

    @Override
    public String toString() {
        return "Pessoa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", status=" + status +
                '}';
    }


}
