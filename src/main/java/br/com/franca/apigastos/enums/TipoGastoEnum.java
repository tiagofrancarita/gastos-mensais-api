package br.com.franca.apigastos.enums;

public enum TipoGastoEnum  {

    LAZER ("Lazer"),
    ALIMENTACAO ("Aliemntacao"),
    SAUDE ("Saude"),
    ENTRETENIMENTO ("Entretenimento"),
    VIAGEM ("Viagem"),
    DIVERSOS ("Diversos");


    private String descricao;

    TipoGastoEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}