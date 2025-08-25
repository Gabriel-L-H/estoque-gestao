package br.com.estoquegestao.gabriel.controller.usercontroller;

import br.com.estoquegestao.gabriel.dao.UserDAO;
import br.com.estoquegestao.gabriel.model.User;
import br.com.estoquegestao.gabriel.utils.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class UserGetHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserGetHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDAO userDAO;

    public UserGetHandler(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())){
            exchange.sendResponseHeaders(405, -1);
            logger.warn("Method not allowed");
            return;
        }

        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if(header == null || !header.startsWith("Bearer ")){
            exchange.sendResponseHeaders(401, -1);
            logger.warn("Authorization header is missing or invalid");
            return;
        }

        String token = header.substring(7);
        try{
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            List<String> tokenClaim = JwtUtil.authenticationToken(jwt);

            if (!tokenClaim.get(1).toLowerCase().contains("admin")){
                exchange.sendResponseHeaders(403, -1);
                logger.warn("Role not authorized");
                return;
            }

            List<User> users = this.userDAO.findAll();
            byte[] response = mapper.writeValueAsBytes(users);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        } catch (RuntimeException e) {
            exchange.sendResponseHeaders(405, -1);
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}