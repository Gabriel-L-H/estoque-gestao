package br.com.estoquegestao.gabriel;

import br.com.estoquegestao.gabriel.dao.*;
import br.com.estoquegestao.gabriel.model.*;
import br.com.estoquegestao.gabriel.service.AuthService;
import br.com.estoquegestao.gabriel.service.ItemSaleService;
import br.com.estoquegestao.gabriel.service.JwtUtil;
import br.com.estoquegestao.gabriel.service.SaleService;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.math.BigDecimal;
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
            //authService.signup(user, user.getCpf().toCharArray());
            //String token = JwtUtil.token(user);
            //System.out.println(token);
            //DecodedJWT jwt = JwtUtil.verifyToken(token);
            //System.out.println(jwt.getSubject());
            Category category = new Category();
            category.setBrand("Acer");
            category.setType(Type.ELECTRONIC);
            category.setSupplier("Amazon");
            //new CategoryDAO().create(category);

            ProductDAO productDAO = new ProductDAO();

            Product product = new Product();
            product.setName("Notebook Acer");
            product.setPrice(new BigDecimal("5000"));
            product.setFk_category(2);
            product.setQuantityStock(430);
            //new ProductDAO().create(product);

            Product product2 = new Product();
            product2.setName("Feijão");
            product2.setPrice(new BigDecimal("34.99"));
            product2.setFk_category(1);
            product2.setQuantityStock(34);
            //productDAO.create(product2);

            SaleService saleService = new SaleService(new SaleDAO(), new ItemSaleDAO(), new ProductDAO());
            ItemSale itemSale = new ItemSale();
            itemSale.setId(12);
            itemSale.setFk_product(2);
            itemSale.setQuantity(2);

            ItemSale itemSale2 = new ItemSale();
            itemSale2.setFk_product(3);
            itemSale2.setQuantity(1);

            List<ItemSale> itemSaleList = List.of(itemSale, itemSale2);

            Sale sale = new Sale();
            sale.setFk_user("63385153000");
            //saleService.create(sale, itemSaleList);
            new ItemSaleService(new ItemSaleDAO(), new ProductDAO(), new SaleDAO()).update(itemSale);
            //Test: new price --> 69.98 | new totalValue --> 5034.99 | new quantity --> 33
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}