package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.ItemSale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemSaleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ItemSaleDAO.class);
    public void create(ItemSale itemSale){
        String sql = "Insert into item_venda (fk_produto, fk_venda, quantidade, preco) values (?, ?, ?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setInt(1, itemSale.getFk_produto());
            stmt.setInt(2, itemSale.getFk_venda());
            stmt.setInt(3, itemSale.getQuantidade());
            stmt.setBigDecimal(4, itemSale.getPreco());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New item_sale added! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this item_sale exists");
                throw new IllegalStateException("Err in execute SQL: item_sale exists" + e);
            }
            logger.error("Err in register new item_sale");
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void update(ItemSale itemSale){
        String sql = "update item_venda set quantidade = ?, preco = ? where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, itemSale.getQuantidade());
            stmt.setBigDecimal(2, itemSale.getPreco());
            stmt.setInt(3, itemSale.getId());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update item_sale by id = {}", itemSale.getId());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void delete(ItemSale itemSale){
        String sql = "delete from item_venda where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, itemSale.getId());
            int sucess = stmt.executeUpdate();
            if (sucess == 0){logger.warn("Item_sale don't found");}
            logger.info("Item_sale deleted successful");
        }catch (SQLException e){
            logger.error("Err in delete item_sale by id = {}", itemSale.getId());
            e.printStackTrace();
        }
    }

    public Optional<ItemSale> findItemSale(ItemSale itemSale){
        String sql = "select fk_produto, fk_venda, quantidade, preco from item_venda where id = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, itemSale.getId());
            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    ItemSale itemSaleFound = new ItemSale();
                    itemSaleFound.setId(itemSale.getId());
                    itemSaleFound.setFk_produto(result.getInt("fk_produto"));
                    itemSaleFound.setFk_venda(result.getInt("fk_venda"));
                    itemSaleFound.setQuantidade(result.getInt("quantidade"));
                    itemSaleFound.setPreco(result.getBigDecimal("preco"));
                    logger.info("Item_sale has found successful");
                    return Optional.of(itemSaleFound);
                }
                logger.info("Item_sale is null");
                return Optional.empty();
            }
        }catch (SQLException e){
            logger.error("Err in found item_sale by id = {}", itemSale.getId());
            throw new RuntimeException(e);
        }
    }

    public List<ItemSale> findAll(){
        String sql = "select id, fk_produto, fk_venda, quantidade, preco from item_venda";
        List<ItemSale> itemSales = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    ItemSale itemSale = new ItemSale();
                    itemSale.setId(result.getInt("id"));
                    itemSale.setFk_produto(result.getInt("fk_produto"));
                    itemSale.setFk_venda(result.getInt("fk_venda"));
                    itemSale.setQuantidade(result.getInt("quantidade"));
                    itemSale.setPreco(result.getBigDecimal("preco"));
                    itemSales.add(itemSale);
                }
            }
            logger.info("All item_sales has found successful");
        } catch (SQLException e) {
            logger.error("Err in found by all item_sales");
            e.printStackTrace();
        }
        return itemSales;
    }
}
