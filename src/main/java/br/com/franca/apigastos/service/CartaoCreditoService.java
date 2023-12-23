package br.com.franca.apigastos.service;

import br.com.franca.apigastos.exceptions.CartaoNotFoundException;
import br.com.franca.apigastos.model.CartaoCredito;
import br.com.franca.apigastos.repository.CartaoCreditoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.List;

@Service
public class CartaoCreditoService {

    private static final String MSG_SUCESSO_DELTE = "Registro excluido com sucesso..";
    private static final String MSG_FALHA = "Operação não realizada.";

    @Autowired
    private CartaoCreditoRepository cartaoCreditoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Validator validator;


    private Logger log = LoggerFactory.getLogger(CartaoCreditoService.class);

    public List<CartaoCredito> listarPessoas() {

        log.info("Inicio do processo listar cartões de crédito.");

        List<CartaoCredito> cartoesAtivos = cartaoCreditoRepository.findByStatusAtivoTrue();

        if (cartoesAtivos.isEmpty()) {
            log.error("Nenhum cartão ativo encontrado.");
            throw new CartaoNotFoundException("Nenhum cartão ativo encontrado");
        }
        log.info("Fim do processo listar cartões de crédito, processo realizado com sucesso.");
        return cartoesAtivos;
    }

    public CartaoCredito salvarCartaoCredito(CartaoCredito cartaoCredito) {

        log.info("Inicio do processo salvar cartão de crédito.");

        if (cartaoCredito.getId() != null) {
            log.error("Cartão de crédito já cadastrado.");
            throw new RuntimeException("Cartão de crédito já cadastrado.");
        }
        if (cartaoCredito.getTitular() == null || cartaoCredito.getTitular().isEmpty()) {
            log.error("Nome do cartão de crédito não informado.");
            throw new RuntimeException("Nome do cartão de crédito não informado.");
        }
        if (cartaoCredito.getNumeroCartao() == null || cartaoCredito.getNumeroCartao().isEmpty()) {
            log.error("Número do cartão de crédito não informado.");
            throw new RuntimeException("Número do cartão de crédito não informado.");
        }
        if (cartaoCredito.getDataVencimento() == null) {
            log.error("Validade do cartão de crédito não informado.");
            throw new RuntimeException("Validade do cartão de crédito não informado.");
        }

        if (cartaoCredito.getNumeroCartao() == null){
            log.info("Realizando busca do numero do cartão na base de dados.");
            List<CartaoCredito> cartoesCadastrados = cartaoCreditoRepository.buscarCartaoPorNumero(cartaoCredito.getNumeroCartao());
                if (cartoesCadastrados.isEmpty()) {
                    log.error("Cartão de crédito já cadastrado.");
                    throw new RuntimeException("Cartão de crédito já cadastrado.");
            }
        }
        cartaoCredito = cartaoCreditoRepository.save(cartaoCredito);
        log.info("Fim do processo salvar cartão de crédito.");
        log.info("Cartão de crédito salvo com sucesso.");
        return cartaoCredito;
    }

    public CartaoCredito buscarCartaoCreditoPorId(Long id) {

        log.info("Inicio do processo buscar cartão de crédito por id.");

        CartaoCredito cartaoCredito = cartaoCreditoRepository.findById(id).orElseThrow(() -> new CartaoNotFoundException("Cartão de crédito não encontrado."));

        log.info("Fim do processo buscar cartão de crédito por id, processo realizado com sucesso.");
        return cartaoCredito;
    }

    public CartaoCredito buscarCartaoCreditoPorNumeroCartao(String numeroCartao) {

        log.info("Inicio do processo buscar cartão de crédito por número.");

        CartaoCredito cartaoCredito = cartaoCreditoRepository.findByNumeroCartao(numeroCartao);

        if (cartaoCredito == null) {
            log.error("Cartão de crédito não encontrado.");
            throw new CartaoNotFoundException("Cartão de crédito não encontrado.");
        }
        log.info("Fim do processo buscar cartão de crédito por número, processo realizado com sucesso.");
        return cartaoCredito;
    }

    public CartaoCredito buscarCartaoCreditoPorTitular(String titular) {

        log.info("Inicio do processo buscar cartão de crédito por titular.");

        CartaoCredito cartaoCredito = cartaoCreditoRepository.findByTitular(titular);

        if (cartaoCredito == null) {
            log.error("Cartão de crédito não encontrado.");
            throw new CartaoNotFoundException("Cartão de crédito não encontrado.");
        }
        log.info("Fim do processo buscar cartão de crédito por titular, processo realizado com sucesso.");
        return cartaoCredito;
    }
/*
    public CartaoCredito buscarCartaoCreditoPorDataVencimento(String dataVencimento) {

        log.info("Inicio do processo buscar cartão de crédito por data de vencimento.");

        CartaoCredito cartaoCredito = cartaoCreditoRepository.findByDataVencimento(dataVencimento);

        if (cartaoCredito == null) {
            log.error("Cartão de crédito não encontrado.");
            throw new CartaoNotFoundException("Cartão de crédito não encontrado.");
        }
        log.info("Fim do processo buscar cartão de crédito por data de vencimento, processo realizado com sucesso.");
        return cartaoCredito;
    }*/

    public CartaoCredito buscarCartaoCreditoPorStatus(String status) {

        log.info("Inicio do processo buscar cartão de crédito por status.");

        CartaoCredito cartaoCredito = cartaoCreditoRepository.findByStatus(status);

        if (cartaoCredito == null) {
            log.error("Cartão de crédito não encontrado.");
            throw new CartaoNotFoundException("Cartão de crédito não encontrado.");
        }
        log.info("Fim do processo buscar cartão de crédito por status, processo realizado com sucesso.");
        return cartaoCredito;
    }

   public ResponseEntity<String> deletarCartaoPorId(Long id) {

        cartaoCreditoRepository.deleteById(id);
        return new ResponseEntity<String>(MSG_SUCESSO_DELTE, HttpStatus.OK);

   }


    public CartaoCredito atualizarCartaoCredito(CartaoCredito cartaoCredito) {

        log.info("Inicio do processo atualizar cartão de crédito.");

        if (cartaoCredito.getId() == null) {
            log.error("Cartão de crédito não encontrado.");
            throw new CartaoNotFoundException("Cartão de crédito não encontrado.");
        }
        if (cartaoCredito.getTitular() == null || cartaoCredito.getTitular().isEmpty()) {
            log.error("Nome do cartão de crédito não informado.");
            throw new RuntimeException("Nome do cartão de crédito não informado.");
        }
        if (cartaoCredito.getNumeroCartao() == null || cartaoCredito.getNumeroCartao().isEmpty()) {
            log.error("Número do cartão de crédito não informado.");
            throw new RuntimeException("Número do cartão de crédito não informado.");
        }
        if (cartaoCredito.getDataVencimento() == null) {
            log.error("Validade do cartão de crédito não informado.");
            throw new RuntimeException("Validade do cartão de crédito não informado.");
        }

        if (cartaoCredito.getNumeroCartao() == null){
            log.info("Realizando busca do numero do cartão na base de dados.");
            List<CartaoCredito> cartoesCadastrados = cartaoCreditoRepository.buscarCartaoPorNumero(cartaoCredito.getNumeroCartao());
            if (cartoesCadastrados.isEmpty()) {
                log.error("Cartão de crédito já cadastrado.");
                throw new RuntimeException("Cartão de crédito já cadastrado.");
            }
        }
        cartaoCredito = cartaoCreditoRepository.save(cartaoCredito);
        log.info("Fim do processo atualizar cartão de crédito.");
        log.info("Cartão de crédito atualizado com sucesso.");
        return cartaoCredito;

    }
}