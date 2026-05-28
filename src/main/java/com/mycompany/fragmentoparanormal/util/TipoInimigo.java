package com.mycompany.fragmentoparanormal.util;


public enum TipoInimigo {

    FRACO("Fraco"),
    FORTE("Forte");

    private String nome;

    TipoInimigo(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
