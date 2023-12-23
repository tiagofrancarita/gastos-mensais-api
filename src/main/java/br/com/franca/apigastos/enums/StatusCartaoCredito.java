package br.com.franca.apigastos.enums;

public enum StatusCartaoCredito {

    VENCIDO ("Vencido"),
    VALIDO ("Valido");

    private String descricao;

    StatusCartaoCredito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
