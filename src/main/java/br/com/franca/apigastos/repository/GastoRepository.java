package br.com.franca.apigastos.repository;

import br.com.franca.apigastos.model.ContaPagar;
import br.com.franca.apigastos.model.Gasto;
import br.com.franca.apigastos.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByDataCompra(LocalDate dataCompra);
    Gasto findByDescricao(String descricao);
    List<Gasto> findByPessoaAndDataCompraBetween(Pessoa pessoa, LocalDate inicio, LocalDate fim);
    List<Gasto> findByContaPagarAndDataCompraBetween(ContaPagar contaPagar, LocalDate inicioMes, LocalDate fimMes);


    List<Gasto> findByContaPagarIdAndDataCompraBetween(Long contaPagarId, LocalDate inicioMes, LocalDate fimMes);


}

