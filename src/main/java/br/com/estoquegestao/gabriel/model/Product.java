package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Product {
    private int id;
    private int fk_categoria;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEstoque;
}