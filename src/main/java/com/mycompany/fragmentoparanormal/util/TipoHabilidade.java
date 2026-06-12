package com.mycompany.fragmentoparanormal.util;

/**
 * Classifica a força de uma habilidade dentro de sua árvore.
 */
public enum TipoHabilidade {
    FRACA("Fraca"),
    MEDIA("Média"),
    FORTE("Forte");

    private final String nome;
    TipoHabilidade(String nome) { this.nome = nome; }

    @Override
    public String toString() { return nome; }
}
