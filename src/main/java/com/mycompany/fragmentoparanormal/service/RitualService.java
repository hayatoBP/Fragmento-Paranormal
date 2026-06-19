package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;

public class RitualService {

    public static boolean usarRitual(Personagem jogador, Inimigo inimigo) {
        Ritual ritual = jogador.getRitualEquipado();
        if (ritual == null) return false;

        // Calcula custo com bônus de afinidade
        int custoReal = jogador.getCustoPEComAfinidade(ritual.getCustoPE(), ritual.getElemento());
        if (jogador.getPontosEsforco() < custoReal) return false;
        if (ritual.getCura() > 0 && !jogador.podeUsarCura()) return false;

        jogador.setPontosEsforco(jogador.getPontosEsforco() - custoReal);

        if (ritual.getDano() > 0) {
            // Busca o multiplicador de PP do catálogo para este ritual
            var entrada = CatalogoRituaisService.getTodos().stream()
                .filter(e -> e.ritual().getNome().equals(ritual.getNome()))
                .findFirst().orElse(null);
            
            double multPP = (entrada != null) ? entrada.multiplicadorPP() : 1.0;
            double afinidade = jogador.getBonusAfinidade(ritual.getElemento());
            double multElem = ElementoService.calcularMultiplicador(ritual.getElemento(), inimigo.getElemento());

            // Dano = (base + PP * multPP) * afinidade * multElem
            int danoFinal = (int)((ritual.getDano() + jogador.getPoderParanormal() * multPP) * afinidade * multElem);
            
            inimigo.setVida(inimigo.getVida() - danoFinal);
        }

        if (ritual.getCura() > 0) {
            int novaVida = Math.min(jogador.getVida() + ritual.getCura(), jogador.getVidaMaxima());
            jogador.setVida(novaVida);
        }

        return true;
    }
}
