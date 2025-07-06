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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    public void create(User user){
        String sql = "Insert into usuario values (?, ?, ?, ?, ?)";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt  = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            stmt.setString(2, String.join(",", user.getRole()));
            stmt.setString(3, user.getSenha());
            stmt.setString(4, user.getNome());
            stmt.setString(5, user.getEmail());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Insert ran but no rows affected.");
            }
            logger.info("New user registered! Rows inserted: {}", row);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062){
                logger.warn("Duplicate detected: this user exists");
                throw new IllegalStateException("Err in execute SQL: user exists" + e);
            }
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
            logger.info("Update successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update user by cpf = {}", user.getCpf());
            throw new RuntimeException("Err in execute SQL: " + e);
        }
    }

    public void updatePassword(User user){
        String sql = "update usuario set senha = ? where cpf = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getSenha());
            stmt.setString(2, user.getCpf());
            int row = stmt.executeUpdate();
            if (row == 0){
                logger.warn("Update ran but no rows affected.");
            }
            logger.info("Update password successful! Rows affected: {}", row);
        }catch (SQLException e) {
            logger.error("Err in update user password by cpf = {}", user.getCpf());
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
            logger.info("User deleted successful");
        }catch (SQLException e){
            logger.error("Err in delete user by cpf = {}", user.getCpf());
            e.printStackTrace();
        }
    }

    public Optional<User> findUser(User user){
        String sql = "select nome, role, senha, email from usuario where cpf = ?";
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            try(ResultSet result = stmt.executeQuery()){
                 if(!result.next()){
                     logger.info("User is null");
                     return Optional.empty();
                }
                User userFound = new User();
                userFound.setCpf(new Cpf(user.getCpf()));
                userFound.setNome(result.getString("nome"));
                String rolesCsv = result.getString("role");
                List<String> roles = rolesCsv == null || rolesCsv.isBlank()
                        ? List.of()
                        : Arrays.asList(rolesCsv.split(","));
                userFound.setRole(roles);
                userFound.setSenha(result.getString("senha"));
                userFound.setEmail(new Email(result.getString("email")));
                logger.info("User has found successful");
                return Optional.of(userFound);
            }
        }catch (SQLException e){
            logger.error("Err in found user by cpf = {}", user.getCpf());
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll(){
        String sql = "select cpf, role, nome, email from usuario";
        List<User> users = new ArrayList<>();
        try(Connection conn = Conexao.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    User user = new User();
                    user.setCpf(new Cpf(result.getString("cpf")));
                    user.setRole(List.of(result.getString("role")));
                    user.setNome(result.getString("nome"));
                    user.setEmail(new Email(result.getString("email")));
                    users.add(user);
                }
            }
            logger.info("All users has found successful");
        } catch (SQLException e) {
            logger.error("Err in found by all users");
            e.printStackTrace();
        }
        return users;
    }

}