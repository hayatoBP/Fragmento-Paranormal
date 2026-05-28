
package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;


public class RitualService {
      public static boolean usarRitual(Personagem jogador, Inimigo inimigo) {

        Ritual ritual = jogador.getRitualEquipado();

        if(ritual == null) {
            return false;
        }

        if(jogador.getPontosEsforco() < ritual.getCustoPE()) {
            return false;
        }

        if(ritual.getCura() > 0 && !jogador.podeUsarCura()) {
            return false;
        }

        jogador.setPontosEsforco(
                jogador.getPontosEsforco() - ritual.getCustoPE()
        );

        if(ritual.getDano() > 0) {

            double multiplicador =
                    ElementoService.calcularMultiplicador(
                            ritual.getElemento(),
                            inimigo.getElemento()
                    );

            int danoFinal = (int) (
                    (ritual.getDano() + jogador.calcularDanoRitual())
                    * multiplicador
            );

            inimigo.setVida(
                    inimigo.getVida() - danoFinal
            );
        }

        if(ritual.getCura() > 0) {

            jogador.setVida(
                    jogador.getVida() + ritual.getCura()
            );
        }

        return true;
    }
}
