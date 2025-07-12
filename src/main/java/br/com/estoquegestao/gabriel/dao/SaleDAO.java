package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
import br.com.estoquegestao.gabriel.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleDAO {
    private static final Logger logger = LoggerFactory.getLogger(SaleDAO.class);
    public void create(Sale sale) throws SQLException{
        String sql = "INSERT INTO sale (fk_user, totalValue) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt  = conn.prepareStatement(sql)) {
                stmt.setString(1, sale.getFk_user());
                stmt.setBigDecimal(2, sale.getTotalValue());
                int row = stmt.executeUpdate();
                if (row == 0) {
                    logger.warn("Insert ran but no rows affected.");
                }
                logger.info("New sale added! Rows inserted: {}", row);
            }
        } catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this sale exists");
                throw new IllegalStateException("Error in execute SQL: sale exists" + e);
            }
            logger.error("Error in register new sale");
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void update(Sale sale) throws SQLException{
        String sql = "UPDATE sale SET totalValue = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setBigDecimal(1, sale.getTotalValue());
                stmt.setInt(2, sale.getId());
                int row = stmt.executeUpdate();
                if (row == 0){
                    logger.warn("Update ran but no rows affected.");
                }
                logger.info("Update successful! Rows affected: {}", row);
            }
        }catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in update sale by id = {}", sale.getId());
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void delete(Sale sale) throws SQLException{
        String sql = "DELETE FROM sale WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sale.getId());
                int success = stmt.executeUpdate();
                if (success == 0) {
                    logger.warn("Sale not found");
                }
                logger.info("Sale deleted successful");
            }
        }catch (SQLException e){
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in delete sale by id = {}", sale.getId());
            e.printStackTrace();
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public Optional<Sale> findSale(Sale sale){
        String sql = "SELECT id, fk_user, dateTime, totalValue FROM sale WHERE id = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, sale.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Sale saleFound = new Sale();
                    saleFound.setId(sale.getId());
                    saleFound.setFk_user(result.getString("fk_user"));
                    saleFound.setDateTime(result.getObject("dateTime", LocalDateTime.class));
                    saleFound.setTotalValue(result.getBigDecimal("totalValue"));
                    logger.info("Sale has found successful");
                    return Optional.of(saleFound);
                }
                logger.info("Sale is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Error in found sale by id = {}", sale.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Sale> findAll(){
        String sql = "SELECT id, fk_user, dateTime, totalValue FROM sale";
        List<Sale> sales = new ArrayList<>();
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Sale sale = new Sale();
                    sale.setId(result.getInt("id"));
                    sale.setFk_user(result.getString("fk_user"));
                    sale.setDateTime(result.getObject("dateTime", LocalDateTime.class));
                    sale.setTotalValue(result.getBigDecimal("totalValue"));
                    sales.add(sale);
                }
            }
            logger.info("All sales has found successful");
        } catch (SQLException e) {
            logger.error("Error in found by all sales");
            e.printStackTrace();
        }
        return sales;
    }
}
