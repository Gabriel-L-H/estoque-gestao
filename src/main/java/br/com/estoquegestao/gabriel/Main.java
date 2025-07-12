package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.*;
import br.com.estoquegestao.gabriel.service.AuthService;
import br.com.estoquegestao.gabriel.service.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            AuthService authService = new AuthService(userDAO);
            User user = new User();
            user.setCpf(new Cpf("633.851.530-00"));
            user.setName("Junior");
            user.setRole(List.of("User"));
            user.setEmail(new Email("jovem2@gmail.com"));
            user.setPassword("ioi");
            authService.signup(user, user.getCpf().toCharArray());
            String token = JwtUtil.token(user);
            System.out.println(token);
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            System.out.println(jwt.getSubject());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}