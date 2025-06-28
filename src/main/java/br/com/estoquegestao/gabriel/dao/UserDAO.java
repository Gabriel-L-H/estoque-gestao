package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {
    public void create(User user){
        String sql = "Insert into usuario values (?, ?, ?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            stmt.setString(2, user.getSenha());
            stmt.setString(3, user.getNome());
            stmt.setString(4, user.getEmail());
            int row = stmt.executeUpdate();
            Logger logger = LoggerFactory.getLogger(UserDAO.class);
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New user registered! Rows inserted: {}", row);
        } catch (SQLException e) {
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

}