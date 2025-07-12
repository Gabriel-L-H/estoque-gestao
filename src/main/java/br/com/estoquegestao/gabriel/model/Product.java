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
    private int fk_category;
    private String name;
    private BigDecimal price;
    private int quantityStock;
}