package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {
    private int id;
    private Type type;
    private String brand;
    private String supplier;
}
