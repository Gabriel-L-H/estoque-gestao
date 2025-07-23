package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
import br.com.estoquegestao.gabriel.model.ItemSale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemSaleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ItemSaleDAO.class);
    public void create(List<ItemSale> itemSale) throws SQLException {
        String sql = "INSERT INTO item_sale (fk_product, fk_sale, quantity, price) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        int[] count;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (ItemSale itemS : itemSale) {
                    stmt.setInt(1, itemS.getFk_product());
                    stmt.setInt(2, itemS.getFk_sale());
                    stmt.setInt(3, itemS.getQuantity());
                    stmt.setBigDecimal(4, itemS.getPrice());
                    stmt.addBatch();
                }
                count = stmt.executeBatch();
            }
            int totalRow = 0;
            for (int result : count){
                if (result > 0){totalRow++;}
                if (result == Statement.SUCCESS_NO_INFO) {
                    logger.info("Success in inserted, but without info");
                    totalRow++;
                }
            }
            logger.info("Nem item_sale added! Rows inserted: {}", totalRow);
            conn.commit();
        } catch (BatchUpdateException e) {
            ConnectionHikari.safeRollback(conn);
            count = e.getUpdateCounts();
            for (int result : count) {
                if (result == Statement.EXECUTE_FAILED)
                    logger.error("Failed in batch index (BatchUpdateException), by {}", result);
                else if (result == Statement.SUCCESS_NO_INFO)
                    logger.warn("Batch index ok, but without info");
                else
                    logger.info("Batch index inserted {} rows", result);
            }
            logger.error("BatchUpdateException: error in batch. updateCounts = {}", Arrays.toString(count));
        } catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            if (e.getErrorCode() == 1062) {
                logger.warn("Duplicate detected: this item_sale exists");
            }
            logger.error("Error in register new item_sale");
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void update(ItemSale itemSale) throws SQLException {
        String sql = "UPDATE item_sale SET quantity = ?, price = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, itemSale.getQuantity());
                stmt.setBigDecimal(2, itemSale.getPrice());
                stmt.setInt(3, itemSale.getId());
                int row = stmt.executeUpdate();
                if (row == 0){
                    logger.warn("Update ran but no rows affected.");
                }
                logger.info("Update successful! Rows affected: {}", row);
            }
            conn.commit();
        }catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in update item_sale by id = {}", itemSale.getId());
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM item_sale WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int success = stmt.executeUpdate();
                if (success == 0) {
                    logger.warn("Item_sale not found");
                }
                logger.info("Item_sale deleted successful");
            }
            conn.commit();
        }catch (SQLException e){
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in delete item_sale by id = {}", id);
            throw new SQLException("Error in delete item_sale: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public Optional<ItemSale> findItemSale(int id){
        String sql = "SELECT fk_product, fk_sale, quantity, price FROM item_sale WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    ItemSale itemSaleFound = new ItemSale();
                    itemSaleFound.setId(id);
                    itemSaleFound.setFk_product(result.getInt("fk_product"));
                    itemSaleFound.setFk_sale(result.getInt("fk_sale"));
                    itemSaleFound.setQuantity(result.getInt("quantity"));
                    itemSaleFound.setPrice(result.getBigDecimal("price"));
                    logger.info("Item_sale has found successful");
                    return Optional.of(itemSaleFound);
                }
                logger.info("Item_sale is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found item_sale by id = {}", id);
            throw new RuntimeException(e);
        }
    }

    public List<ItemSale> findAll(){
        String sql = "SELECT id, fk_product, fk_sale, quantity, price FROM item_sale";
        List<ItemSale> itemSales = new ArrayList<>();
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    ItemSale itemSale = new ItemSale();
                    itemSale.setId(result.getInt("id"));
                    itemSale.setFk_product(result.getInt("fk_product"));
                    itemSale.setFk_sale(result.getInt("fk_sale"));
                    itemSale.setQuantity(result.getInt("quantity"));
                    itemSale.setPrice(result.getBigDecimal("price"));
                    itemSales.add(itemSale);
                }
            }
            logger.info("All item_sales has found successful");
        } catch (SQLException e) {
            logger.error("Error in found by all item_sales");
            e.printStackTrace();
        }
        return itemSales;
    }
}
