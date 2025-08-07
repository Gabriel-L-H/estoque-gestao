package br.com.estoquegestao.gabriel.conexaojdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHikari {
    private static final Dotenv dotenv = Dotenv.configure()
                                        .directory("c:/Users/norag/OneDrive/Documentos/Projetos/estoque-gestao")
                                        .ignoreIfMissing()
                                        .load();
    private static HikariDataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHikari.class);

    static {
        try(InputStream input = ConnectionHikari.class
                                .getClassLoader()
                                .getResourceAsStream("application.properties")) {
            if (input == null){
                throw new RuntimeException("File properties not found");
            }
            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL"));
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASSWORD"));

            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("maximumPoolSize")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle")));
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(100000);
            config.setMaxLifetime(1800000);
            config.setPoolName("HikariConnection");

            dataSource = new HikariDataSource(config);
        }catch (IOException e){
            System.out.println("Error: " + e);
        }
    }

    public ConnectionHikari() {};

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(){
        if(dataSource !=null){
            try{
               dataSource.close();
            }catch (Exception e){
                System.out.println("Error in close connection: " + e.getMessage());
            }
        }
    }

    public static void resetAndCloseConnection(Connection conn) {
    if (conn != null) {
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            logger.error("Connection don't closed");
            throw new RuntimeException("Error in close connection" + e);
        }
    }
}

    public static void safeRollback(Connection conn) throws SQLException {
    if (conn != null) {
        try {
            logger.error("Error detected, attempting rollback");
            conn.rollback();
        } catch (SQLException ex) {
            logger.error("Rollback failed");
            throw new SQLException("Rollback doesn't worked" + ex);
        }
    }
}
}
