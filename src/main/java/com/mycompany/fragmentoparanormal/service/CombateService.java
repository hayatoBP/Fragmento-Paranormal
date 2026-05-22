
package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;


public class CombateService {
    public static void ataqueFisico(Personagem jogador, Inimigo inimigo) {

        int dano = jogador.calcularDanoFisico();

        inimigo.setVida(
                inimigo.getVida() - dano
        );
    }

    public static void ataqueInimigo(Personagem jogador, Inimigo inimigo) {

        jogador.setVida(
                jogador.getVida() - inimigo.getDano()
        );
    }
}
