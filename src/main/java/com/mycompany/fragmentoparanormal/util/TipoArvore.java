package com.mycompany.fragmentoparanormal.util;

/**
 * Identifica a árvore de habilidades à qual uma habilidade pertence.
 * Usada por Combatente e Especialista.
 * Ocultista usa árvores elementais diretamente (via Elemento).
 */
public enum TipoArvore {
    // Combatente
    ATAQUE,
    DEFESA,
    MOBILIDADE,
    // Especialista
    PRECISAO,
    INVESTIGACAO,
    PREPARACAO,
    // Compartilhada (elemental — cada classe tem sua variante)
    ELEMENTAL
}
