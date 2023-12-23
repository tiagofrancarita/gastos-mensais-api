package br.com.franca.apigastos.repository;

import br.com.franca.apigastos.model.CartaoCredito;
import br.com.franca.apigastos.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {

    CartaoCredito findByNumeroCartao(String numeroCartao);

    @Query("SELECT cc FROM CartaoCredito cc WHERE cc.numeroCartao LIKE %:numeroCartao%")
    public List<CartaoCredito> buscarCartaoPorNumero(String numeroCartao);

    @Query("SELECT cc FROM CartaoCredito cc WHERE cc.status = 'VALIDO'")
    List<CartaoCredito> findByStatusAtivoTrue();

    CartaoCredito findByTitular(String titular);

    CartaoCredito findByDataVencimento(LocalDate dataVencimento);

    CartaoCredito findByStatus(String status);

    @Query("SELECT cc FROM CartaoCredito cc WHERE cc.dataVencimento BETWEEN :dataInicio AND :dataFim")
    List<CartaoCredito> findByDataVencimentoBetween(LocalDate dataInicio, LocalDate dataFim);

}