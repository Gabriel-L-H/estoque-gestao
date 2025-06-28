package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.Cpf;
import br.com.estoquegestao.gabriel.model.Email;
import br.com.estoquegestao.gabriel.model.User;

public class Main {
    public static void main(String[] args) {
        try {
            UserDAO g = new UserDAO();
            g.create(new User(new Cpf("443.856.380-12"), "9999", "Elaine", new Email("zzz@gmail.com")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}