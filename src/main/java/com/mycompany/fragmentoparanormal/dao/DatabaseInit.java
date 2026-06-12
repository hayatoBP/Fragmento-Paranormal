package com.mycompany.fragmentoparanormal.dao;
 
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
 
/**
 * Inicializa o schema do banco de dados.
 * Chame DatabaseInit.inicializar() na inicialização da aplicação.
 *
 * Tabelas criadas:
 *   jogadores       — dados persistidos do personagem
 *   campanhas       — registro de campanhas (missões iniciadas/concluídas)
 *   itens_jogador   — itens do inventário de cada jogador
 */
public class DatabaseInit {
 
    public static void inicializar() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
 
            // ----------------------------------------------------------
            // Tabela de jogadores
            // ----------------------------------------------------------
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS jogadores (
                    id                  SERIAL PRIMARY KEY,
                    nome                VARCHAR(100)  NOT NULL,
                    classe              VARCHAR(30)   NOT NULL,
                    genero              VARCHAR(20)   NOT NULL,
                    elemento            VARCHAR(30)   NOT NULL,
                    nivel               INT           NOT NULL DEFAULT 1,
                    xp_atual            INT           NOT NULL DEFAULT 0,
                    vida                INT           NOT NULL,
                    vida_maxima         INT           NOT NULL,
                    forca               INT           NOT NULL,
                    investigacao        INT           NOT NULL,
                    poder_paranormal    INT           NOT NULL,
                    pontos_esforco      INT           NOT NULL,
                    pe_maximo           INT           NOT NULL,
                    pontos_atributo     INT           NOT NULL DEFAULT 0,
                    arma_equipada       VARCHAR(100),
                    arma_bonus_dano     INT           DEFAULT 0,
                    criado_em           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    atualizado_em       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """);
 
            // ----------------------------------------------------------
            // Tabela de campanhas
            // ----------------------------------------------------------
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS campanhas (
                    id                  SERIAL PRIMARY KEY,
                    jogador_id          INT           NOT NULL REFERENCES jogadores(id) ON DELETE CASCADE,
                    elemento_missao     VARCHAR(30)   NOT NULL,
                    nome_missao         VARCHAR(100)  NOT NULL,
                    paginas_coletadas   INT           NOT NULL DEFAULT 0,
                    concluida           BOOLEAN       NOT NULL DEFAULT FALSE,
                    boss_desbloqueado   BOOLEAN       NOT NULL DEFAULT FALSE,
                    iniciada_em         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    atualizada_em       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(jogador_id, elemento_missao)
                )
            """);
 
            // ----------------------------------------------------------
            // Tabela de itens do inventário
            // ----------------------------------------------------------
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS itens_jogador (
                    id          SERIAL PRIMARY KEY,
                    jogador_id  INT          NOT NULL REFERENCES jogadores(id) ON DELETE CASCADE,
                    nome        VARCHAR(100) NOT NULL,
                    descricao   TEXT,
                    tipo        VARCHAR(30)  NOT NULL DEFAULT 'COMUM',
                    bonus_dano  INT          NOT NULL DEFAULT 0
                )
            """);
 
            // Migração: adicionar colunas tipo e bonus_dano se não existirem
            try {
                conn.createStatement().execute(
                    "ALTER TABLE itens_jogador ADD COLUMN IF NOT EXISTS tipo VARCHAR(30) NOT NULL DEFAULT 'COMUM'");
                conn.createStatement().execute(
                    "ALTER TABLE itens_jogador ADD COLUMN IF NOT EXISTS bonus_dano INT NOT NULL DEFAULT 0");
            } catch (Exception ex) {
                System.err.println("[DB] Migração itens_jogador: " + ex.getMessage());
            }

            System.out.println("[DB] Schema inicializado com sucesso.");
 
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao inicializar schema: " + e.getMessage());
        }
    }
}
