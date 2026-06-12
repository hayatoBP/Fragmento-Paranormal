package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.TipoItem;

/**
 * Artefato equipável — concede bônus passivos enquanto estiver equipado.
 * Máximo de 2 artefatos equipados simultaneamente.
 * Bônus possíveis: Força, Investigação, Poder Paranormal, Vida Máxima, PE Máximo.
 */
public class Artefato extends Item {

    private final int bonusForca;
    private final int bonusInvestigacao;
    private final int bonusPoderParanormal;
    private final int bonusVidaMaxima;
    private final int bonusPeMaximo;

    private Artefato(Builder b) {
        super(b.nome, b.descricao, TipoItem.ARTEFATO, 0);
        this.bonusForca            = b.bonusForca;
        this.bonusInvestigacao     = b.bonusInvestigacao;
        this.bonusPoderParanormal  = b.bonusPoderParanormal;
        this.bonusVidaMaxima       = b.bonusVidaMaxima;
        this.bonusPeMaximo         = b.bonusPeMaximo;
    }

    public int getBonusForca()           { return bonusForca; }
    public int getBonusInvestigacao()    { return bonusInvestigacao; }
    public int getBonusPoderParanormal() { return bonusPoderParanormal; }
    public int getBonusVidaMaxima()      { return bonusVidaMaxima; }
    public int getBonusPeMaximo()        { return bonusPeMaximo; }

    /** Gera descrição legível dos bônus para exibição na UI. */
    public String descricaoBonuses() {
        StringBuilder sb = new StringBuilder();
        if (bonusForca           > 0) sb.append("+").append(bonusForca).append(" Força\n");
        if (bonusInvestigacao    > 0) sb.append("+").append(bonusInvestigacao).append(" Investigação\n");
        if (bonusPoderParanormal > 0) sb.append("+").append(bonusPoderParanormal).append(" Poder Paranormal\n");
        if (bonusVidaMaxima      > 0) sb.append("+").append(bonusVidaMaxima).append(" Vida Máxima\n");
        if (bonusPeMaximo        > 0) sb.append("+").append(bonusPeMaximo).append(" PE Máximo\n");
        return sb.toString().strip();
    }

    @Override
    public String toString() { return getNome(); }

    // ── Builder ──────────────────────────────────────────────────────
    public static class Builder {
        private String nome, descricao = "";
        private int bonusForca, bonusInvestigacao, bonusPoderParanormal, bonusVidaMaxima, bonusPeMaximo;

        public Builder nome(String n)                { this.nome = n;                    return this; }
        public Builder descricao(String d)           { this.descricao = d;               return this; }
        public Builder forca(int v)                  { this.bonusForca = v;              return this; }
        public Builder investigacao(int v)           { this.bonusInvestigacao = v;       return this; }
        public Builder poderParanormal(int v)        { this.bonusPoderParanormal = v;    return this; }
        public Builder vidaMaxima(int v)             { this.bonusVidaMaxima = v;         return this; }
        public Builder peMaximo(int v)               { this.bonusPeMaximo = v;           return this; }
        public Artefato build()                      { return new Artefato(this); }
    }
}
