package com.mycompany.fragmentoparanormal.dao;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 * Gerencia a conexão com o banco de dados PostgreSQL (pgAdmin).
 * Configurações de host, porta, banco, usuário e senha podem ser
 * ajustadas pelas constantes abaixo.
 */
public class DatabaseConnection {
 
    // ---------------------------------------------------------------
    // Ajuste estas constantes para corresponder à sua instalação do pgAdmin
    // ---------------------------------------------------------------
    private static final String HOST     = "localhost";
    private static final int    PORT     = 5432;
    private static final String DATABASE = "fragmento_paranormal";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres";
    // ---------------------------------------------------------------
 
    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
 
    private static Connection connection = null;
 
    /** Retorna a conexão singleton, criando-a se necessário. */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver PostgreSQL não encontrado. "
                        + "Verifique se o arquivo postgresql.jar está no classpath.", e);
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Conectado ao PostgreSQL: " + DATABASE);
        }
        return connection;
    }
 
    /** Fecha a conexão com o banco. */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DB] Conexão encerrada.");
            } catch (SQLException e) {
                System.err.println("[DB] Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
 
    /**
     * Testa se o banco está acessível.
     * @return true se a conexão for bem-sucedida
     */
    public static boolean testarConexao() {
        try {
            Connection c = getConnection();
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[DB] Falha na conexão: " + e.getMessage());
            return false;
        }
    }
}
