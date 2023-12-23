package br.com.franca.apigastos.controller;


import br.com.franca.apigastos.model.CartaoCredito;
import br.com.franca.apigastos.service.CartaoCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Classe responsavel por controlar as requisicoes de cartao de credito
 * @since 18/05/2021
 * @version 1.0
 * @Author Tiago França
 */

@RestController
@RequestMapping("/api/cartoesCredito")
@Tag(name = "entrypoint-cartao-credito", description = "entrypoint para gerenciamento de cartoes de credito")
public class CartaoCreditoController {

    private static final String MSG_SUCESSO = "Operação realizada com sucesso.";
    private static final String MSG_SUCESSO_EXCLUSAO = "Registro excluido com sucesso..";
    private static final String MSG_FALHA = "Operação não realizada.";

    @Autowired
    private CartaoCreditoService cartaoCreditoService;

    private Logger log = LoggerFactory.getLogger(CartaoCreditoController.class);

    /**
     * Metodo responsavel por listar todas as pessoas cadastradas no banco de dados
     * @return
     */

    @Operation(summary = "Metodo responsavel por listar todas as pessoas cadastradas no banco de dados")
    @GetMapping("/listarCartaoCredito")
    public List<CartaoCredito> listarCartaoCredito(){

        return cartaoCreditoService.listarPessoas();
    }

      /**
      * Metodo responsavel cadastrar um cartão de credito
      * @param cartaoCredito
      * @return
      */
    @Operation(summary = "Metodo responsavel cadastrar um cartão de credito")
    @PostMapping("/cadastrarCartaoCredito")
    public CartaoCredito cadastrarCartaoCredito(CartaoCredito cartaoCredito){

        return cartaoCreditoService.salvarCartaoCredito(cartaoCredito);
    }

    /**
     * Metodo responsavel por atualizar um cartão de credito
     * @param cartaoCredito
     * @return
     */
    @Operation(summary = "Metodo responsavel por atualizar um cartão de credito")
    @PutMapping("/atualizarCartaoCredito")
    public CartaoCredito atualizarCartaoCredito(CartaoCredito cartaoCredito){

        return cartaoCreditoService.atualizarCartaoCredito(cartaoCredito);
    }

    /**
     * Metodo responsavel por deletar um cartão de credito
     *
     * @param id
     * @return
     */
    @Operation(summary = "Metodo responsavel por deletar um cartão de credito")
    @DeleteMapping("/deletarCartaoCredito/{id}")
    public ResponseEntity<String> deletarCartaoCredito(@PathVariable Long id){

            CartaoCredito buscaId = cartaoCreditoService.buscarCartaoCreditoPorId(id);

            if (buscaId.getId() == null) {
                log.error("Cartão de crédito não encontrado.");
                throw new RuntimeException("Cartão de crédito não encontrado.");
            }

            cartaoCreditoService.deletarCartaoPorId(buscaId.getId());
            return new ResponseEntity<>(MSG_SUCESSO_EXCLUSAO, HttpStatus.OK);

    }


    /**
     * Metodo responsavel por buscar um cartão de credito por numero cartao
     * @param numeroCartao
     * @return
     */
    @Operation(summary = "Metodo responsavel por buscar um cartão de credito por id")
    @GetMapping("/buscarCartaoCreditoPorNumeroCartao/{numeroCartao}")
    public CartaoCredito buscarCartaoCreditoPorNumeroCartao(@PathVariable String numeroCartao){

        return cartaoCreditoService.buscarCartaoCreditoPorNumeroCartao(numeroCartao);
    }

    /**
     * Metodo responsavel por buscar um cartão de credito por nome titular
     * @param nomeTitular
     * @return
     */
    @Operation(summary = "Metodo responsavel por buscar um cartão de credito por id")
    @GetMapping("/buscarCartaoCreditoPorNomeTitular/{nomeTitular}")
    public CartaoCredito buscarCartaoCreditoPorNomeTitular(@PathVariable String nomeTitular){

        return cartaoCreditoService.buscarCartaoCreditoPorTitular(nomeTitular);
    }

    /**
     * Metodo responsavel por buscar um cartão de credito por status
     * @param status
     * @return
     */
    @Operation(summary = "Metodo responsavel por buscar um cartão de credito por id")
    @GetMapping("/buscarCartaoCreditoPorStatus/{status}")
    public CartaoCredito buscarCartaoCreditoPorStatus(@PathVariable String status){

        return cartaoCreditoService.buscarCartaoCreditoPorStatus(status);
    }
}
