package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
import br.com.estoquegestao.gabriel.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);
    public void create(Product product){
        String sql = "INSERT INTO product (fk_category, name, price, quantityStock) VALUES (?, ?, ?, ?)";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getFk_category());
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getQuantityStock());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New product added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this product exists");
                throw new IllegalStateException("Error in execute SQL: product exists" + e);
            }
            logger.error("Error in register new product");
            throw new RuntimeException("Error in execute SQL: " + e);
        }
    }

    public void update(Product product){
        String sql = "UPDATE product SET name = ?, price = ?, quantityStock = ? WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getQuantityStock());
            stmt.setInt(4, product.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Error in update product by id = {}", product.getId());
            throw new RuntimeException("Error in execute SQL: " + e);
        }
    }

    public void delete(Product product){
        String sql = "DELETE FROM product WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getId());
            int success = stmt.executeUpdate();
            if (success == 0){logger.warn("Product not found");}
            logger.info("Product deleted successful");
        }catch (SQLException e){
            logger.error("Error in delete product by id = {}", product.getId());
            e.printStackTrace();
        }
    }

    public Optional<Product> findProduct(Product product){
        String sql = "SELECT fk_category, name, price, quantityStock FROM product WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Product productFound = new Product();
                    productFound.setId(product.getId());
                    productFound.setFk_category(result.getInt("fk_category"));
                    productFound.setName(result.getString("name"));
                    productFound.setPrice(result.getBigDecimal("price"));
                    productFound.setQuantityStock(result.getInt("quantityStock"));
                    logger.info("Product has found successful");
                    return Optional.of(productFound);
                }
                logger.info("Product is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found Product by id = {}", product.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll(){
        String sql = "SELECT id, fk_category, name, price, quantityStock FROM product";
        List<Product> products = new ArrayList<>();
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Product product = new Product();
                    product.setId(result.getInt("id"));
                    product.setFk_category(result.getInt("fk_category"));
                    product.setName(result.getString("name"));
                    product.setPrice(result.getBigDecimal("price"));
                    product.setQuantityStock(result.getInt("quantityStock"));
                    products.add(product);
                }
            }
            logger.info("All products has found successful");
        } catch (SQLException e) {
            logger.error("Error in found by all products");
            e.printStackTrace();
        }
        return products;
    }

}