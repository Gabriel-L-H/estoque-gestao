package br.com.estoquegestao.gabriel.dao;

import br.com.estoquegestao.gabriel.conexaojdbc.ConnectionHikari;
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
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO user VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getCpf());
                stmt.setString(2, String.join(",", user.getRole()));
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getName());
                stmt.setString(5, user.getEmail());

                int row = stmt.executeUpdate();
                if (row == 0) {
                    logger.warn("Insert ran but no rows affected.");
                }
                logger.info("New user registered! Rows inserted: {}", row);
            }
            conn.commit();
        } catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            if (e.getErrorCode() == 1062) {
                logger.warn("Duplicate detected: this user exists");
                throw new IllegalStateException("Error in execute SQL: user exists" + e);
            }
            logger.error("Error in register new user");
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void update(User user) throws SQLException{
        String sql = "UPDATE user SET name = ?, email = ? WHERE cpf = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getCpf());
                int row = stmt.executeUpdate();
                if (row == 0) {
                    logger.warn("Update ran but no rows affected.");
                }
                logger.info("Update successful! Rows affected: {}", row);
            }
            conn.commit();
        }catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in update user by cpf = {}", user.getCpf());
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void updatePassword(User user) throws SQLException{
        String sql = "UPDATE user SET password = ? WHERE cpf = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, user.getPassword());
                stmt.setString(2, user.getCpf());
                int row = stmt.executeUpdate();
                if (row == 0){
                    logger.warn("Update ran but no rows affected.");
                }
                logger.info("Update password successful! Rows affected: {}", row);
            }
            conn.commit();
        }catch (SQLException e) {
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in update user password by cpf = {}", user.getCpf());
            throw new RuntimeException("Error in execute SQL: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public void delete(User user) throws SQLException{
        String sql = "DELETE FROM user WHERE cpf = ?";
        Connection conn = null;
        try {
            conn = ConnectionHikari.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getCpf());
                int success = stmt.executeUpdate();
                if (success == 0) {
                    logger.warn("User not found");
                }
                logger.info("User deleted successful");
            }
            conn.commit();
        }catch (SQLException e){
            ConnectionHikari.safeRollback(conn);
            logger.error("Error in delete user by cpf = {}", user.getCpf());
            throw new SQLException("Error in delete user: " + e);
        } finally {
            ConnectionHikari.resetAndCloseConnection(conn);
        }
    }

    public Optional<User> findUser(User user){
        String sql = "SELECT name, role, password, email FROM user WHERE cpf = ?";
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, user.getCpf());
            try(ResultSet result = stmt.executeQuery()){
                 if(!result.next()){
                     return Optional.empty();
                }
                User userFound = new User();
                userFound.setCpf(new Cpf(user.getCpf()));
                userFound.setName(result.getString("name"));
                String rolesCsv = result.getString("role");
                List<String> roles = rolesCsv == null || rolesCsv.isBlank()
                        ? List.of()
                        : Arrays.asList(rolesCsv.split(","));
                userFound.setRole(roles);
                userFound.setPassword(result.getString("password"));
                userFound.setEmail(new Email(result.getString("email")));
                logger.info("User has found successful");
                return Optional.of(userFound);
            }
        }catch (SQLException e){
            logger.error("Error in found user by cpf = {}", user.getCpf());
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll(){
        String sql = "SELECT cpf, role, name, email FROM user";
        List<User> users = new ArrayList<>();
        try(Connection conn = ConnectionHikari.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            try(ResultSet result = stmt.executeQuery()){
                while(result.next()){
                    User user = new User();
                    user.setCpf(new Cpf(result.getString("cpf")));
                    user.setRole(List.of(result.getString("role")));
                    user.setName(result.getString("name"));
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