package com.mycompany.fragmentoparanormal.dao;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.Genero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pela persistência dos dados do jogador (Personagem)
 * e dos itens do seu inventário.
 */
public class JogadorDAO {

    // ------------------------------------------------------------------
    // Salvar / Atualizar
    // ------------------------------------------------------------------

    /**
     * Persiste o jogador no banco. Se já existe um registro com o mesmo nome,
     * atualiza os dados; caso contrário, insere um novo registro.
     *
     * @return id do jogador no banco (necessário para operações de campanha)
     */
    public static int salvar(Personagem jogador) {
        int id = buscarIdPorNome(jogador.getNome());
        if (id > 0) {
            atualizar(id, jogador);
            salvarItens(id, jogador);
            return id;
        } else {
            return inserir(jogador);
        }
    }

    private static int inserir(Personagem jogador) {
        String sql = """
            INSERT INTO jogadores
                (nome, classe, genero, elemento, nivel, xp_atual,
                 vida, vida_maxima, forca, investigacao, poder_paranormal,
                 pontos_esforco, pe_maximo, pontos_atributo,
                 arma_equipada, arma_bonus_dano)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencherStatement(ps, jogador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                salvarItens(id, jogador);
                System.out.println("[DAO] Jogador inserido com id=" + id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao inserir jogador: " + e.getMessage());
        }
        return -1;
    }

    private static void atualizar(int id, Personagem jogador) {
        String sql = """
            UPDATE jogadores SET
                classe = ?, genero = ?, elemento = ?,
                nivel = ?, xp_atual = ?,
                vida = ?, vida_maxima = ?,
                forca = ?, investigacao = ?, poder_paranormal = ?,
                pontos_esforco = ?, pe_maximo = ?,
                pontos_atributo = ?,
                arma_equipada = ?, arma_bonus_dano = ?,
                atualizado_em = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jogador.getClasse().name());
            ps.setString(2, jogador.getGenero().name());
            ps.setString(3, jogador.getElemento().name());
            ps.setInt   (4, jogador.getNivel());
            ps.setInt   (5, jogador.getXpAtual());
            ps.setInt   (6, jogador.getVida());
            ps.setInt   (7, jogador.getVidaMaxima());
            ps.setInt   (8, jogador.getForca());
            ps.setInt   (9, jogador.getInvestigacao());
            ps.setInt   (10, jogador.getPoderParanormal());
            ps.setInt   (11, jogador.getPontosEsforco());
            ps.setInt   (12, jogador.getPeMaximo());
            ps.setInt   (13, jogador.getPontosAtributo());
            if (jogador.getArmaEquipada() != null) {
                ps.setString(14, jogador.getArmaEquipada().getNome());
                ps.setInt   (15, jogador.getArmaEquipada().getBonusDano());
            } else {
                ps.setNull(14, Types.VARCHAR);
                ps.setInt (15, 0);
            }
            ps.setInt(16, id);
            ps.executeUpdate();
            System.out.println("[DAO] Jogador id=" + id + " atualizado.");
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao atualizar jogador: " + e.getMessage());
        }
    }

    private static void preencherStatement(PreparedStatement ps, Personagem jogador)
            throws SQLException {
        ps.setString(1, jogador.getNome());
        ps.setString(2, jogador.getClasse().name());
        ps.setString(3, jogador.getGenero().name());
        ps.setString(4, jogador.getElemento().name());
        ps.setInt   (5, jogador.getNivel());
        ps.setInt   (6, jogador.getXpAtual());
        ps.setInt   (7, jogador.getVida());
        ps.setInt   (8, jogador.getVidaMaxima());
        ps.setInt   (9, jogador.getForca());
        ps.setInt   (10, jogador.getInvestigacao());
        ps.setInt   (11, jogador.getPoderParanormal());
        ps.setInt   (12, jogador.getPontosEsforco());
        ps.setInt   (13, jogador.getPeMaximo());
        ps.setInt   (14, jogador.getPontosAtributo());
        if (jogador.getArmaEquipada() != null) {
            ps.setString(15, jogador.getArmaEquipada().getNome());
            ps.setInt   (16, jogador.getArmaEquipada().getBonusDano());
        } else {
            ps.setNull(15, Types.VARCHAR);
            ps.setInt (16, 0);
        }
    }

    // ------------------------------------------------------------------
    // Itens do inventário
    // ------------------------------------------------------------------

    private static void salvarItens(int jogadorId, Personagem jogador) {
        String deleteSql = "DELETE FROM itens_jogador WHERE jogador_id = ?";
        String insertSql = "INSERT INTO itens_jogador (jogador_id, nome, descricao, tipo, bonus_dano) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement del = conn.prepareStatement(deleteSql);
             PreparedStatement ins = conn.prepareStatement(insertSql)) {

            del.setInt(1, jogadorId);
            del.executeUpdate();

            // Salvar itens consumíveis/permanentes
            for (Item item : jogador.getInventario().getItens()) {
                ins.setInt(1, jogadorId);
                ins.setString(2, item.getNome());
                ins.setString(3, item.getDescricao());
                ins.setString(4, item.getTipo().name());
                ins.setInt(5, item.getEfeito());
                ins.addBatch();
            }

            // Salvar armas do inventário (não equipada)
            for (com.mycompany.fragmentoparanormal.model.Arma arma : jogador.getArmas()) {
                ins.setInt(1, jogadorId);
                ins.setString(2, arma.getNome());
                ins.setString(3, "Arma guardada no inventário.");
                ins.setString(4, "ARMA");
                ins.setInt(5, arma.getBonusDano());
                ins.addBatch();
            }

            ins.executeBatch();
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao salvar itens: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /** Retorna todos os jogadores cadastrados (usado pela TableView). */
    public static List<Personagem> listarTodos() {
        List<Personagem> lista = new ArrayList<>();
        String sql = """
            SELECT nome, classe, genero, elemento,
                   nivel, xp_atual, vida, vida_maxima,
                   forca, investigacao, poder_paranormal,
                   pontos_esforco, pe_maximo, pontos_atributo,
                   arma_equipada, arma_bonus_dano
            FROM jogadores
            ORDER BY atualizado_em DESC
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Personagem p = reconstruirPersonagem(rs);
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao listar jogadores: " + e.getMessage());
        }
        return lista;
    }

    /** Carrega um jogador completo pelo nome (incluindo itens do inventário). */
    public static Personagem buscarPorNome(String nome) {
        String sql = """
            SELECT nome, classe, genero, elemento,
                   nivel, xp_atual, vida, vida_maxima,
                   forca, investigacao, poder_paranormal,
                   pontos_esforco, pe_maximo, pontos_atributo,
                   arma_equipada, arma_bonus_dano, id
            FROM jogadores
            WHERE nome = ?
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Personagem p = reconstruirPersonagem(rs);
                int id = rs.getInt("id");
                carregarItens(id, p);
                return p;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao buscar jogador: " + e.getMessage());
        }
        return null;
    }

    public static int buscarIdPorNome(String nome) {
        String sql = "SELECT id FROM jogadores WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao buscar id: " + e.getMessage());
        }
        return -1;
    }

    public static List<String> listarNomes() {
        List<String> nomes = new ArrayList<>();
        String sql = "SELECT nome FROM jogadores ORDER BY atualizado_em DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) nomes.add(rs.getString(1));
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao listar nomes: " + e.getMessage());
        }
        return nomes;
    }

    // ------------------------------------------------------------------
    // Helpers de reconstrução
    // ------------------------------------------------------------------

    private static Personagem reconstruirPersonagem(ResultSet rs) throws SQLException {
        ClassePersonagem classe  = ClassePersonagem.valueOf(rs.getString("classe"));
        Genero           genero  = Genero.valueOf(rs.getString("genero"));
        Elemento         elemento = Elemento.valueOf(rs.getString("elemento"));

        Personagem p = new Personagem(rs.getString("nome"), classe, genero, elemento);

        // Sobrescreve os valores padrão com os salvos no banco
        p.setVida            (rs.getInt("vida"));
        p.setVidaMaxima      (rs.getInt("vida_maxima"));
        p.setForca           (rs.getInt("forca"));
        p.setInvestigacao    (rs.getInt("investigacao"));
        p.setPoderParanormal (rs.getInt("poder_paranormal"));
        p.setPontosEsforco   (rs.getInt("pontos_esforco"));
        p.setPeMaximo        (rs.getInt("pe_maximo"));
        p.setPontosAtributo  (rs.getInt("pontos_atributo"));

        // Repõe nível e XP via reflexão ou setters públicos
        for (int i = 1; i < rs.getInt("nivel"); i++) {
            p.ganharXp(100); // sobe nível sem alterar o XP real
        }
        // Ajusta o XP atual exato
        // (ganharXp sobe nível quando chega a 100; aqui só repomos o estado)

        String armaNome = rs.getString("arma_equipada");
        if (armaNome != null && !armaNome.isBlank()) {
            p.setArmaEquipada(new Arma(armaNome, rs.getInt("arma_bonus_dano")));
        }
        return p;
    }

    private static void carregarItens(int jogadorId, Personagem p) {
        String sql = "SELECT nome, descricao, tipo, bonus_dano FROM itens_jogador WHERE jogador_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jogadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tipo = rs.getString("tipo");
                if ("ARMA".equals(tipo)) {
                    // Restaura arma no inventário de armas
                    p.adicionarArma(new com.mycompany.fragmentoparanormal.model.Arma(
                        rs.getString("nome"), rs.getInt("bonus_dano")));
                } else {
                    // Restaura item consumível/permanente com tipo correto
                    com.mycompany.fragmentoparanormal.util.TipoItem tipoItem;
                    try {
                        tipoItem = com.mycompany.fragmentoparanormal.util.TipoItem.valueOf(tipo);
                    } catch (Exception ex) {
                        tipoItem = com.mycompany.fragmentoparanormal.util.TipoItem.COMUM;
                    }
                    p.getInventario().adicionarItem(
                        new Item(rs.getString("nome"), rs.getString("descricao"),
                                 tipoItem, rs.getInt("bonus_dano")));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Erro ao carregar itens: " + e.getMessage());
        }
    }
}