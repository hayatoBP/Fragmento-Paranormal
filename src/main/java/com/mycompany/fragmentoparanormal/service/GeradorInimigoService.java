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
        Elemento    elemento     = sortearElemento();
        TipoInimigo tipo         = sortearTipo(jogador.getNivel());
        int         indiceMissao = getIndiceMissaoAtual();
        return new Inimigo(elemento, tipo, indiceMissao);
    }

    private static int getIndiceMissaoAtual() {
        Missao m = GameState.getMissaoAtual();
        if (m == null) return 0;
        return MissaoService.getIndiceMissao(m.getElemento());
    }

    private static Elemento sortearElemento() {
        Missao missaoAtual = GameState.getMissaoAtual();
        if (missaoAtual != null && missaoAtual.getElemento() != null) {
            if (random.nextInt(100) < 70) return missaoAtual.getElemento();
        }
        Elemento[] validos = java.util.Arrays.stream(Elemento.values())
                .filter(e -> e != Elemento.MEDO)
                .toArray(Elemento[]::new);
        return validos[random.nextInt(validos.length)];
    }

    /**
     * nível 1–3  → 100% FRACO
     * nível 4–6  → 20% FORTE
     * nível 7–10 → 40% FORTE
     * nível 11+  → 60% FORTE
     */
    private static TipoInimigo sortearTipo(int nivel) {
        int chanceForte = nivel <= 3 ? 0
                        : nivel <= 6 ? 20
                        : nivel <= 10 ? 40
                        : 60;
        return random.nextInt(100) < chanceForte ? TipoInimigo.FORTE : TipoInimigo.FRACO;
    }
}
