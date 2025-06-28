package br.com.estoquegestao.gabriel.conexaojdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {
    public static HikariDataSource dataSource;

    static {
        try(InputStream input = Conexao.class
                                .getClassLoader()
                                .getResourceAsStream("application.properties")) {
            if (input == null){
                throw new RuntimeException("File properties not found");
            }
            Properties properties = new Properties();
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("jdbcUrl"));
            config.setUsername(properties.getProperty("username"));
            config.setPassword(properties.getProperty("password"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("maximumPoolSize")));
            config.setMinimumIdle(Integer.parseInt(properties.getProperty("minimumIdle")));
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(100000);
            config.setMaxLifetime(1800000);
            config.setPoolName("HikariConnection");

            dataSource = new HikariDataSource(config);
        }catch (IOException e){
            System.out.println("Err: " + e);
        }
    }

    public Conexao() {};

    public static Connection getConexao() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(){
        if(dataSource !=null){
            try{
               dataSource.close();
            }catch (Exception e){
                System.out.println("Err: " + e.getMessage());
            }
        }
    }
}
