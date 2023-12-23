package br.com.franca.apigastos.utils.dtos;

import br.com.franca.apigastos.enums.TipoGastoEnum;
import br.com.franca.apigastos.model.Gasto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;
    private String descricao;
    private LocalDate dataCompra;
    private int numeroParcelas;
    private BigDecimal valorTotal;
    private BigDecimal valorParcelado;
    private Long idPessoa;
    private Long idCartao;
    private String numeroCartao;
    private Long idContaPagar;
    private Boolean parcelado;
    private TipoGastoEnum status;
    private Integer numeroParcelaAtual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public int getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorParcelado() {
        return valorParcelado;
    }

    public void setValorParcelado(BigDecimal valorParcelado) {
        this.valorParcelado = valorParcelado;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdCartao() {
        return idCartao;
    }

    public void setIdCartao(Long idCartao) {
        this.idCartao = idCartao;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public Long getIdContaPagar() {
        return idContaPagar;
    }

    public void setIdContaPagar(Long idContaPagar) {
        this.idContaPagar = idContaPagar;
    }

    public Boolean getParcelado() {
        return parcelado;
    }

    public void setParcelado(Boolean parcelado) {
        this.parcelado = parcelado;
    }

    public TipoGastoEnum getStatus() {
        return status;
    }

    public void setStatus(TipoGastoEnum status) {
        this.status = status;
    }

    public Integer getNumeroParcelaAtual() {
        return numeroParcelaAtual;
    }

    public void setNumeroParcelaAtual(Integer numeroParcelaAtual) {
        this.numeroParcelaAtual = numeroParcelaAtual;
    }

    public static GastoDTO fromEntity(Gasto gasto) {
        GastoDTO dto = new GastoDTO();
        dto.setId(gasto.getId());
        dto.setDescricao(gasto.getDescricao());
        dto.setDataCompra(gasto.getDataCompra());
        dto.setNumeroParcelas(gasto.getNumeroParcelas());
        dto.setValorTotal(gasto.getValorTotal());
        dto.setValorParcelado(gasto.getValorParcelado());
        dto.setIdPessoa(gasto.getPessoa() != null ? gasto.getPessoa().getId() : null);
        dto.setIdCartao(gasto.getCartaoCredito().getId());
        dto.setNumeroCartao(gasto.getNumeroCartao());
        dto.setIdContaPagar(gasto.getContaPagar() != null ? gasto.getContaPagar().getId() : null);
        dto.setParcelado(gasto.isParcelado());
        dto.setStatus(gasto.getStatus());
        dto.setNumeroParcelaAtual(gasto.getNumeroParcelaAtual());

        return dto;
    }
}