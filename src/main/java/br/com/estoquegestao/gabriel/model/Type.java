package br.com.estoquegestao.gabriel.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Type {
    ELECTRONIC,
    CLOTHING,
    HOUSEHOLD,
    FOOD,
    COSMETICS,
    ENTERTAINMENT;

    @JsonCreator
    public static Type fromString(String value) {
        return Type.valueOf(value.toUpperCase());
    }
}