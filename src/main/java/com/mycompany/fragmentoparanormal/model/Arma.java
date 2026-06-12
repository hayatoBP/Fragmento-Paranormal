package com.mycompany.fragmentoparanormal.model;

public class Arma {

    public enum Classificacao { FRACA, MEDIA, FORTE }

    private String         nome;
    private int            bonusDano;
    private String         descricao;
    private Classificacao  classificacao;

    public Arma(String nome, int bonusDano) {
        this.nome          = nome;
        this.bonusDano     = bonusDano;
        this.descricao     = gerarDescricao(nome, bonusDano);
        this.classificacao = classificarPorDano(bonusDano);
    }

    /** Compat: ignora terceiro parâmetro legado. */
    public Arma(String nome, int bonusDano, int ignorado) {
        this(nome, bonusDano);
    }

    /** Construtor completo com descrição personalizada. */
    public Arma(String nome, int bonusDano, String descricao) {
        this.nome          = nome;
        this.bonusDano     = bonusDano;
        this.descricao     = descricao;
        this.classificacao = classificarPorDano(bonusDano);
    }

    // ── Classificação automática por dano ────────────────────────────
    public static Classificacao classificarPorDano(int bonusDano) {
        if (bonusDano >= 18) return Classificacao.FORTE;
        if (bonusDano >= 10) return Classificacao.MEDIA;
        return Classificacao.FRACA;
    }

    /** Rótulo colorido para exibir na UI. */
    public String rotuloclassificacao() {
        return switch (classificacao) {
            case FORTE -> "★★★ Forte";
            case MEDIA -> "★★☆ Média";
            case FRACA -> "★☆☆ Fraca";
        };
    }

    /** Cor hex do rótulo. */
    public String corClassificacao() {
        return switch (classificacao) {
            case FORTE -> "#e74c3c";
            case MEDIA -> "#f39c12";
            case FRACA -> "#95a5a6";
        };
    }

    // ── Descrição automática quando não fornecida ─────────────────────
    private static String gerarDescricao(String nome, int bonusDano) {
        Classificacao c = classificarPorDano(bonusDano);
        return switch (c) {
            case FORTE -> "Uma arma perigosa e bem conservada. Causa dano considerável.";
            case MEDIA -> "Arma funcional encontrada no local. Razoavelmente eficaz.";
            case FRACA -> "Objeto improvisado ou deteriorado. Melhor do que nada.";
        };
    }

    // ── Getters ───────────────────────────────────────────────────────
    public String        getNome()           { return nome; }
    public int           getBonusDano()      { return bonusDano; }
    public String        getDescricao()      { return descricao; }
    public Classificacao getClassificacao()  { return classificacao; }

    @Override
    public String toString() { return nome; }
}
