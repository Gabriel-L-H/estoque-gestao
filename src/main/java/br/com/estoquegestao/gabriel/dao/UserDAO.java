package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.Conexao;
import br.com.estoquegestao.gabriel.model.Cpf;
import br.com.estoquegestao.gabriel.model.Email;
import br.com.estoquegestao.gabriel.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
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
            logger.error("Err in register new user");
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void update(User user){
        String sql = "update usuario set nome = ?, email = ? where cpf = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getNome());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getCpf());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update with sucess! Rows inserted: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update user by cpf = {}", user.getCpf());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void delete(User user){
        String sql = "delete from usuario where cpf = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            int sucess = stmt.executeUpdate();
            if (sucess == 0){logger.warn("User don't found");}
            logger.info("User deleted with sucess");
        }catch (SQLException e){
            logger.error("Err in delete user by cpf = {}", user.getCpf());
            e.printStackTrace();
        }
    }

    public User findUser(User user){
        String sql = "select nome, email from usuario where cpf = ?";
        User userFound = new User();
        userFound.setCpf(new Cpf(user.getCpf()));
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            try(ResultSet result = stmt.executeQuery()){
                 if(result.next()){
                    userFound.setNome(result.getString("nome"));
                    userFound.setEmail(new Email(result.getString("email")));
                }
            }
            logger.info("User has found with sucess");
        }catch (SQLException e){
            logger.error("Err in found user by cpf = {}", user.getCpf());
            e.printStackTrace();
        }
        return userFound;
    }

    public List<User> findAll(){
        String sql = "select cpf, nome, email from usuario";
        List<User> users = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    User user = new User();
                    user.setCpf(new Cpf(result.getString("cpf")));
                    user.setNome(result.getString("nome"));
                    user.setEmail(new Email(result.getString("email")));
                    users.add(user);
                }
            }
            logger.info("Users has found with sucess");
        } catch (SQLException e) {
            logger.error("Err in found by users");
            e.printStackTrace();
        }
        return users;
    }

}