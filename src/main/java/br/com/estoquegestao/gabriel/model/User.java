package br.com.estoquegestao.gabriel.model;

import lombok.Setter;

@Setter
public class User {
    private Cpf cpf;
    private String senha;
    private String nome;
    private Email email;

    public User(Cpf cpf, String senha, String nome, Email email){
        this.cpf = cpf;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
    }

    public String getCpf() {return this.cpf.getCpf();}

    public String getSenha() {return this.senha;}

    public String getNome() {return this.nome;}

    public String getEmail() {return this.email.getEmail();}
    
}