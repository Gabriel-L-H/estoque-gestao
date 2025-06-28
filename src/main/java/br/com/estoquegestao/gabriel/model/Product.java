package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Product {
    private int id;
    private int fk_categoria;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEstoque;

    public Product(int fk_categoria, String nome, BigDecimal preco, int quantidadeEstoque) {
        this.fk_categoria = fk_categoria;
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }
}