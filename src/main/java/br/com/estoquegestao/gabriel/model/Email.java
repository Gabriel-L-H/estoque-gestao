package br.com.estoquegestao.gabriel.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {
    private String email;

    public Email(String email){
        validateEmail(email);
        this.email = email;
    };

    private void validateEmail(String email){
        if(email == null){
            throw new NullPointerException("Fill out an email");
        }
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            throw new IllegalArgumentException("Invalid format");
        }
    }

    public String getEmail() {
        return this.email;
    }
}
