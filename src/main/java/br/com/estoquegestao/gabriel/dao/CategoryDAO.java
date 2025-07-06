package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.Category;

import br.com.estoquegestao.gabriel.model.Tipo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);
    public void create(Category category){
        String sql = "Insert into categoria (tipo, marca, fornecedor) values (?, ?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1, category.getTipo().name());
            stmt.setString(2, category.getMarca());
            stmt.setString(3, category.getFornecedor());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New category added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this category exists");
                throw new IllegalStateException("Err in execute SQL: category exists" + e);
            }
            logger.error("Err in register new category");
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void update(Category category){
        String sql = "update categoria set tipo = ?, marca = ?, fornecedor = ? where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, category.getTipo().name());
            stmt.setString(2, category.getMarca());
            stmt.setString(3, category.getFornecedor());
            stmt.setInt(4, category.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update category by id = {}", category.getId());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void delete(Category category){
        String sql = "delete from categoria where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, category.getId());
            int sucess = stmt.executeUpdate();
            if (sucess == 0){logger.warn("Category don't found");}
            logger.info("Category deleted successful");
        }catch (SQLException e){
            logger.error("Err in delete category by id = {}", category.getId());
            e.printStackTrace();
        }
    }

    public Optional<Category> findCategory(Category category){
        String sql = "select tipo, marca, fornecedor from categoria where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, category.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Category categoryFound = new Category();
                    categoryFound.setId(category.getId());
                    categoryFound.setTipo(Tipo.valueOf(result.getString("tipo")));
                    categoryFound.setMarca(result.getString("marca"));
                    categoryFound.setFornecedor(result.getString("fornecedor"));
                    logger.info("Category has found successful");
                    return Optional.of(categoryFound);
                }
                logger.error("Category is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Err in found category by id = {}", category.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Category> findAll(){
        String sql = "select id, tipo, marca, fornecedor from categoria";
        List<Category> categories = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Category category = new Category();
                    category.setId(result.getInt("id"));
                    category.setTipo(Tipo.valueOf(result.getString("tipo")));
                    category.setMarca(result.getString("marca"));
                    category.setFornecedor(result.getString("fornecedor"));
                    categories.add(category);
                }
            }
            logger.info("All categories has found successful");
        } catch (SQLException e) {
            logger.error("Err in found by all categories");
            e.printStackTrace();
        }
        return categories;
    }
}