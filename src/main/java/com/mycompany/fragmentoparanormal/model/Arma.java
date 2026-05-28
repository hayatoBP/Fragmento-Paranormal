package com.mycompany.fragmentoparanormal.model;


public class Arma {
     private String nome;
    private int bonusDano;

    public Arma(String nome, int bonusDano) {
        this.nome = nome;
        this.bonusDano = bonusDano;
    }

    public String getNome() {
        return nome;
    }

    public int getBonusDano() {
        return bonusDano;
    }

    @Override
    public String toString() {
        return nome;
    }
}
