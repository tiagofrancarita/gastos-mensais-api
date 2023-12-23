package br.com.franca.apigastos.enums;

public enum StatusContaPagar {

    PAGO("Pago"),
    PENDENTE("Pendente"),
    CANCELADO("Cancelado"),
    AGENDADO("Agendado"),
    ATRASADO("Atrasado");

    private String descricao;

    StatusContaPagar(String descricao) {
        this.descricao = descricao;
    }
}

