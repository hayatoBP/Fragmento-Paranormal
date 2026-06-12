package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.util.Elemento;

public class ElementoService {

    /**
     * Retorna o multiplicador de dano elemental.
     *
     * MEDO (Boss Final) é super efetivo contra QUALQUER elemento — sempre 2x.
     * Nenhum elemento é super efetivo contra MEDO (imune a vantagem).
     *
     * Cadeia normal:
     *   SANGUE > CONHECIMENTO > ENERGIA > MORTE > SANGUE
     *   vantagem = 1.5x | desvantagem = 0.5x | neutro = 1.0x
     */
    public static double calcularMultiplicador(Elemento ataque, Elemento defesa) {

        // Boss Final — MEDO esmaga tudo
        if (ataque == Elemento.MEDO) return 2.0;

        // Ninguém tem vantagem sobre MEDO
        if (defesa == Elemento.MEDO) return 1.0;

        // Vantagens normais
        if (
            (ataque == Elemento.SANGUE      && defesa == Elemento.CONHECIMENTO)
            || (ataque == Elemento.MORTE    && defesa == Elemento.SANGUE)
            || (ataque == Elemento.ENERGIA  && defesa == Elemento.MORTE)
            || (ataque == Elemento.CONHECIMENTO && defesa == Elemento.ENERGIA)
        ) {
            return 1.5;
        }

        // Desvantagens normais
        if (
            (ataque == Elemento.SANGUE      && defesa == Elemento.MORTE)
            || (ataque == Elemento.MORTE    && defesa == Elemento.ENERGIA)
            || (ataque == Elemento.ENERGIA  && defesa == Elemento.CONHECIMENTO)
            || (ataque == Elemento.CONHECIMENTO && defesa == Elemento.SANGUE)
        ) {
            return 0.5;
        }

        return 1.0;
    }
}
