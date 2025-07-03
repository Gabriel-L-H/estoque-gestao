package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Sale {
    private int id;
    private String fk_usuario;
    private LocalDateTime dataHora;
    private BigDecimal valorTotal;
}