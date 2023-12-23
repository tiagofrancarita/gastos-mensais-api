package br.com.franca.apigastos.controller;

import br.com.franca.apigastos.model.Gasto;
import br.com.franca.apigastos.repository.GastoRepository;
import br.com.franca.apigastos.service.GastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe responsavel por controlar as requisicoes de gastos
 * @since 18/05/2021
 * @version 1.0
 * @Author Tiago França
 */

@RestController
@RequestMapping("/api/gastos")
@Tag(name = "entrypoint-gastos", description = "entrypoint para gerenciamento de gastos mensais")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private GastoRepository gastoRepository;

    private Logger log = LoggerFactory.getLogger(GastoController.class);


    @GetMapping("/atualizar-id-conta-pagar")
    @Operation(summary = "Atualizar gastos sem conta a pagar associada para o mês vigente")
    public ResponseEntity<String> atualizarIdContaPagarEmGastos() {
     return null;
    }

    @Operation(summary = "Metodo responsavel por listar todaos os gastos cadastrados")
    @GetMapping("/listarGastosPorData")
    public ResponseEntity<List<Gasto>> listarGastosPorData(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCompra) {

        try {
            List<Gasto> gastos = gastoService.listarGastoPorMes(dataCompra);
            return new ResponseEntity<>(gastos, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Erro ao listar gastos por data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Metodo responsavel por listar todaos os gastos cadastrados")
    @GetMapping("/listarGastosCadastrados")
    public ResponseEntity<List<Gasto>> listarGastosCadastrados() {

        try {
            List<Gasto> gastos = gastoService.listarGastosCadastrados();
            return new ResponseEntity<>(gastos, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Erro ao listar gastos cadastrados: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Metodo responsavel por cadastrar um gasto")
    @PostMapping("/cadastrarGasto")
    public ResponseEntity<String> cadastrarGasto(@RequestBody Gasto gasto) {
        try {
            gastoService.salvarGasto(gasto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Gasto cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            log.error("Erro ao cadastrar gasto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Erro interno ao cadastrar gasto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao cadastrar gasto");
        }
    }

    @Operation(summary = "Metodo responsavel por deletar um gasto")
    @DeleteMapping("/deletarGastoPorId/{id}")
    public ResponseEntity<String> deletarGastoPorId(@PathVariable Long id) {
        try {
            gastoService.deletarGastoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body("Gasto deletado com sucesso");
        } catch (IllegalArgumentException e) {
            log.error("Erro ao deletar gasto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            log.error("Erro interno ao deletar gasto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno ao deletar gasto");
        }
    }

    @Operation(summary = "Metodo responsavel por buscar um gasto por id")
    @GetMapping("/buscarGastoPorId/{id}")
    public ResponseEntity<Gasto> buscarGastoPorId(@PathVariable Long id) {
        try {
            Gasto gasto = gastoService.buscarGastoPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(gasto);
        } catch (IllegalArgumentException e) {
            log.error("Erro ao buscar gasto por id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            log.error("Erro interno ao buscar gasto por id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}