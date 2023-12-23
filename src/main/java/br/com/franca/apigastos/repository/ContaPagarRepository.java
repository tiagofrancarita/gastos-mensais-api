package br.com.franca.apigastos.repository;

import br.com.franca.apigastos.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {


    ContaPagar findByMesReferencia(String mesVigenteGasto);

}
