package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import java.util.Random;

public class InvestigacaoService {

    private static final Random random = new Random();

    // Pool de armas que podem ser encontradas na investigação
    private static final Arma[] ARMAS_LOOT = {
        new Arma("Faca Ritualística",   10),
        new Arma("Pistola Antiga",      12),
        new Arma("Revólver .38",        14),
        new Arma("Machete Enferrujado", 16),
        new Arma("Espingarda Quebrada", 18),
        new Arma("Adaga Paranormal",    20),
        new Arma("Tocha Abençoada",      8),
    };

    public static String investigar(Personagem jogador) {

        int chance = jogador.getInvestigacao();
        int numero = random.nextInt(100);

        if (numero < chance) {
            return "FRAGMENTO";
        }

        if (numero < chance + 20) {
            // Sorteia uma arma aleatória do pool
            Arma armaEncontrada = ARMAS_LOOT[random.nextInt(ARMAS_LOOT.length)];
            // Só equipa se for melhor que a atual (ou se não tem nenhuma)
            if (jogador.getArmaEquipada() == null
                    || armaEncontrada.getBonusDano() > jogador.getArmaEquipada().getBonusDano()) {
                jogador.setArmaEquipada(armaEncontrada);
                return "ARMA_MELHOR:" + armaEncontrada.getNome();
            } else {
                // Encontrou uma arma, mas não é melhor — vai pro inventário como item
                jogador.getInventario().adicionarItem(
                    new Item(armaEncontrada.getNome(),
                             "Arma encontrada na investigação. Dano: +" + armaEncontrada.getBonusDano()));
                return "ARMA_FRACA:" + armaEncontrada.getNome();
            }
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