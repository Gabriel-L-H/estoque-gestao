package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
import br.com.estoquegestao.gabriel.model.Category;

import br.com.estoquegestao.gabriel.model.Type;
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
        String sql = "INSERT INTO category (type, brand, supplier) VALUES (?, ?, ?)";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1, category.getType().name());
            stmt.setString(2, category.getBrand());
            stmt.setString(3, category.getSupplier());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New category added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this category exists");
                throw new IllegalStateException("Error in execute SQL: category exists" + e);
            }
            logger.error("Error in register new category");
            throw new RuntimeException("Error in execute SQL: " + e);
        }
    }

    public void update(Category category){
        String sql = "UPDATE category SET type = ?, brand = ?, supplier = ? WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, category.getType().name());
            stmt.setString(2, category.getBrand());
            stmt.setString(3, category.getSupplier());
            stmt.setInt(4, category.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Error in update category by id = {}", category.getId());
            throw new RuntimeException("Error in execute SQL: " + e);
        }
    }

    public void delete(Category category){
        String sql = "DELETE FROM category WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, category.getId());
            int success = stmt.executeUpdate();
            if (success == 0){logger.warn("Category not found");}
            logger.info("Category deleted successful");
        }catch (SQLException e){
            logger.error("Error in delete category by id = {}", category.getId());
            e.printStackTrace();
        }
    }

    public Optional<Category> findCategory(Category category){
        String sql = "SELECT type, brand, supplier FROM category WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, category.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Category categoryFound = new Category();
                    categoryFound.setId(category.getId());
                    categoryFound.setType(Type.valueOf(result.getString("type")));
                    categoryFound.setBrand(result.getString("brand"));
                    categoryFound.setSupplier(result.getString("supplier"));
                    logger.info("Category has found successful");
                    return Optional.of(categoryFound);
                }
                logger.error("Category is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found category by id = {}", category.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Category> findAll(){
        String sql = "SELECT id, type, brand, supplier FROM category";
        List<Category> categories = new ArrayList<>();
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Category category = new Category();
                    category.setId(result.getInt("id"));
                    category.setType(Type.valueOf(result.getString("type")));
                    category.setBrand(result.getString("brand"));
                    category.setSupplier(result.getString("supplier"));
                    categories.add(category);
                }
            }
            logger.info("All categories has found successful");
        } catch (SQLException e) {
            logger.error("Error in found by all categories");
            e.printStackTrace();
        }
        return categories;
    }
}