package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class User {
    private Cpf cpf;
    @Getter
    private String nome;
    private Email email;
    @Getter
    private String senha;

    public String getCpf() {return this.cpf.getCpf();}

    public String getEmail() {return this.email.getEmail();}

}