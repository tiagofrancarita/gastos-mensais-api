package br.com.franca.apigastos.service;


import br.com.franca.apigastos.enums.StatusContaPagar;
import br.com.franca.apigastos.model.CartaoCredito;
import br.com.franca.apigastos.model.ContaPagar;
import br.com.franca.apigastos.model.Gasto;
import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.repository.CartaoCreditoRepository;
import br.com.franca.apigastos.repository.ContaPagarRepository;
import br.com.franca.apigastos.repository.GastoRepository;
import br.com.franca.apigastos.repository.PessoaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class GastoService {

    // Importações omitidas para brevidade

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private CartaoCreditoRepository cartaoCreditoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    private Logger log = LoggerFactory.getLogger(GastoService.class);

    @Transactional
    public Gasto salvarGasto(Gasto gasto) {

        log.info("Inicio do processo de salvar gasto");

        validarGasto(gasto);

        // Certificar-se de que a Pessoa está gerenciada
        log.info("Buscando pessoa por id");
        Pessoa pessoa = pessoaRepository.findById(gasto.getPessoa().getId())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        gasto.setPessoa(pessoa);

        // Certificar-se de que o CartaoCredito está gerenciado
        log.info("Buscando cartaoCredito por id");
        CartaoCredito cartaoCredito = cartaoCreditoRepository.findById(gasto.getCartaoCredito().getId())
                .orElseThrow(() -> new RuntimeException("CartaoCredito não encontrado"));

            if (gasto.getNumeroParcelas() > 1) {
                log.info("Criando gasto parcelado");
                criarGastoParcelado(gasto);
            } else {
                log.info("Criando gasto não parcelado");
                gasto.setCartaoCredito(cartaoCredito);
                gasto.setNumeroParcelas(gasto.getNumeroParcelas());
                gasto.setParcelado(false);
                gasto.setStatus(gasto.getStatus());
                LocalDate dataInclusao = YearMonth.now().atDay(1);
                gasto.setDataInclusao(dataInclusao);
                gasto.setMesVigenteGasto(Month.from(dataInclusao).name());
                gasto.setNumeroParcelaAtual(1);
                gasto.setNumeroParcelas(1);
                gasto = gastoRepository.save(gasto);

                log.info("Criando ou atualizando conta a pagar");
                criarOuAtualizarContaPagar(gasto);
            } {

        }
        return gasto;
    }

    public Gasto criarGastoParcelado(Gasto gasto) {

        log.info("Inicio do processo de criar gasto parcelado");

        BigDecimal valorParcela = gasto.getValorTotal().divide(BigDecimal.valueOf(gasto.getNumeroParcelas()), RoundingMode.HALF_UP);
        LocalDate dataInclusao = YearMonth.now().atDay(1);


        for (int parcela = 0; parcela < gasto.getNumeroParcelas(); parcela++) {
            Gasto gastoParcelado = new Gasto();
            log.info("Criando gasto parcelado");

            // Certificar-se de que o CartaoCredito está gerenciado
            CartaoCredito cartaoCredito = cartaoCreditoRepository.findById(gasto.getCartaoCredito().getId())
                    .orElseThrow(() -> new RuntimeException("CartaoCredito não encontrado"));

            gastoParcelado.setCartaoCredito(cartaoCredito); // Usando a mesma instância de CartaoCredito

            // Certificar-se de que a Pessoa está gerenciada
            Pessoa pessoa = pessoaRepository.findById(gasto.getPessoa().getId())
                    .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

            gastoParcelado.setPessoa(pessoa);

            gastoParcelado.setDescricao(gasto.getDescricao());
            gastoParcelado.setDataCompra(gasto.getDataCompra().plusMonths(parcela));
            gastoParcelado.setNumeroParcelas(gasto.getNumeroParcelas());
            gastoParcelado.setValorTotal(gasto.getValorTotal());
            gastoParcelado.setValorParcelado(valorParcela);
            gastoParcelado.setNumeroCartao(gasto.getNumeroCartao());
            gastoParcelado.setParcelado(true);
            gastoParcelado.setNumeroParcelaAtual(parcela + 1);
            gastoParcelado.setStatus(gasto.getStatus());

            gastoParcelado.setDataInclusao(dataInclusao.plusMonths(parcela));
            gastoParcelado.setMesVigenteGasto(Month.from(dataInclusao.plusMonths(parcela)).name());

            gastoRepository.save(gastoParcelado);
            log.info("Gasto parcelado criado com sucesso");


            log.info("Criando ou atualizando conta a pagar");
            criarOuAtualizarContaPagar(gastoParcelado);
            log.info("Conta a pagar criada ou atualizada com sucesso");


        }
        return gasto;
    }

    private ContaPagar criarOuAtualizarContaPagar(Gasto gasto) {

        log.info("================================ Inicio do processo de criar ou atualizar conta a pagar =================================");

        LocalDate inicioMes = getInicioMes();
        LocalDate fimMes = getFimMes();

        // Certificar-se de que o CartaoCredito está gerenciado
        log.info("Buscando cartaoCredito por id");
        CartaoCredito cartaoCredito = cartaoCreditoRepository.findById(gasto.getCartaoCredito().getId())
                .orElseThrow(() -> new RuntimeException("CartaoCredito não encontrado"));

        gasto.setCartaoCredito(cartaoCredito); // Usando a mesma instância de CartaoCredito
        log.info("CartaoCredito encontrado com sucesso");

        // Certificar-se de que a Pessoa está gerenciada
        log.info("Buscando pessoa por id");
        Pessoa pessoa = pessoaRepository.findById(gasto.getPessoa().getId())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        gasto.setPessoa(pessoa);
        log.info("Pessoa encontrada com sucesso");


        String mesReferencia = gasto.getMesVigenteGasto();
        BigDecimal valorTotalConta;

        if (gasto.isParcelado() && gasto.getNumeroParcelaAtual() > 1) {
            // Para gastos parcelados e parcelas posteriores, considerar apenas o valor da parcela atual
            log.info("Gasto parcelado e parcela atual maior que 1");
            valorTotalConta = gasto.getValorParcelado();
        } else {
            // Para gastos não parcelados e primeira parcela de gastos parcelados, considerar o valor total ou da parcela
            log.info("Gasto não parcelado");
            valorTotalConta = gasto.isParcelado() ? gasto.getValorParcelado() : gasto.getValorTotal();
        }

        // Verificar se já existe uma conta a pagar para o mês de referência
        log.info("Buscando conta a pagar por mesReferencia");
        ContaPagar contaPagarExistente = contaPagarRepository.findByMesReferencia(mesReferencia);

        if (contaPagarExistente != null) {
            log.info("Conta a pagar encontrada com sucesso");
            // Atualizar a conta existente
            log.info("Atualizando conta a pagar");
            contaPagarExistente.setValor(contaPagarExistente.getValor().add(valorTotalConta));
            contaPagarRepository.save(contaPagarExistente);

            log.info("Conta a pagar atualizada com sucesso");

            gasto.setContaPagar(contaPagarExistente);
            log.info("Atualizando gasto com o id da conta a pagar");
            gastoRepository.save(gasto);
            log.info("Gasto atualizado com o id da conta a pagar com sucesso");
            return contaPagarExistente;
        } else {
            log.info("Conta a pagar não encontrada");
            // Criar uma nova conta
            ContaPagar novaContaPagar = new ContaPagar();
            log.info("Criando nova conta a pagar");
            novaContaPagar.setDataInclusao(YearMonth.now().atDay(1));
            novaContaPagar.setDataVencimento(fimMes);
            novaContaPagar.setDescricao("Conta a pagar referente ao mês de " + mesReferencia + " do ano de " + YearMonth.now().getYear() + " Pessoa: " + gasto.getPessoa().getNome());
            novaContaPagar.setMesReferencia(mesReferencia);
            novaContaPagar.setPessoa(gasto.getPessoa());
            novaContaPagar.setStatus(StatusContaPagar.PENDENTE);
            novaContaPagar.setValor(valorTotalConta);
            contaPagarRepository.save(novaContaPagar);
            log.info("Conta a pagar criada com sucesso");

            gasto.setContaPagar(novaContaPagar);
            gastoRepository.save(gasto);
            log.info("Gasto atualizado com o id da conta a pagar criada com sucesso");
            return novaContaPagar;
        }
    }



    public List<Gasto> listarGastoPorMes(LocalDate dataCompra) {

        log.info("Inicio do processo de listagem de gastos por mês");

        try {
            List<Gasto> gastos = gastoRepository.findByDataCompra(dataCompra);
            log.info("Total de Gastos encontrados: {}", gastos.size());
            return gastos;
        } catch (Exception e) {
            log.error("Erro ao listar gastos por mês: {}", e.getMessage());
            return null;
        }
    }

    public List<Gasto> listarGastosCadastrados() {

        log.info("Inicio do processo de listagem de gastos por mês");

        try {
            List<Gasto> gastos = gastoRepository.findAll();
            log.info("Total de Gastos encontrados: {}", gastos.size());
            return gastos;
        } catch (Exception e) {
            log.error("Erro ao listar gastos por mês: {}", e.getMessage());
            return null;
        }
    }

    public ResponseEntity<String> deletarGastoPorId(Long id) {

        gastoRepository.deleteById(id);
        return new ResponseEntity<String>("Gasto deletado com sucesso", HttpStatus.OK);

    }

    public Gasto buscarGastoPorId(Long id) {

        log.info("Inicio do processo buscar gasto por id.");

        return gastoRepository.findById(id).orElse(null);
    }

    public Gasto buscarGastoPorDescricao(String descricao) {

        log.info("Inicio do processo buscar gasto por descricao.");

        return gastoRepository.findByDescricao(descricao);
    }

    private LocalDate getInicioMes() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    private LocalDate getFimMes() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }


    private void validarGasto(Gasto gasto) {

        if (gasto.getDescricao() == null || gasto.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("Descrição do gasto não informada");
        }
        // ... (validações adicionais)
    }
}