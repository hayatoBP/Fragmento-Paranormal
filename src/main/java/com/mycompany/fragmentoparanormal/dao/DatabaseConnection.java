package com.mycompany.fragmentoparanormal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerencia a conexão com o banco de dados PostgreSQL.
 * Cria automaticamente o banco "fragmento_paranormal" se ele ainda não existir.
 *
 * Configurações de host, porta, usuário e senha podem ser ajustadas
 * pelas constantes abaixo.
 */
public class DatabaseConnection {

    // ---------------------------------------------------------------
    // Ajuste estas constantes para corresponder à sua instalação do pgAdmin
    // ---------------------------------------------------------------
    private static final String HOST     = "localhost";
    private static final int    PORT     = 5432;
    private static final String DATABASE = "fragmento_paranormal";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgresql";
    // ---------------------------------------------------------------

    private static final String URL_BANCO =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    /** URL do banco padrão "postgres" — usado apenas para criar o banco do jogo. */
    private static final String URL_POSTGRES =
            "jdbc:postgresql://" + HOST + ":" + PORT + "/postgres";

    private static Connection connection = null;

    /**
     * Retorna a conexão singleton com o banco do jogo.
     * Se o banco ainda não existir, ele é criado automaticamente.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            carregarDriver();
            criarBancoSeNecessario();
            connection = DriverManager.getConnection(URL_BANCO, USER, PASSWORD);
            System.out.println("[DB] Conectado ao PostgreSQL: " + DATABASE);
        }
        return connection;
    }

    /**
     * Cria o banco de dados "fragmento_paranormal" caso ele não exista.
     * Conecta ao banco padrão "postgres" para executar o CREATE DATABASE.
     */
    private static void criarBancoSeNecessario() {
        try (Connection conn = DriverManager.getConnection(URL_POSTGRES, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Verifica se o banco já existe
            ResultSet rs = stmt.executeQuery(
                "SELECT 1 FROM pg_database WHERE datname = '" + DATABASE + "'");

            if (!rs.next()) {
                // Banco não existe — cria agora
                stmt.execute("CREATE DATABASE " + DATABASE);
                System.out.println("[DB] Banco de dados '" + DATABASE + "' criado com sucesso.");
            }

        } catch (SQLException e) {
            // Se não conseguir criar, tenta conectar mesmo assim
            // (pode ser que o banco já exista mas a verificação falhou)
            System.err.println("[DB] Aviso ao verificar/criar banco: " + e.getMessage());
        }
    }

    /** Carrega o driver JDBC do PostgreSQL. */
    private static void carregarDriver() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "Driver PostgreSQL não encontrado. " +
                "Verifique se o arquivo postgresql.jar está no classpath.", e);
        }
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
