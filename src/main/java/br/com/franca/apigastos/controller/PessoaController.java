package br.com.franca.apigastos.controller;

import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.service.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

/**
 * Classe responsavel por controlar as requisicoes de pessoas
 * @since 18/05/2021
 * @version 1.0
 * @Author Tiago França
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/pessoas")
@Tag(name = "entrypoint-pessoas", description = "entrypoint para gerenciamento de pessoas")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    private Logger log = LoggerFactory.getLogger(PessoaController.class);

    /**
     * Metodo responsavel por cadastrar uma pessoa e usuario no banco de dados
     * @param pessoa
     * @return
     */

    @Operation(summary = "Metodo responsavel por cadastrar uma pessoa e usuario no banco de dados")
    @PostMapping("/cadastrarPessoa")
    public ResponseEntity<Pessoa> cadastrarPessoa(@RequestBody Pessoa pessoa) {

        try {

            log.info("Inicio do processo de cadastro de pessoa.");
            Pessoa novaPessoa = pessoaService.salvarPessoa(pessoa);

            log.info("Pessoa cadastrada com sucesso." + " Codigo: " + novaPessoa.getId());
            return new ResponseEntity<>(novaPessoa, HttpStatus.CREATED);

        }catch (Exception e){
            log.error("Erro ao cadastrar pessoa." + e.getMessage());
            throw new RuntimeException("Erro ao cadastrar pessoa." + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por listar todas as pessoas cadastradas no banco de dados
     * @return
     */

    @Operation(summary = "Metodo responsavel por listar todas as pessoas cadastradas no banco de dados")
    @GetMapping("listarPessoasCadastradas")
    public List<Pessoa> listarPessoasCadastradas() {

        try {
            log.info("Inicio do processo de listagem de pessoas.");
            List<Pessoa> listarPessoasCadastradas = pessoaService.listarPessoas();
            log.info("Listagem de pessoas realizada com sucesso.");
            return pessoaService.listarPessoas();
        }catch (Exception e){
            log.error("Erro ao listar pessoas." + e.getMessage());
            throw new RuntimeException("Erro ao listar pessoas." + e.getMessage());
        }
    }

    /**
     * Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo id
     * @param id
     * @return
     */

    @Operation(summary = "Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo id")
    @GetMapping("/buscarPessoaPorId/{id}")
    public ResponseEntity<Pessoa> buscarPessoaPorId(@PathVariable("id") Long id) {

        log.info("Inicio do processo de busca de pessoa por id.");

        Pessoa buscaPessoaCadastradaPorId = pessoaService.buscarPessoaPorId(id);

        if (buscaPessoaCadastradaPorId == null) {
            log.error("Codigo informado não existe no banco de dados." + " Codigo: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa encontrada com sucesso." + " Codigo: " + id);
            return new ResponseEntity<>(buscaPessoaCadastradaPorId, HttpStatus.OK);

        }
    }

    /**
     * Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo cpf
     * @param cpf
     * @return
     */

    @Operation(summary = "Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo cpf")
    @GetMapping("/buscarPessoaPorCpf/{cpf}")
    public ResponseEntity<Pessoa> buscarPessoaPorCpf(@PathVariable("cpf") String cpf) {

        log.info("Inicio do processo de busca de pessoa por cpf.");

        Pessoa buscarPessoaPorCpf = pessoaService.buscarPessoaPorCpf(cpf);

        if (buscarPessoaPorCpf == null) {
            log.error("Cpf informado não existe no banco de dados." + " CPF: " + cpf);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa encontrada com sucesso." + " CPF: " + cpf);
            return new ResponseEntity<>(buscarPessoaPorCpf, HttpStatus.OK);
        }
    }

    /**
     * Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo email
     * @param email
     * @return
     */

    @Operation(summary = "Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo cpf")
    @GetMapping("/buscarPessoaPorEmail/{email}")
    public ResponseEntity<Pessoa> buscarPessoaPorEmail(@PathVariable("email") String email) {

        log.info("Inicio do processo de busca de pessoa por email.");

        Pessoa buscarPessoaPorEmail = pessoaService.buscarPessoaPorEmail(email);

        if (buscarPessoaPorEmail == null) {
            log.error("E-mail informado não existe no banco de dados." + " Email: " + email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa encontrada com sucesso." + " Email: " + email);
            return new ResponseEntity<>(buscarPessoaPorEmail, HttpStatus.OK);
        }
    }

    /**
     * Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo id e email
     * @param id
     * @param email
     * @return
     */

    @Operation(summary = "Metodo responsavel por buscar uma pessoa cadastrada no banco de dados pelo cpf")
    @GetMapping("/buscarPessoaPorIdAndEmail/{id}/{email}")
    public ResponseEntity<Pessoa> buscarPessoaPorIdAndEmail(@PathVariable("id") Long id , @PathVariable("email") String email) {

        log.info("Inicio do processo de busca de pessoa por id e email.");

        Pessoa buscarPessoaPorIdEmail = pessoaService.buscarPessoaPorIdAndEmail(id, email);

        if (buscarPessoaPorIdEmail == null) {
            log.error("E-mail informado não existe no banco de dados." + " Email: " + email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa encontrada com sucesso." + " Email: " + email);
            return new ResponseEntity<>(buscarPessoaPorIdEmail, HttpStatus.OK);
        }
    }

    /**
     * Metodo responsavel por buscar uma pessoa cadastrada no banco de dados por um intervalo de datas
     * @param dataInicio
     * @param dataFim
     * @return
     */
    @ResponseBody
    @Operation(summary = "Metodo responsavel por buscar uma pessoa cadastrada no banco de dados por um intervalo de datas")
    @GetMapping("/buscarPessoaPorDataCadastro/{dataInicio}/{dataFim}")
    public ResponseEntity<List<Pessoa>> buscarPessoaPorDataCadastro(@PathVariable("dataInicio") LocalDate dataInicio , @PathVariable("dataFim") LocalDate dataFim) throws ParseException {

        log.info("Inicio do processo de busca de pessoa pela dara de cadastro.");

        List<Pessoa> buscarPessoaPorDataCadastro = pessoaService.buscarPessoaPorDataCadastro(dataInicio, dataFim);

        if (buscarPessoaPorDataCadastro == null) {
            log.error("Nenhuma pessoa encontrada no intervalo de datas informado." + " Data Inicio: " + dataInicio + " Data Fim: " + dataFim);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa encontrada com sucesso." + " Data Inicio: " + dataInicio + " Data Fim: " + dataFim);
            return new ResponseEntity<>(buscarPessoaPorDataCadastro, HttpStatus.OK);
        }
    }

    /**
     * Metodo responsavel por inativar uma pessoa cadastrada no banco de dados
     * @param id
     * @return
     */
    @Operation(summary = "Metodo responsavel por inativar uma pessoa cadastrada no banco de dados")
    @PostMapping("/inativarPessoa/{id}")
    public ResponseEntity<Pessoa> inativarPessoa(@PathVariable("id") Long id) {


        log.info("Inicio do processo de inativação de pessoa por id");

        Pessoa inativarPessoa = pessoaService.inativarPessoa(id);

        if (inativarPessoa == null) {
            log.error("Código informado não existe no banco de dados." + " Codigo: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa inativada com sucesso." + " Codigo: " + id);
            return new ResponseEntity<>(inativarPessoa, HttpStatus.OK);
        }
    }

    /**
     * Metodo responsavel por ativar uma pessoa cadastrada no banco de dados
     * @param id
     * @return
     */
    @Operation(summary = "Metodo responsavel por ativar uma pessoa cadastrada no banco de dados")
    @PostMapping("/ativarPessoa/{id}")
    public ResponseEntity<Pessoa> ativarPessoa(@PathVariable("id") Long id) {


        log.info("Inicio do processo de ativação de pessoa por id");

        Pessoa inativarPessoa = pessoaService.ativarPessoa(id);

        if (inativarPessoa == null) {
            log.error("Código informado não existe no banco de dados." + " Codigo: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Pessoa ativa com sucesso." + " Codigo: " + id);
            return new ResponseEntity<>(inativarPessoa, HttpStatus.OK);
        }
    }
}