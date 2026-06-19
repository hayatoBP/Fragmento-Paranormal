package com.mycompany.fragmentoparanormal.dao;

import com.mycompany.fragmentoparanormal.util.Elemento;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO responsável pela persistência do progresso das campanhas (missões).
 * Cada campanha é identificada pelo par (jogador_id, elemento_missao).
 */
public class CampanhaDAO {

    public record CampanhaData(int paginasColetadas, boolean concluida, boolean bossDesbloqueado, int localAtual, String paginasLocaisEncontradas) {}


    /**
     * Salva ou atualiza o progresso de uma missão para um jogador.
     *
     * @param jogadorId        id do jogador no banco
     * @param elemento         elemento da missão (ex: SANGUE, MORTE…)
     * @param nomeMissao       nome da missão
     * @param paginasColetadas páginas coletadas até agora
     * @param concluida        se a missão foi concluída
     * @param bossDesbloqueado se o boss foi desbloqueado
     */
    public static void salvarCampanha(int jogadorId,
                                      Elemento elemento,
                                      String nomeMissao,
                                      int paginasColetadas,
                                      boolean concluida,
                                      boolean bossDesbloqueado,
                                      int localAtual,
                                      String paginasLocaisEncontradas) {
        String sql = """
            INSERT INTO campanhas
                (jogador_id, elemento_missao, nome_missao,
                 paginas_coletadas, concluida, boss_desbloqueado, local_atual, paginas_locais_encontradas)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (jogador_id, elemento_missao)
            DO UPDATE SET
                paginas_coletadas = EXCLUDED.paginas_coletadas,
                concluida         = EXCLUDED.concluida,
                boss_desbloqueado = EXCLUDED.boss_desbloqueado,
                local_atual       = EXCLUDED.local_atual,
                paginas_locais_encontradas = EXCLUDED.paginas_locais_encontradas,
                atualizada_em     = CURRENT_TIMESTAMP
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt    (1, jogadorId);
            ps.setString (2, elemento.name());
            ps.setString (3, nomeMissao);
            ps.setInt    (4, paginasColetadas);
            ps.setBoolean(5, concluida);
            ps.setBoolean(6, bossDesbloqueado);
            ps.setInt    (7, localAtual);
            ps.setString (8, paginasLocaisEncontradas);
            ps.executeUpdate();
            System.out.println("[DAO] Campanha salva: " + nomeMissao + " (" + paginasColetadas + " páginas)");

        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao salvar campanha: " + e.getMessage());
        }
    }

    /**
     * Carrega todas as campanhas de um jogador.
     * @return mapa Elemento → CampanhaData
     */
    public static Map<Elemento, CampanhaData> carregarCampanhas(int jogadorId) {
        Map<Elemento, CampanhaData> mapa = new LinkedHashMap<>();
        String sql = """
            SELECT elemento_missao, paginas_coletadas, concluida, boss_desbloqueado, local_atual, paginas_locais_encontradas
            FROM campanhas
            WHERE jogador_id = ?
            ORDER BY iniciada_em
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jogadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Elemento el = Elemento.valueOf(rs.getString("elemento_missao"));
                int paginas = rs.getInt("paginas_coletadas");
                int concluida = rs.getBoolean("concluida") ? 1 : 0;
                boolean concluidaBool = rs.getBoolean("concluida");
                boolean bossDesbloqueadoBool = rs.getBoolean("boss_desbloqueado");
                int localAtual = rs.getInt("local_atual");
                String paginasLocaisEncontradas = rs.getString("paginas_locais_encontradas");
                mapa.put(el, new CampanhaData(paginas, concluidaBool, bossDesbloqueadoBool, localAtual, paginasLocaisEncontradas));
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao carregar campanhas: " + e.getMessage());
        }
        return mapa;
    }

    /** Retorna o número de páginas coletadas para uma missão específica. */
    public static int getPaginasColetadas(int jogadorId, Elemento elemento) {
        String sql = """
            SELECT paginas_coletadas FROM campanhas
            WHERE jogador_id = ? AND elemento_missao = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jogadorId);
            ps.setString(2, elemento.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao ler páginas: " + e.getMessage());
        }
        return 0;
    }
}