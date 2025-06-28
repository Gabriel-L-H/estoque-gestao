package br.com.estoquegestao.gabriel.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Sale {
    private int id;
    private String fk_usuario;
    private LocalDateTime dataHora;
    private BigDecimal valorTotal;

    public Sale(String fk_usuario, LocalDateTime dataHora, BigDecimal valorTotal) {
        this.fk_usuario = fk_usuario;
        this.dataHora = dataHora;
        this.valorTotal = valorTotal;
    }
}