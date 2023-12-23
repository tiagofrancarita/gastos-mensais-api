package br.com.franca.apigastos.controller;

import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.repository.PessoaRepository;
import br.com.franca.apigastos.service.ContaPagarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@Tag(name = "entrypoint-conta-pagar", description = "entrypoint para gerenciamento de contas a pagar")
@RestController
@RequestMapping("/contas-pagar")
public class ContaPagarController {

    @Autowired
    private ContaPagarService contaPagarService;

    @Autowired
    private PessoaRepository pessoaRepository;



    private Logger log = LoggerFactory.getLogger(ContaPagarController.class);

    @PostMapping("/salvarContaPagar")
    @Operation(summary = "Metodo responsavel por salvar uma conta a pagar")
    public ResponseEntity<String> salvarContaPagar(@RequestBody Long idPessoa) {
        try {
            Pessoa pessoa = pessoaRepository.findById(idPessoa)
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

            contaPagarService.processarGastosMensais(pessoa);
            return ResponseEntity.ok("Conta a pagar criada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao criar a conta a pagar.");
        }
    }

    @GetMapping("/{contaPagarId}/gerar-excel")
    @Operation(summary = "Metodo responsavel por gerar um arquivo excel com os detalhes da conta a pagar")
    public ResponseEntity<byte[]> gerarExcelContaPagar(@PathVariable Long contaPagarId) {
        try {
            byte[] excelBytes = contaPagarService.gerarExcelContaPagar(contaPagarId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "detalhes_conta_pagar.xlsx");

            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (IOException e) {
            // Lide com a exceção de IO aqui, por exemplo, registre o erro e retorne uma resposta adequada
            return ResponseEntity.status(500).body("Erro ao gerar o arquivo Excel".getBytes());
        }
    }

    @GetMapping("/gerar-pdf/{idContaPagar}")
    @Operation(summary = "Metodo responsavel por gerar um arquivo pdf com os detalhes da conta a pagar")
    public ResponseEntity<byte[]> gerarPDFContaPagar(@PathVariable Long idContaPagar) {
        try {
            byte[] pdfBytes = contaPagarService.gerarPdfContaPagar(idContaPagar);
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment; filename=conta_pagar_" + idContaPagar + ".pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            // Trate o erro conforme sua lógica, como logar e retornar um erro adequado
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/relatorio-excel/{contaPagarId}/{inicioPeriodo}/{fimPeriodo}")
    public ResponseEntity<byte[]> gerarRelatorioExcelPorContaPagarEData(
            @PathVariable Long contaPagarId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicioPeriodo,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fimPeriodo) {

        try {
            byte[] excelBytes = contaPagarService.gerarRelatorioExcelPorContaPagarEData(contaPagarId, inicioPeriodo, fimPeriodo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "relatorio_gastos.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Trate a exceção adequadamente
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}