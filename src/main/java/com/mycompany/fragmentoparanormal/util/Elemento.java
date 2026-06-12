package com.mycompany.fragmentoparanormal.util;

public enum Elemento {

    SANGUE("Sangue"),
    MORTE("Morte"),
    ENERGIA("Energia"),
    CONHECIMENTO("Conhecimento"),
    MEDO("Medo");          // elemento exclusivo do Boss Final — super efetivo contra tudo

    private final String nome;

    Elemento(String nome) { this.nome = nome; }

    @Override
    public String toString() { return nome; }
}
