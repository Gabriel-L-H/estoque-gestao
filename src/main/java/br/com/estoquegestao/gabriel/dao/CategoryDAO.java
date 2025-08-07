package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
import br.com.estoquegestao.gabriel.model.Category;

import br.com.estoquegestao.gabriel.model.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);
    public void create(Category category) throws SQLException{
        String sql = "INSERT INTO category (type, brand, supplier) VALUES (?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt  = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, category.getType().name());
                stmt.setString(2, category.getBrand());
                stmt.setString(3, category.getSupplier());
                int row = stmt.executeUpdate();
                if (row == 0) {
                    logger.warn("Insert ran but no rows affected.");
                }
                logger.info("New category added! Rows inserted: {}", row);

                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next()){
                    int id = rs.getInt(1);
                    category.setId(id);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this category exists");
            }
            logger.error("Error in register new category");
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void update(Category category) throws SQLException{
        String sql = "UPDATE category SET type = ?, brand = ?, supplier = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, category.getType().name());
                stmt.setString(2, category.getBrand());
                stmt.setString(3, category.getSupplier());
                stmt.setInt(4, category.getId());
                int row = stmt.executeUpdate();
                if (row == 0) {
                    logger.warn("Update ran but no rows affected.");
                }
                logger.info("Update successful! Rows affected: {}", row);
            }
            conn.commit();
        }catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in update category by id = {}", category.getId());
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void delete(int id) throws SQLException{
        String sql = "DELETE FROM category WHERE id = ?";
        String sqlFk = "DELETE FROM product WHERE fk_category = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sqlFk)){
                stmt.setInt(1, id);
                int success = stmt.executeUpdate();
                if (success == 0) {logger.warn("CategoryFk not found");}
                logger.info("CategoryFK deleted successful");
            }

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int success = stmt.executeUpdate();
                if (success == 0) {
                    logger.warn("Category not found");
                }
                logger.info("Category deleted successful");
            }
            conn.commit();
        }catch (SQLException e){
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in delete category by id = {}", id);
            throw new SQLException("Error in delete category: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public Optional<Category> findCategory(int id){
        String sql = "SELECT type, brand, supplier FROM category WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Category categoryFound = new Category();
                    categoryFound.setId(id);
                    categoryFound.setType(Type.valueOf(result.getString("type").toUpperCase()));
                    categoryFound.setBrand(result.getString("brand"));
                    categoryFound.setSupplier(result.getString("supplier"));
                    logger.info("Category has found successful");
                    return Optional.of(categoryFound);
                }
                logger.error("Category is null, not found");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found category by id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public Optional<Category> findCategoryByBrand(String brand){
        String sql = "SELECT id, type, brand, supplier FROM category WHERE brand = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, brand);
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Category categoryFound = new Category();
                    categoryFound.setId(result.getInt("id"));
                    categoryFound.setType(Type.valueOf(result.getString("type").toUpperCase()));
                    categoryFound.setBrand(result.getString("brand"));
                    categoryFound.setSupplier(result.getString("supplier"));
                    logger.info("Category has found successful");
                    return Optional.of(categoryFound);
                }
                logger.error("Category is null, not found");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found category by brand = {}", brand);
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
                    category.setType(Type.valueOf(result.getString("type").toUpperCase()));
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