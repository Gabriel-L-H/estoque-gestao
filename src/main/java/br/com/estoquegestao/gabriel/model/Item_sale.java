package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Item_sale {
    private int id;
    private int fk_produto;
    private int fk_venda;
    private int quantidade;
    private BigDecimal preco;

    public Item_sale(int fk_produto, int fk_venda, int quantidade, BigDecimal preco) {
        this.fk_produto = fk_produto;
        this.fk_venda = fk_venda;
        this.quantidade = quantidade;
        this.preco = preco;
    }
}