package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ItemSale {
    private int id;
    private int fk_produto;
    private int fk_venda;
    private int quantidade;
    private BigDecimal preco;
}