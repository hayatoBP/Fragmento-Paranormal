package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TipoInimigo;
import java.util.Random;

public class GeradorInimigoService {

    private static final Random random = new Random();

    public static Inimigo gerarInimigo(Personagem jogador) {

        Elemento elemento = sortearElemento();
        TipoInimigo tipo = sortearTipo(jogador.getNivel());

        return new Inimigo(elemento, tipo);
    }

    /**
     * Sorteia o elemento do inimigo.
     * Se a missão atual tiver um elemento definido,
     * 70% de chance de gerar inimigo daquele elemento,
     * 30% de chance de ser qualquer outro elemento aleatório.
     */
    private static Elemento sortearElemento() {
        Missao missaoAtual = GameState.getMissaoAtual();

        if (missaoAtual != null && missaoAtual.getElemento() != null) {
            // 70% chance de ser o elemento da missão
            if (random.nextInt(100) < 70) {
                return missaoAtual.getElemento();
            }
        }

        // 30% (ou missão sem elemento): sorteia aleatório
        return Elemento.values()[random.nextInt(Elemento.values().length)];
    }

    /**
     * Sorteia o tipo (FRACO/FORTE) baseado no nível do jogador.
     * nível 1–3  → 100% FRACO
     * nível 4–6  → 20% FORTE
     * nível 7–10 → 40% FORTE
     * nível 10+  → 60% FORTE
     */
    private static TipoInimigo sortearTipo(int nivel) {
        int chanceForte;

        if (nivel <= 3) {
            chanceForte = 0;
        } else if (nivel <= 6) {
            chanceForte = 20;
        } else if (nivel <= 10) {
            chanceForte = 40;
        } else {
            chanceForte = 60;
        }

        return random.nextInt(100) < chanceForte
                ? TipoInimigo.FORTE
                : TipoInimigo.FRACO;
    }
}
