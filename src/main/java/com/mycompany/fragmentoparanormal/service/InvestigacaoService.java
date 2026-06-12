package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Artefato;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.service.CatalogoArtefatosService;
import com.mycompany.fragmentoparanormal.util.TipoItem;
import java.util.Random;

public class InvestigacaoService {

    private static final Random random = new Random();

    // ── Limite de itens por sessão de missão ─────────────────────────
    public static final int MAX_ITENS_SESSAO = 3;

    // ── Armas loot ───────────────────────────────────────────────────
    public static final Arma[] ARMAS_LOOT = {
        new Arma("Faca Ritualística",    10, "Lâmina entalhada com símbolos ocultistas."),
        new Arma("Pistola Antiga",       12, "Revólver enferrujado, mas ainda dispara."),
        new Arma("Revólver .38",         14, "Calibre médio. Confiável em combate próximo."),
        new Arma("Machete Enferrujado",  16, "Lâmina longa e pesada."),
        new Arma("Espingarda Quebrada",  18, "A coronha está rachada, mas o cano funciona."),
        new Arma("Adaga Paranormal",     20, "Parece vibrar nas suas mãos."),
        new Arma("Tocha Abençoada",       8, "Madeira sacralizada. Afasta espíritos menores."),
        new Arma("Foice Enferrujada",    15, "Pesada, mas eficaz."),
        new Arma("Faca de Caça",         11, "Ferramenta robusta de caçador experiente."),
        new Arma("Rifle Quebrado",       22, "O mecanismo falha às vezes, mas o impacto é brutal."),
    };

    // ── Consumíveis (foco: cura e PE) ────────────────────────────────
    private static final Item[] CONSUMIVEIS = {
        new Item("Bandagem Improvisada",  "Curativo básico. Restaura 30 pontos de vida.",             TipoItem.CURA,         30),
        new Item("Bandagem Reforçada",    "Curativo resistente. Restaura 45 pontos de vida.",          TipoItem.CURA,         45),
        new Item("Cristal de Cura",       "Cristal pulsante paranormal. Restaura 55 vida.",            TipoItem.CURA,         55),
        new Item("Elixir Sanguíneo",      "Líquido escarlate sobrenatural. Restaura 70 vida.",         TipoItem.CURA,         70),
        new Item("Poção de Esforço",      "Líquido azul turvo. Restaura 20 PE.",                      TipoItem.RESTAURAR_PE, 20),
        new Item("Frasco de Energia",     "Energia paranormal concentrada. Restaura 35 PE.",          TipoItem.RESTAURAR_PE, 35),
        new Item("Tintura Arcana",        "Mistura de ervas ritualísticas. Restaura 50 PE.",           TipoItem.RESTAURAR_PE, 50),
    };

    // ── Permanentes por classe ────────────────────────────────────────
    private static final Item[] PERMANENTES_FISICO = {
        new Item("Pedra da Força",       "Irradia poder físico. +5 Força permanentemente.",          TipoItem.BOOST_FORCA,    5),
        new Item("Tônica do Caçador",    "Bebida de caçadores. +8 Força permanentemente.",           TipoItem.BOOST_FORCA,    8),
        new Item("Coração Cristalizado", "Órgão petrificado. +25 Vida Máxima permanentemente.",      TipoItem.BOOST_VIDA_MAX, 25),
        new Item("Amuleto de Proteção",  "Protege o portador. +15 Vida Máxima permanentemente.",     TipoItem.BOOST_VIDA_MAX, 15),
    };

    private static final Item[] PERMANENTES_OCULTISTA = {
        new Item("Amuleto Paranormal",    "Medalhão arcano. +6 Poder Paranormal permanentemente.",   TipoItem.BOOST_PARANORMAL, 6),
        new Item("Essência do Além",      "Névoa capturada. +10 Poder Paranormal permanentemente.",  TipoItem.BOOST_PARANORMAL, 10),
        new Item("Pergaminho Amaldiçoado","Em língua morta. +8 Poder Paranormal permanentemente.",   TipoItem.BOOST_PARANORMAL, 8),
        new Item("Grimório Fragmentado",  "Livro proibido. +12 Poder Paranormal permanentemente.",   TipoItem.BOOST_PARANORMAL, 12),
    };

    // ── Artefatos ────────────────────────────────────────────────────
    // Artefatos gerenciados pelo CatalogoArtefatosService

    // ── Eventos ruins ─────────────────────────────────────────────────
    /**
     * Retorna string de evento ruim. Formato:
     *   "EVENTO_RUIM:VIDA:20"   → perde 20 de vida
     *   "EVENTO_RUIM:PE:15"     → perde 15 de PE
     *   "EVENTO_RUIM:ATORDOA"   → perde turno (flag no GameState)
     */
    private static final String[] EVENTOS_RUINS = {
        "EVENTO_RUIM:VIDA:15",
        "EVENTO_RUIM:VIDA:25",
        "EVENTO_RUIM:VIDA:35",
        "EVENTO_RUIM:PE:10",
        "EVENTO_RUIM:PE:20",
        "EVENTO_RUIM:ATORDOA",
    };

    /**
     * Método principal de investigação.
     *
     * Tabela de probabilidades (em ordem de verificação):
     *   1. Fragmento (página)   : 10% + log10(inv)*10, teto 45%
     *   2. Inimigo surpresa     : 10%
     *   3. Evento ruim          : 12%
     *   4. Pista rara (+10 XP)  :  8%
     *   5. Item (se abaixo do limite de sessão): 18%
     *   6. Nada                 : restante
     *
     * @param jogador       personagem do jogador
     * @param itensSessao   quantos itens já foram encontrados nesta sessão
     */
    public static String investigar(Personagem jogador, int itensSessao) {
        double inv = jogador.getInvestigacao();

        // Bônus de investigação do Especialista
        double bonusInv = 1.0;
        if (jogador.getNivelBonusInvestigacao() > 0 && !jogador.isBonusInvestigacaoUsado()) {
            bonusInv = switch (jogador.getNivelBonusInvestigacao()) {
                case 1 -> 1.30;  // Buscar Evidências
                case 2 -> 1.60;  // Reconstrução da Cena
                case 3 -> 1.90;  // Perfil Completo
                default -> 1.0;
            };
        }

        double chanceFragmento = (10.0 + 10.0 * Math.log10(Math.max(1, inv))) * bonusInv;
        chanceFragmento = Math.min(chanceFragmento, 50.0); // teto 50% mesmo com bônus

        double roll = random.nextDouble() * 100.0;

        // 1) Fragmento
        if (roll < chanceFragmento) {
            if (bonusInv > 1.0) jogador.marcarBonusInvestigacaoUsado();
            return "FRAGMENTO";
        }

        // 2) Inimigo surpresa — 10%
        if (random.nextDouble() * 100.0 < 10.0) return "INIMIGO_SURPRESA";

        // 3) Evento ruim — 12%
        if (random.nextDouble() * 100.0 < 12.0) {
            String evento = EVENTOS_RUINS[random.nextInt(EVENTOS_RUINS.length)];
            aplicarEventoRuim(jogador, evento);
            return evento;
        }

        // 4) Pista rara — 8%
        if (random.nextDouble() * 100.0 < 8.0) return "PISTA_RARA";

        // 5) Item — 18%, mas apenas se abaixo do limite de sessão
        if (itensSessao < MAX_ITENS_SESSAO && random.nextDouble() * 100.0 < 18.0) {
            if (bonusInv > 1.0) jogador.marcarBonusInvestigacaoUsado();
            return gerarItem(jogador);
        }

        return "NADA";
    }

    /** Aplica imediatamente o efeito do evento ruim no jogador. */
    public static void aplicarEventoRuim(Personagem jogador, String evento) {
        if (!evento.startsWith("EVENTO_RUIM:")) return;
        String[] partes = evento.split(":");
        if (partes.length < 2) return;
        switch (partes[1]) {
            case "VIDA" -> {
                int dano = partes.length >= 3 ? Integer.parseInt(partes[2]) : 10;
                jogador.setVida(Math.max(1, jogador.getVida() - dano));
            }
            case "PE" -> {
                int perda = partes.length >= 3 ? Integer.parseInt(partes[2]) : 10;
                jogador.setPontosEsforco(Math.max(0, jogador.getPontosEsforco() - perda));
            }
            // ATORDOA é tratado no controller via flag
        }
    }

    /** Descrição amigável do evento ruim para exibir ao jogador. */
    public static String descricaoEventoRuim(String evento) {
        if (!evento.startsWith("EVENTO_RUIM:")) return "Algo deu errado.";
        String[] partes = evento.split(":");
        return switch (partes[1]) {
            case "VIDA"    -> "⚠ Armadilha! Você perdeu " + partes[2] + " pontos de vida.";
            case "PE"      -> "⚠ Aura hostil! Você perdeu " + partes[2] + " PE.";
            case "ATORDOA" -> "⚠ Presença perturbadora! Você perde a próxima ação.";
            default        -> "⚠ Algo de ruim aconteceu.";
        };
    }

    private static String gerarItem(Personagem jogador) {
        double roll = random.nextDouble() * 100.0;

        // 55% consumível (foco em cura/PE)
        if (roll < 55.0) {
            Item base = CONSUMIVEIS[random.nextInt(CONSUMIVEIS.length)];
            jogador.getInventario().adicionarItem(
                new Item(base.getNome(), base.getDescricao(), base.getTipo(), base.getEfeito()));
            return "ITEM_CONSUMIVEL:" + base.getNome();
        }

        // 25% arma
        if (roll < 80.0) {
            Arma arma = ARMAS_LOOT[random.nextInt(ARMAS_LOOT.length)];
            return "ARMA:" + arma.getNome() + ":" + arma.getBonusDano();
        }

        // 15% permanente
        if (roll < 95.0) {
            Item[] pool = escolherPoolPermanente(jogador.getClasse());
            Item base   = pool[random.nextInt(pool.length)];
            jogador.getInventario().adicionarItem(
                new Item(base.getNome(), base.getDescricao(), base.getTipo(), base.getEfeito()));
            return "ITEM_PERMANENTE:" + base.getNome();
        }

        // 5% artefato — usa catálogo completo de Artefatos
        Artefato artefato = CatalogoArtefatosService.sortearAleatorio();
        jogador.getInventario().adicionarItem(artefato);
        return "ITEM_ARTEFATO:" + artefato.getNome();
    }

    private static Item[] escolherPoolPermanente(ClassePersonagem classe) {
        return switch (classe) {
            case COMBATENTE, ESPECIALISTA -> PERMANENTES_FISICO;
            case OCULTISTA               -> PERMANENTES_OCULTISTA;
            default                      -> PERMANENTES_FISICO;
        };
    }
}
