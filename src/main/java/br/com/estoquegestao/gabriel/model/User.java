package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@ToString
public class User {
    private Cpf cpf;
    @Getter
    private List<String> role;
    @Getter
    private String name;
    private Email email;
    @Getter
    private String password;

    public String getCpf() {return this.cpf.getCpf();}

    public String getEmail() {return this.email.getEmail();}

}