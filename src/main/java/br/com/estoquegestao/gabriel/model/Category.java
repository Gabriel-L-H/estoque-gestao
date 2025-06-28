package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private int id;
    private Tipo tipo;
    private String marca;
    private String fornecedor;

    public Category(Tipo tipo, String marca, String fornecedor){
        this.tipo = tipo;
        this.marca = marca;
        this.fornecedor = fornecedor;
    }
}
