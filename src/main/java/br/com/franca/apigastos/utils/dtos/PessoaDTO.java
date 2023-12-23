package br.com.franca.apigastos.utils.dtos;

import br.com.franca.apigastos.enums.StatusPessoaEnum;
import br.com.franca.apigastos.model.Pessoa;

import java.time.LocalDate;

public class PessoaDTO {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private String cpf;
    private String email;
    private LocalDate dataCadastro;
    private StatusPessoaEnum status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public StatusPessoaEnum getStatus() {
        return status;
    }

    public void setStatus(StatusPessoaEnum status) {
        this.status = status;
    }
    public static PessoaDTO fromEntity(Pessoa pessoa) {
        PessoaDTO dto = new PessoaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setCpf(pessoa.getCpf());
        dto.setEmail(pessoa.getEmail());
        dto.setDataCadastro(pessoa.getDataCadastro());
        dto.setStatus(pessoa.getStatus());
        return dto;
    }
}
