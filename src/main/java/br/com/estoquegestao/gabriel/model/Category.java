package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Category {
    private int id;
    private Tipo tipo;
    private String marca;
    private String fornecedor;
}
