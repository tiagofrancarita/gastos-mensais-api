package br.com.franca.apigastos.service;

import br.com.franca.apigastos.enums.StatusContaPagar;
import br.com.franca.apigastos.model.ContaPagar;
import br.com.franca.apigastos.model.Gasto;
import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.repository.ContaPagarRepository;
import br.com.franca.apigastos.repository.GastoRepository;
import br.com.franca.apigastos.repository.PessoaRepository;
import br.com.franca.apigastos.utils.dtos.GastoDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ContaPagarService {

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    private Logger log = LoggerFactory.getLogger(ContaPagarService.class);

    public ContaPagar obterOuCriarContaPagarParaOMes(Pessoa pessoa, LocalDate dataInclusao) {

        return null;
    }

    public void processarGastosMensais(Pessoa pessoa) {

        pessoa = pessoaRepository.findById(pessoa.getId())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        log.info("Processando gastos mensais para a pessoa: " + pessoa.getNome());

        // Obtém o início e o fim do mês atual como LocalDate
        LocalDate inicioMes = getInicioMes();
        LocalDate fimMes = getFimMes();

        // Obtém os gastos da pessoa no mês atual
        List<Gasto> gastosDoMes = gastoRepository.findByPessoaAndDataCompraBetween(pessoa, inicioMes, fimMes);

        if (gastosDoMes.isEmpty()) {
            throw new IllegalArgumentException("Não há gastos para a pessoa no mês atual.");
        }

        // Cria uma nova conta a pagar
        ContaPagar contaPagar = new ContaPagar();
        contaPagar.setPessoa(pessoa);
        //contaPagar.setDataInclusao(new Date());
        //contaPagar.setDataVencimento(Date.from(fimMes.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        contaPagar.setDescricao("Conta de " + pessoa.getNome() + " do mês " + (fimMes.getMonthValue()) + "/" + (fimMes.getYear()));
        contaPagar.setStatus(StatusContaPagar.PENDENTE);

        // Calcula o valor total da conta considerando apenas a parcela do mês vigente para gastos parcelados
        BigDecimal valorTotal = gastosDoMes.stream()
                .map(gasto -> {
                    if (Boolean.TRUE.equals(gasto.getParcelado())) {
                        // Se for um gasto parcelado, verifica se a parcela é do mês vigente
                        if (gasto.getDataCompra().isBefore(fimMes.plusMonths(1)) && gasto.getDataCompra().isAfter(inicioMes.minusDays(1))) {
                            return gasto.getValorParcelado();
                        } else {
                            return BigDecimal.ZERO; // Se não for do mês vigente, considera valor zero
                        }
                    } else {
                        // Se não for parcelado, soma o valor total
                        return gasto.getValorTotal();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        contaPagar.setValor(valorTotal);

        // Salva a conta a pagar no banco de dados
        contaPagarRepository.save(contaPagar);

        // Atualiza os gastos com o ID da conta a pagar
        List<Gasto> gastosAtualizados = gastosDoMes.stream()
                .map(gasto -> {
                    gasto.setContaPagar(contaPagar);
                    return gastoRepository.save(gasto);
                })
                .collect(Collectors.toList());

        // Atualiza a lista de gastos na conta a pagar
        contaPagar.setGastos(gastosAtualizados);
        contaPagarRepository.save(contaPagar);
    }


    public byte[] gerarPdfContaPagar(Long contaPagarId) throws DocumentException {
        Optional<ContaPagar> optionalContaPagar = contaPagarRepository.findById(contaPagarId);

        log.info("Gerando PDF para a conta a pagar de ID: " + contaPagarId);

        if (optionalContaPagar.isPresent()) {
            ContaPagar contaPagar = optionalContaPagar.get();

            log.info("Conta a pagar encontrada: " + contaPagar);

            // Criação do documento PDF
            log.info("Criando documento PDF");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);

            document.open();

            // Adiciona informações da conta a pagar ao PDF
            document.add(new Paragraph("Descrição: " + contaPagar.getDescricao()));
            document.add(new Paragraph("Valor Total: " + contaPagar.getValor()));
            document.add(new Paragraph("Data de Vencimento: " + contaPagar.getDataVencimento()));
            document.add(new Paragraph("Status: " + contaPagar.getStatus()));


            // Obtém o início e o fim do mês atual como LocalDate
            LocalDate inicioMes = getInicioMes();
            LocalDate fimMes = getFimMes();

            // Obtém os gastos da pessoa no mês atual
            List<Gasto> gastosDoMes = gastoRepository.findByPessoaAndDataCompraBetween(contaPagar.getPessoa(), inicioMes, fimMes);

            // Cria uma tabela para os detalhes dos gastos
            PdfPTable table = new PdfPTable(5); // 3 colunas
            table.setWidthPercentage(100);

            // Defina os títulos das colunas
            table.addCell("Descrição");
            table.addCell("Valor");
            table.addCell("Data de Compra");
            table.addCell("Parcelado");
            table.addCell("Tipo");
            table.addCell("Numero Parcelas");

            // Adiciona os detalhes dos gastos à tabela
            for (Gasto gasto : gastosDoMes) {
                GastoDTO gastoDTO = GastoDTO.fromEntity(gasto);

                PdfPCell cellDescricao = new PdfPCell(new Phrase(" " + gastoDTO.getDescricao()));
                PdfPCell cellValor = new PdfPCell(new Phrase("R$ : " + (Boolean.TRUE.equals(gastoDTO.getParcelado()) ? gastoDTO.getValorParcelado() : gastoDTO.getValorTotal())));
                PdfPCell cellDataCompra = new PdfPCell(new Phrase("" + gastoDTO.getDataCompra()));
                PdfPCell cellParcelado = new PdfPCell(new Phrase("" + gastoDTO.getParcelado()));
                PdfPCell cellTipo = new PdfPCell(new Phrase("" + gastoDTO.getStatus()));
                PdfPCell cellNumParcelas = new PdfPCell(new Phrase(" Parcelas: " + gastoDTO.getNumeroParcelaAtual() + " de " + gastoDTO.getNumeroParcelas()));


                table.addCell(cellDescricao);
                table.addCell(cellValor);
                table.addCell(cellDataCompra);
                table.addCell(cellParcelado);
                table.addCell(cellTipo);
                table.addCell(cellNumParcelas);
            }

            // Adicione a tabela ao documento
            document.add(table);

            // Fechamento do documento PDF
            document.close();
            return baos.toByteArray();
        } else {
            throw new RuntimeException("Conta a pagar não encontrada com o ID: " + contaPagarId);
        }
    }



    /**
     * Metodo responsavel por gerar um arquivo Excel com os detalhes da conta a pagar
     * @param contaPagarId
     * @return
     * @throws IOException
     */
        public byte[] gerarExcelContaPagar (Long contaPagarId) throws IOException {
            Optional<ContaPagar> optionalContaPagar = contaPagarRepository.findById(contaPagarId);

            log.info("Gerando Excel para a conta a pagar de ID: " + contaPagarId);

            if (optionalContaPagar.isPresent()) {
                ContaPagar contaPagar = optionalContaPagar.get();

                log.info("Conta a pagar encontrada: " + contaPagar);

                // Criação do arquivo Excel
                log.info("Criando arquivo Excel");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Detalhes da Conta Pagar");

                // Adiciona informações da conta a pagar ao Excel
                int rowNum = 0;
                Row row = sheet.createRow(rowNum++);

                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
                Cell cell = row.createCell(0);
                cell.setCellValue(contaPagar.getDescricao());

                // Mescla a linha A2 até R2
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));
                // Cria a linha A2 até R2
                Row mergedRow = sheet.createRow(rowNum++);
                // Popula a célula mesclada com o valor total a pagar
                Cell mergedCell = mergedRow.createCell(0);
                mergedCell.setCellValue("Valor Total a Pagar R$ : " + contaPagar.getValor());

                // Mescla a linha A3 até R3
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));

                // Cria a linha A3 até R3
                Row mergedRow2 = sheet.createRow(rowNum++);
                Cell mergedCell2 = mergedRow2.createCell(0);
                mergedCell2.setCellValue("Data de Vencimento: " + contaPagar.getDataVencimento().toString());

                // Mescla a linha A4 até R4
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));

                // Cria a linha A4 até R4
                Row mergedRow3 = sheet.createRow(rowNum++);
                Cell mergedCell3 = mergedRow3.createCell(0);
                mergedCell3.setCellValue("Status: " + contaPagar.getStatus().toString());

                // Pule algumas linhas para dar espaço às informações anteriores
                rowNum += 1;

                // Adiciona "Descrição" na célula A5
                Row rowDescricao = sheet.createRow(rowNum++);
                Cell cellLabelDescricao = rowDescricao.createCell(0);
                cellLabelDescricao.setCellValue("Descrição");

                Cell cellLabelValor = rowDescricao.createCell(1);
                cellLabelValor.setCellValue("Valor");

                Cell cellLabelDtCompra = rowDescricao.createCell(2);
                cellLabelDtCompra.setCellValue("Dt de Compra");

                Cell cellLabelParcelado = rowDescricao.createCell(3);
                cellLabelParcelado.setCellValue("Parcelado");

                Cell cellLabelTpGasto = rowDescricao.createCell(4);
                cellLabelTpGasto.setCellValue("Tipo Gasto");

                Cell cellLabelQtdPacelas = rowDescricao.createCell(5);
                cellLabelQtdPacelas.setCellValue("Qtd Parcelas");

                log.info("Obtendo o início e o fim do mês atual como LocalDate");
                LocalDate inicioMes = getInicioMes();
                LocalDate fimMes = getFimMes();

                log.info("Datas de início e fim do mês: " + inicioMes + " - " + fimMes);

                log.info("Obtendo os gastos da pessoa no mês atual");
                List<Gasto> gastosDoMes = gastoRepository.findByContaPagarAndDataCompraBetween(contaPagar, inicioMes, fimMes);

                log.info("Número de gastos do mês: " + gastosDoMes.size());

                log.info("Adicionando detalhes dos gastos à planilha");
                for (Gasto gasto : gastosDoMes) {
                    GastoDTO gastoDTO = GastoDTO.fromEntity(gasto);

                    log.info("Detalhes do gasto: " + gastoDTO);

                    row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(gastoDTO.getDescricao());
                    row.createCell(1).setCellValue("R$ " + (gastoDTO.getParcelado() ? gastoDTO.getValorParcelado().toString() : gastoDTO.getValorTotal().toString()));
                    row.createCell(2).setCellValue(gastoDTO.getDataCompra().toString());
                    row.createCell(3).setCellValue("R$ " + gastoDTO.getParcelado().toString());
                    row.createCell(4).setCellValue(gastoDTO.getStatus() != null ? gastoDTO.getStatus().toString() : "");
                    row.createCell(5).setCellValue("Parcelas: " + gastoDTO.getNumeroParcelaAtual() + " de " + gastoDTO.getNumeroParcelas());

                    log.info("Gasto adicionado à planilha: ");
                }

                // Auto size columns
                for (int i = 0; i < 6; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Escrever o arquivo Excel para o ByteArrayOutputStream
                workbook.write(baos);
                workbook.close();
                log.info("Arquivo Excel criado com sucesso.");

                return baos.toByteArray();
            } else {
                throw new RuntimeException("Conta a pagar não encontrada com o ID: " + contaPagarId);
            }
        }

    public byte[] gerarRelatorioExcelPorContaPagarEData(Long contaPagarId, LocalDate inicioPeriodo, LocalDate fimPeriodo) throws IOException {

        if (inicioPeriodo == null || fimPeriodo == null) {
            throw new IllegalArgumentException("As datas de início e fim do período são obrigatórias.");
        } else if (inicioPeriodo.isAfter(fimPeriodo)) {
            throw new IllegalArgumentException("A data de início do período deve ser anterior à data de fim do período.");
        } else {

            log.info("Gerando Excel para a conta a pagar de ID: " + contaPagarId);

            Optional<ContaPagar> optionalContaPagar = contaPagarRepository.findById(contaPagarId);


            if (optionalContaPagar.isPresent()) {
                ContaPagar contaPagar = optionalContaPagar.get();

                log.info("Conta a pagar encontrada: " + contaPagar);

                // Criação do arquivo Excel
                log.info("Criando arquivo Excel");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Detalhes da Conta Pagar");

                // Adiciona informações da conta a pagar ao Excel
                int rowNum = 0;
                Row row = sheet.createRow(rowNum++);

                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
                Cell cell = row.createCell(0);
                cell.setCellValue(contaPagar.getDescricao());

                // Mescla a linha A2 até R2
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));
                // Cria a linha A2 até R2
                Row mergedRow = sheet.createRow(rowNum++);
                // Popula a célula mesclada com o valor total a pagar
                Cell mergedCell = mergedRow.createCell(0);
                mergedCell.setCellValue("Valor Total a Pagar R$ : " + contaPagar.getValor());

                // Mescla a linha A3 até R3
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));

                // Cria a linha A3 até R3
                Row mergedRow2 = sheet.createRow(rowNum++);
                Cell mergedCell2 = mergedRow2.createCell(0);
                mergedCell2.setCellValue("Data de Vencimento: " + contaPagar.getDataVencimento().toString());

                // Mescla a linha A4 até R4
                sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 7));

                // Cria a linha A4 até R4
                Row mergedRow3 = sheet.createRow(rowNum++);
                Cell mergedCell3 = mergedRow3.createCell(0);
                mergedCell3.setCellValue("Status: " + contaPagar.getStatus().toString());

                // Pule algumas linhas para dar espaço às informações anteriores
                rowNum += 1;

                // Adiciona "Descrição" na célula A5
                Row rowDescricao = sheet.createRow(rowNum++);
                Cell cellLabelDescricao = rowDescricao.createCell(0);
                cellLabelDescricao.setCellValue("Descrição");

                Cell cellLabelValor = rowDescricao.createCell(1);
                cellLabelValor.setCellValue("Valor");

                Cell cellLabelDtCompra = rowDescricao.createCell(2);
                cellLabelDtCompra.setCellValue("Dt de Compra");

                Cell cellLabelParcelado = rowDescricao.createCell(3);
                cellLabelParcelado.setCellValue("Parcelado");

                Cell cellLabelTpGasto = rowDescricao.createCell(4);
                cellLabelTpGasto.setCellValue("Tipo Gasto");

                Cell cellLabelQtdPacelas = rowDescricao.createCell(5);
                cellLabelQtdPacelas.setCellValue("Qtd Parcelas");

                log.info("Obtendo o início e o fim do mês atual como LocalDate");
                LocalDate inicioMes = inicioPeriodo;
                LocalDate fimMes = fimPeriodo;

                CreationHelper helper = workbook.getCreationHelper();


                CellStyle estiloLinhaPar = workbook.createCellStyle();
                estiloLinhaPar.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex()); // Azul claro
                estiloLinhaPar.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                CellStyle estiloLinhaImpar = workbook.createCellStyle();
                estiloLinhaImpar.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Cinza claro
                estiloLinhaImpar.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                log.info("Datas de início e fim do mês: " + inicioMes + " - " + fimMes);

                log.info("Obtendo os gastos da pessoa no mês atual");
                List<Gasto> gastosDoMes = gastoRepository.findByContaPagarAndDataCompraBetween(contaPagar, inicioMes, fimMes);

                log.info("Número de gastos do mês: " + gastosDoMes.size());

                log.info("Adicionando detalhes dos gastos à planilha");
                for (Gasto gasto : gastosDoMes) {
                    GastoDTO gastoDTO = GastoDTO.fromEntity(gasto);

                    log.info("Detalhes do gasto: " + gastoDTO);

                    row = sheet.createRow(rowNum++);

                    // Aplica estilo à cada célula individualmente a partir da coluna A
                    for (int i = 0; i < 6; i++) {
                        Cell cell1 = row.createCell(i);
                        if (rowNum % 2 == 0) {
                            cell1.setCellStyle(estiloLinhaPar);
                        } else {
                            cell1.setCellStyle(estiloLinhaImpar);
                        }

                        // Adiciona valores às células conforme necessário
                        if (i == 0) {
                            cell1.setCellValue(gastoDTO.getDescricao());
                        } else if (i == 1) {
                            cell1.setCellValue("R$ " + (gastoDTO.getParcelado() ? gastoDTO.getValorParcelado().toString() : gastoDTO.getValorTotal().toString()));
                        } else if (i == 2) {
                            cell1.setCellValue(gastoDTO.getDataCompra().toString());
                        } else if (i == 3) {
                            cell1.setCellValue("R$ " + gastoDTO.getParcelado().toString());
                        } else if (i == 4) {
                            cell1.setCellValue(gastoDTO.getStatus() != null ? gastoDTO.getStatus().toString() : "");
                        } else if (i == 5) {
                            cell1.setCellValue("Parcelas: " + gastoDTO.getNumeroParcelaAtual() + " de " + gastoDTO.getNumeroParcelas());
                        }
                    }

                    log.info("Gasto adicionado à planilha: ");
                }

                // Auto size columns
                for (int i = 0; i < 6; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Escrever o arquivo Excel para o ByteArrayOutputStream
                workbook.write(baos);
                workbook.close();
                log.info("Arquivo Excel criado com sucesso.");

                return baos.toByteArray();
            } else {
                throw new RuntimeException("Conta a pagar não encontrada com o ID: " + contaPagarId);
            }
        }
}

    // Método auxiliar para aplicar o estilo a uma linha
    private static void applyCellStyle(Row row, CellStyle style) {
        for (Cell cell : row) {
            cell.setCellStyle(style);
        }
    }

    private LocalDate getInicioMes() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    private LocalDate getFimMes() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }
}