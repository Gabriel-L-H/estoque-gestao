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
import java.io.InputStream;
import java.util.List;

public class ProductPostHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProductPostHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProductDAO productDAO;

    public ProductPostHandler(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())){
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

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")){
            logger.warn("Unsupported Content-Type: {}", contentType);
            exchange.sendResponseHeaders(415, -1);
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

            InputStream requestBody = exchange.getRequestBody();
            Product newProduct = mapper.readValue(requestBody, Product.class);

            this.productDAO.create(newProduct);

            exchange.sendResponseHeaders(200, -1);
            exchange.close();
            logger.info("Product created with Id {}", newProduct.getId());
        } catch (Exception e) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            logger.error("Error processing request: " + e.getMessage());
        }
    }
}