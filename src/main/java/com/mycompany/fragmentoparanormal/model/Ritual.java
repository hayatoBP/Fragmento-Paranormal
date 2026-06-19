package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;


public class Ritual {
    private String nome;
    private int custoPE;
    private int dano;
    private int cura;
    private Elemento elemento;
    private String descricao;

    public Ritual(String nome, int custoPE, int dano, int cura, Elemento elemento) {
        this(nome, custoPE, dano, cura, elemento, "");
    }

    public Ritual(String nome, int custoPE, int dano, int cura, Elemento elemento, String descricao) {
        this.nome = nome;
        this.custoPE = custoPE;
        this.dano = dano;
        this.cura = cura;
        this.elemento = elemento;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return (descricao == null || descricao.isEmpty()) ? "Um ritual de " + elemento + "." : descricao;
    }

    public String getNome() {
        return nome;
    }

    public int getCustoPE() {
        return custoPE;
    }

    public int getDano() {
        return dano;
    }

    public int getCura() {
        return cura;
    }

    public Elemento getElemento() {
        return elemento;
    }

    @Override
    public String toString() {
        return nome;
    }
}
