package br.com.estoquegestao.gabriel.controller.usercontroller;

import br.com.estoquegestao.gabriel.model.User;
import br.com.estoquegestao.gabriel.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class UserRegister implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserRegister.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthService authService;

    public UserRegister(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())){
            exchange.sendResponseHeaders(405, -1);
            logger.warn("Method not allowed");
            return;
        }

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")){
            logger.warn("Unsupported Content-Type: {}", contentType);
            exchange.sendResponseHeaders(415, -1);
            return;
        }

        try{
            InputStream requestBody = exchange.getRequestBody();
            User newUser = mapper.readValue(requestBody, User.class);

            this.authService.signup(newUser, newUser.getPassword().toCharArray());;

            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            logger.info("User created with CPF {}", newUser.getCpf());
        } catch (Exception e) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}