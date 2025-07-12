package br.com.estoquegestao.gabriel.model;

public class Cpf {
    private String cpf;

    public Cpf(String cpf){
        try{
            this.cpf = this.validateCpf(cpf);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String validateCpf(String cpf){
    if (cpf == null) {
        throw new NullPointerException("CPF can't be null");
    }

    cpf = cpf.replaceAll("\\D", "");

    if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
        throw new IllegalArgumentException("Invalid sequence");
    }

    int[] nums = new int[11];
    for (int i = 0; i < 11; i++) {
        nums[i] = cpf.charAt(i) - '0';
    }

    for (int t = 9; t < 11; t++) {
        int sum = 0;
        for (int c = 0; c < t; c++) {
            sum += nums[c] * ((t + 1) - c);
        }
        int resto = (sum * 10) % 11;
        int dv = (resto == 10) ? 0 : resto;
        if (nums[t] != dv) {
            throw new IllegalArgumentException("Invalid CPF");
        }
    }
        return cpf;
    }

    public String getCpf() {
        return this.cpf;
    }
}