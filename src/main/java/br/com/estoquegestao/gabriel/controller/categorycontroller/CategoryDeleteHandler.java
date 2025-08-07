package br.com.estoquegestao.gabriel.controller.categorycontroller;

import br.com.estoquegestao.gabriel.dao.CategoryDAO;
import br.com.estoquegestao.gabriel.model.Category;
import br.com.estoquegestao.gabriel.utils.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CategoryDeleteHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDeleteHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final CategoryDAO categoryDAO;

    public CategoryDeleteHandler(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(exchange.getRequestMethod())){
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

        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 4){
            exchange.sendResponseHeaders(400, -1);
            logger.warn("Without id");
            return;
        }

        int id = Integer.parseInt(parts[3]);
        String token = header.substring(7);
        try{
            DecodedJWT jwt = JwtUtil.verifyToken(token);
            List<String> tokenClaim = JwtUtil.authenticationToken(jwt);

            if (!tokenClaim.get(1).toLowerCase().contains("admin")){
                exchange.sendResponseHeaders(403, -1);
                logger.warn("Role not authorized");
                return;
            }

            Optional<Category> category = this.categoryDAO.findCategory(id);
            if (category.isEmpty()){
                exchange.sendResponseHeaders(404, -1);
                logger.warn("Category not found");
                return;
            }
            this.categoryDAO.delete(id);

            byte[] response = mapper.writeValueAsBytes(category.get());

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        } catch (Exception e) {
            exchange.sendResponseHeaders(405, -1);
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}