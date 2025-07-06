package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.*;
import br.com.estoquegestao.gabriel.service.AuthService;
import br.com.estoquegestao.gabriel.service.PasswordUtil;


public class Main {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            User user = new User();
            user.setCpf(new Cpf("543.878.750-62"));
            user.setNome("Geraldo");
            user.setEmail(new Email("geraldo21@gmail.com"));
            user.setSenha("senhaAleatoria");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}