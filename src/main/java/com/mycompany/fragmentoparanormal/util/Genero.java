package com.mycompany.fragmentoparanormal.util;


public enum Genero {

    HOMEM("Homem"),
    MULHER("Mulher");

    private String nome;

    Genero(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
