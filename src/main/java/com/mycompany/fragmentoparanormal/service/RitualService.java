package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;

public class RitualService {

    public static boolean usarRitual(Personagem jogador, Inimigo inimigo) {
        Ritual ritual = jogador.getRitualEquipado();
        if (ritual == null) return false;
        if (jogador.getPontosEsforco() < ritual.getCustoPE()) return false;
        if (ritual.getCura() > 0 && !jogador.podeUsarCura()) return false;

        jogador.setPontosEsforco(jogador.getPontosEsforco() - ritual.getCustoPE());

        if (ritual.getDano() > 0) {
            double mult = ElementoService.calcularMultiplicador(
                    ritual.getElemento(), inimigo.getElemento());
            // Dano = (base do ritual + poderParanormal do jogador) × mult elemental
            int danoFinal = (int)((ritual.getDano() + jogador.calcularDanoRitual()) * mult);
            inimigo.setVida(inimigo.getVida() - danoFinal);
        }

        if (ritual.getCura() > 0) {
            int novaVida = Math.min(jogador.getVida() + ritual.getCura(), jogador.getVidaMaxima());
            jogador.setVida(novaVida);
        }

        return true;
    }
}
