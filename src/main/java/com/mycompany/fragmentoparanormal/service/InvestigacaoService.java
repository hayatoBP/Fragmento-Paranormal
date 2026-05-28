package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import java.util.Random;

public class InvestigacaoService {

    private static final Random random = new Random();

    // Pool de armas que podem ser encontradas na investigação
    public static final Arma[] ARMAS_LOOT = {
        new Arma("Faca Ritualística",    10),
        new Arma("Pistola Antiga",       12),
        new Arma("Revólver .38",         14),
        new Arma("Machete Enferrujado",  16),
        new Arma("Espingarda Quebrada",  18),
        new Arma("Adaga Paranormal",     20),
        new Arma("Tocha Abençoada",       8),
        new Arma("Foice Enferrujada",    15),
        new Arma("Faca de Caça",         11),
        new Arma("Rifle Quebrado",       22),
    };

    /**
     * Realiza a investigação e retorna o resultado como String:
     *  - "FRAGMENTO"          → página encontrada
     *  - "ARMA:<nome>"        → arma encontrada; MissaoController abre o diálogo de comparação
     *  - "ITEM_PARANORMAL"    → amuleto adicionado ao inventário
     *  - "NADA"               → nada encontrado
     *
     * Nenhuma arma é equipada automaticamente aqui — a decisão fica com o jogador.
     */
    public static String investigar(Personagem jogador) {
        int chance = jogador.getInvestigacao();
        int numero = random.nextInt(100);

        if (numero < chance) {
            return "FRAGMENTO";
        }

        if (numero < chance + 20) {
            // Sorteia arma — NÃO equipa, apenas informa ao controller
            Arma armaEncontrada = ARMAS_LOOT[random.nextInt(ARMAS_LOOT.length)];
            return "ARMA:" + armaEncontrada.getNome() + ":" + armaEncontrada.getBonusDano();
        }

        if (numero < chance + 35) {
            jogador.getInventario().adicionarItem(
                new Item("Amuleto Paranormal",
                         "Um item que aumenta sua conexão com o outro lado."));
            jogador.setPoderParanormal(jogador.getPoderParanormal() + 5);
            return "ITEM_PARANORMAL";
        }

        return "NADA";
    }
}
