package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.*;
import br.com.estoquegestao.gabriel.service.AuthService;
import br.com.estoquegestao.gabriel.service.JwtUtil;
import br.com.estoquegestao.gabriel.service.PasswordUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            User user = new User();
            user.setCpf(new Cpf("543.878.750-62"));
            user.setNome("Geraldo");
            user.setRole(List.of("User", "Admin"));
            user.setEmail(new Email("geraldo21@gmail.com"));
            user.setSenha("senhaAleatoria");
            String token = JwtUtil.token(user);
            System.out.println(token);
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            System.out.println(jwt.getSubject());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}