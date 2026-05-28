package com.mycompany.fragmentoparanormal.util;


public enum ClassePersonagem {

    COMBATENTE("Combatente"),
    ESPECIALISTA("Especialista"),
    OCULTISTA("Ocultista");

    private String nome;

    ClassePersonagem(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}