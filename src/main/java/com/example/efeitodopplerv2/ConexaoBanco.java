package com.example.efeitodopplerv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PBL_EFEITO_DOPPLER";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Carrega a classe do driver JDBC
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Estabelece a conexão com o banco de dados
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão estabelecida com sucesso!");
        } catch (ClassNotFoundException e) {
            System.err.println("Não foi possível encontrar o driver JDBC. Certifique-se de que ele está no classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer a conexão com o banco de dados.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão fechada com sucesso.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco de dados.");
                e.printStackTrace();
            }
        }
    }
}
