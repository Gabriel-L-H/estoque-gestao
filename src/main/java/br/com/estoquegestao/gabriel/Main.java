package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.Cpf;
import br.com.estoquegestao.gabriel.model.User;

public class Main {
    public static void main(String[] args) {
        try {
            UserDAO g = new UserDAO();
            User user = new User();
            user.setCpf(new Cpf("41043946020"));
            System.out.println(g.findUser(user).toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}