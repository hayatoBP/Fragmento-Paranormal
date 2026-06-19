
package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;


public class CombateService {
    public static void ataqueFisico(Personagem jogador, Inimigo inimigo) {
        int danoBase = jogador.calcularDanoFisico();
        double multElem = 1.0;

        // Se a arma estiver amaldiçoada, aplica vantagem elemental
        if (jogador.isArmaAmaldicoada()) {
            multElem = ElementoService.calcularMultiplicador(jogador.getElemento(), inimigo.getElemento());
        }

        int danoFinal = (int)(danoBase * multElem);
        inimigo.setVida(inimigo.getVida() - danoFinal);
    }

    public static void ataqueInimigo(Personagem jogador, Inimigo inimigo) {

        jogador.setVida(
                jogador.getVida() - inimigo.getDano()
        );
    }
}
