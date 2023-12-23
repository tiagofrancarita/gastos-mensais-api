package br.com.franca.apigastos.repository;

import br.com.franca.apigastos.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByIdAndEmail(Long id, String email);

    Pessoa findByEmail(String email);

    Pessoa findByCpf(String cpf);

    @Query("SELECT p FROM Pessoa p WHERE p.dataCadastro BETWEEN :dataInicio AND :dataFim")
    List<Pessoa> findByDataCadastroBetween(LocalDate dataInicio, LocalDate dataFim);

}