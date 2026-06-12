package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.ItemLoja;
import com.mycompany.fragmentoparanormal.model.ItemLoja.Raridade;
import com.mycompany.fragmentoparanormal.util.TipoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Gerencia a loja da Ivete.
 *
 * - 3 itens sorteados ao entrar (persiste até o jogador morrer)
 * - Cada item pode ser comprado apenas 1 vez
 * - Distribuição: 1 Comum + 1 Raro + 1 Muito Raro (sempre)
 * - Reset: ao morrer/fugir (chamado de StatusController)
 */
public class LojaService {

    private static final Random random = new Random();

    // ── Catálogo completo por raridade ───────────────────────────────

    private static final List<ItemLoja> CATALOGO_COMUM = List.of(
        new ItemLoja("Bandagem Reforçada",   "Curativo de campo. Restaura 50 vida.",          TipoItem.CURA,         50, Raridade.COMUM,      30),
        new ItemLoja("Frasco de Energia",    "Recupera 35 PE.",                               TipoItem.RESTAURAR_PE, 35, Raridade.COMUM,      30),
        new ItemLoja("Ervas Medicinais",     "Mistura natural. Restaura 40 vida.",             TipoItem.CURA,         40, Raridade.COMUM,      25),
        new ItemLoja("Tintura Energizante",  "Restaura 25 PE.",                               TipoItem.RESTAURAR_PE, 25, Raridade.COMUM,      20),
        new ItemLoja("Poção Simples",        "Restaura 60 vida.",                             TipoItem.CURA,         60, Raridade.COMUM,      35),
        new ItemLoja("Extrato Paranormal",   "Restaura 40 PE.",                               TipoItem.RESTAURAR_PE, 40, Raridade.COMUM,      35)
    );

    private static final List<ItemLoja> CATALOGO_RARO = List.of(
        new ItemLoja("Elixir Sanguíneo",     "Líquido sobrenatural. Restaura 80 vida.",       TipoItem.CURA,         80, Raridade.RARO,       70),
        new ItemLoja("Pedra da Força",       "+6 Força permanentemente.",                     TipoItem.BOOST_FORCA,   6, Raridade.RARO,       80),
        new ItemLoja("Cristal de Cura",      "Cristal paranormal. Restaura 70 vida.",         TipoItem.CURA,         70, Raridade.RARO,       65),
        new ItemLoja("Frasco Concentrado",   "Restaura 60 PE.",                               TipoItem.RESTAURAR_PE, 60, Raridade.RARO,       70),
        new ItemLoja("Amuleto Paranormal",   "+7 Poder Paranormal permanentemente.",          TipoItem.BOOST_PARANORMAL, 7, Raridade.RARO,    85),
        new ItemLoja("Coração Cristalizado", "+20 Vida Máxima permanentemente.",              TipoItem.BOOST_VIDA_MAX, 20, Raridade.RARO,     75)
    );

    private static final List<ItemLoja> CATALOGO_MUITO_RARO = List.of(
        new ItemLoja("Grimório do Abismo",   "+15 Poder Paranormal permanentemente.",         TipoItem.BOOST_PARANORMAL, 15, Raridade.MUITO_RARO, 180),
        new ItemLoja("Tônica do Guerreiro",  "+12 Força permanentemente.",                   TipoItem.BOOST_FORCA,  12, Raridade.MUITO_RARO, 160),
        new ItemLoja("Essência Vital",       "+40 Vida Máxima permanentemente.",              TipoItem.BOOST_VIDA_MAX, 40, Raridade.MUITO_RARO, 200),
        new ItemLoja("Éter Puro",            "Restaura toda a PE.",                           TipoItem.RESTAURAR_PE, 999, Raridade.MUITO_RARO, 150),
        new ItemLoja("Cristal do Abismo",    "+12 Poder Paranormal permanentemente.",         TipoItem.BOOST_PARANORMAL, 12, Raridade.MUITO_RARO, 170),
        new ItemLoja("Relíquia Sagrada",     "+35 Vida Máxima permanentemente.",              TipoItem.BOOST_VIDA_MAX, 35, Raridade.MUITO_RARO, 190)
    );

    // ── Estado atual da loja ─────────────────────────────────────────
    private static List<ItemLoja> itensAtivos = null;

    /**
     * Retorna os 3 itens da loja.
     * Se ainda não foram gerados (ou foram resetados), gera agora.
     * 1 Comum + 1 Raro + 1 Muito Raro
     */
    public static List<ItemLoja> getItens() {
        if (itensAtivos == null) gerarItens();
        return itensAtivos;
    }

    private static void gerarItens() {
        itensAtivos = new ArrayList<>();

        List<ItemLoja> comuns      = new ArrayList<>(CATALOGO_COMUM);
        List<ItemLoja> raros       = new ArrayList<>(CATALOGO_RARO);
        List<ItemLoja> muitoRaros  = new ArrayList<>(CATALOGO_MUITO_RARO);

        Collections.shuffle(comuns,     random);
        Collections.shuffle(raros,      random);
        Collections.shuffle(muitoRaros, random);

        itensAtivos.add(comuns.get(0));
        itensAtivos.add(raros.get(0));
        itensAtivos.add(muitoRaros.get(0));
    }

    /** Chamado ao morrer/fugir — reseta a loja para o próximo ciclo. */
    public static void resetar() {
        itensAtivos = null;
    }

    /** Diálogos de entrada da Ivete (escolhido aleatoriamente). */
    public static String getDialogoEntrada() {
        String[] dialogos = {
            "Ivete: \"Ahh, você apareceu! Estava esperando alguém com cara de problema. Veja o que tenho, pode ser útil lá fora...\"",
            "Ivete: \"Entre, entre. A Ordem paga mal, mas pelo menos os clientes são interessantes. O que vai ser?\"",
            "Ivete: \"Sabia que você voltaria. Sempre voltam. Tenho exatamente o que você precisa — ou quase isso.\"",
            "Ivete: \"Não me pergunte de onde vêm essas coisas. Só sei que funcionam. Confie em mim... desta vez.\"",
            "Ivete: \"Você sobreviveu? Impressionante. Então talvez valha a pena investir em você. Dê uma olhada.\""
        };
        return dialogos[random.nextInt(dialogos.length)];
    }
}
