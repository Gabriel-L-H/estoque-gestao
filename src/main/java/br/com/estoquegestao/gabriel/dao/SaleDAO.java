package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.ItemSale;
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
    public void create(Sale sale){
        String sql = "Insert into venda (fk_usuario, valorTotal) values (?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1, sale.getFk_usuario());
            stmt.setBigDecimal(2, sale.getValorTotal());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New sale added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this sale exists");
                throw new IllegalStateException("Err in execute SQL: sale exists" + e);
            }
            logger.error("Err in register new sale");
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void update(Sale sale){
        String sql = "update venda set valorTotal = ? where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setBigDecimal(1, sale.getValorTotal());
            stmt.setInt(2, sale.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update sale by id = {}", sale.getId());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void delete(Sale sale){
        String sql = "delete from venda where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, sale.getId());
            int sucess = stmt.executeUpdate();
            if (sucess == 0){logger.warn("Sale don't found");}
            logger.info("Sale deleted successful");
        }catch (SQLException e){
            logger.error("Err in delete sale by id = {}", sale.getId());
            e.printStackTrace();
        }
    }

    public Optional<Sale> findSale(Sale sale){
        String sql = "select id, fk_usuario, dataHora, valorTotal from venda where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, sale.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    Sale saleFound = new Sale();
                    saleFound.setId(sale.getId());
                    saleFound.setFk_usuario(result.getString("fk_usuario"));
                    saleFound.setDataHora(result.getObject("dataHora", LocalDateTime.class));
                    saleFound.setValorTotal(result.getBigDecimal("valorTotal"));
                    logger.info("Sale has found successful");
                    return Optional.of(saleFound);
                }
                logger.info("Sale is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Err in found sale by id = {}", sale.getId());
            throw new RuntimeException(e);
        }
    }

    public List<Sale> findAll(){
        String sql = "select id, fk_usuario, dataHora, valorTotal from venda";
        List<Sale> sales = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    Sale sale = new Sale();
                    sale.setId(result.getInt("id"));
                    sale.setFk_usuario(result.getString("fk_usuario"));
                    sale.setDataHora(result.getObject("dataHora", LocalDateTime.class));
                    sale.setValorTotal(result.getBigDecimal("valorTotal"));
                    sales.add(sale);
                }
            }
            logger.info("All sales has found successful");
        } catch (SQLException e) {
            logger.error("Err in found by all sales");
            e.printStackTrace();
        }
        return sales;
    }
}
