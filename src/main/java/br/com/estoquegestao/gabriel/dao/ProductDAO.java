package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
        String sql = "Insert into produto (fk_categoria, nome, preco, quantidadeEstoque) values (?, ?, ?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getFk_categoria());
            stmt.setString(2, product.getNome());
            stmt.setBigDecimal(3, product.getPreco());
            stmt.setInt(4, product.getQuantidadeEstoque());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New product added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this product exists");
                throw new IllegalStateException("Err in execute SQL: product exists" + e);
            }
            logger.error("Err in register new product");
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void update(Product product){
        String sql = "update produto set nome = ?, preco = ?, quantidadeEstoque = ? where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, product.getNome());
            stmt.setBigDecimal(2, product.getPreco());
            stmt.setInt(3, product.getQuantidadeEstoque());
            stmt.setInt(4, product.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update product by id = {}", product.getId());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void delete(Product product){
        String sql = "delete from produto where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getId());
            int sucess = stmt.executeUpdate();
            if (sucess == 0){logger.warn("Product don't found");}
            logger.info("Product deleted successful");
        }catch (SQLException e){
            logger.error("Err in delete product by id = {}", product.getId());
            e.printStackTrace();
        }
    }

    public Optional<Product> findProduct(Product product){
        String sql = "select fk_categoria, nome, preco, quantidadeEstoque from produto where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, product.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Product productFound = new Product();
                    productFound.setId(product.getId());
                    productFound.setFk_categoria(result.getInt("fk_categoria"));
                    productFound.setNome(result.getString("nome"));
                    productFound.setPreco(result.getBigDecimal("preco"));
                    productFound.setQuantidadeEstoque(result.getInt("quantidadeEstoque"));
                    logger.info("Product has found successful");
                    return Optional.of(productFound);
                }
                logger.info("Product is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Err in found Product by id = {}", product.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll(){
        String sql = "select id, fk_categoria, nome, preco, quantidadeEstoque from produto";
        List<Product> products = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Product product = new Product();
                    product.setId(result.getInt("id"));
                    product.setFk_categoria(result.getInt("fk_categoria"));
                    product.setNome(result.getString("nome"));
                    product.setPreco(result.getBigDecimal("preco"));
                    product.setQuantidadeEstoque(result.getInt("quantidadeEstoque"));
                    products.add(product);
                }
            }
            logger.info("All products has found successful");
        } catch (SQLException e) {
            logger.error("Err in found by all products");
            e.printStackTrace();
        }
        return products;
    }

}