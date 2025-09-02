package br.com.estoquegestao.gabriel.controller.productcontroller;

import br.com.estoquegestao.gabriel.dao.ProductDAO;
import br.com.estoquegestao.gabriel.model.Product;
import br.com.estoquegestao.gabriel.utils.JwtUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ProductGetHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductGetHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProductDAO productDAO;

    public ProductGetHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
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

            if (!tokenClaim.get(1).toLowerCase().contains("user")){
                exchange.sendResponseHeaders(403, -1);
                logger.warn("Role not authorized");
                return;
            }

            List<Product> products = this.productDAO.findAll();
            byte[] response = mapper.writeValueAsBytes(products);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        } catch (RuntimeException e) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}