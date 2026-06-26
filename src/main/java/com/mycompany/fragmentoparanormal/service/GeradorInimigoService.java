package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TipoInimigo;

import java.util.Random;

/**
 * Responsável por gerar inimigos (normais, bosses de missão e boss final).
 *
 * Lógica de geração:
 *  1. Se a campanha foi concluída e o boss final foi desbloqueado → Boss Final (MEDO).
 *  2. Se a missão atual está concluída (todas as páginas coletadas) e o boss da missão
 *     ainda não foi derrotado → Boss de Missão.
 *  3. Caso contrário → inimigo normal (fraco ou forte) com escalonamento por missão.
 */
public class GeradorInimigoService {

    private static final Random random = new Random();

    public static Inimigo gerarInimigo(Personagem jogador) {
        // Bosses são acionados diretamente pelo MissaoController e MenuMissoesController.
        // Aqui geramos apenas inimigos normais para o combate de cada local.

        Missao missaoAtual = GameState.getMissaoAtual();
        int indiceMissao   = getIndiceMissaoAtual(missaoAtual);

        Elemento    elemento = sortearElemento(missaoAtual);
        TipoInimigo tipo     = sortearTipo(jogador.getNivel());
        return new Inimigo(elemento, tipo, indiceMissao);
    }

    private static int getIndiceMissaoAtual(Missao missao) {
        if (missao == null) return 0;
        return MissaoService.getIndiceMissao(missao.getElemento());
    }

    private static Elemento sortearElemento(Missao missaoAtual) {
        // Inimigos comuns NUNCA possuem o elemento MEDO.
        // Se a missão atual tiver um elemento específico, há 70% de chance de segui-lo.
        if (missaoAtual != null
                && missaoAtual.getElemento() != null
                && missaoAtual.getElemento() != Elemento.MEDO) {
            if (random.nextInt(100) < 70) return missaoAtual.getElemento();
        }
        // Sorteia entre os elementos válidos, EXCLUINDO Medo
        Elemento[] validos = java.util.Arrays.stream(Elemento.values())
                .filter(e -> e != Elemento.MEDO)
                .toArray(Elemento[]::new);
        return validos[random.nextInt(validos.length)];
    }

    /**
     * Probabilidade de inimigo FORTE por faixa de nível:
     *   nível 1–3  → 0%
     *   nível 4–6  → 20%
     *   nível 7–10 → 40%
     *   nível 11+  → 60%
     */
    private static TipoInimigo sortearTipo(int nivel) {
        int chanceForte = nivel <= 3 ? 0
                        : nivel <= 6 ? 20
                        : nivel <= 10 ? 40
                        : 60;
        return random.nextInt(100) < chanceForte ? TipoInimigo.FORTE : TipoInimigo.FRACO;
    }
}
