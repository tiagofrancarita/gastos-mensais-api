package br.com.franca.apigastos.enums;

public enum StatusPessoaEnum {

    ATIVO("Ativo"),
    INATIVO("Inativo");

    private String descricao;

    StatusPessoaEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}