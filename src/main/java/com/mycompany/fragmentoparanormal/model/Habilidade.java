package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TipoArvore;
import com.mycompany.fragmentoparanormal.util.TipoHabilidade;

/**
 * Representa uma habilidade especial de Combatente ou Especialista.
 *
 * Para o Ocultista os rituais (Ritual.java) já cobrem o papel equivalente.
 *
 * Campos principais:
 *  - nome / descricao / efeito  → exibição na tela de escolha
 *  - custoPE                    → custo por uso em combate ou campo
 *  - multiplicadorDano          → % do dano da arma (ex: 1.75 = 175%)
 *  - reducaoDano                → % de redução do dano recebido (Defesa)
 *  - ehHabilidadeCampo          → true = só fora do combate (Especialista)
 *  - arvore / tipoHabilidade    → posição na árvore de progressão
 *  - elementoArvore             → preenchido apenas se arvore == ELEMENTAL
 *  - classeDonaOriginal         → classe que pode aprender esta habilidade
 */
public class Habilidade {

    private final String          nome;
    private final String          descricao;
    private final String          efeito;
    private final int             custoPE;
    private final double          multiplicadorDano;   // 0 se não aplica dano
    private final double          reducaoDano;         // 0.0–1.0 (só árvore Defesa)
    private final boolean         ehHabilidadeCampo;
    private final TipoArvore      arvore;
    private final TipoHabilidade  tipoHabilidade;
    private final Elemento        elementoArvore;      // null se não elemental
    private final ClassePersonagem classeDona;

    private Habilidade(Builder b) {
        this.nome              = b.nome;
        this.descricao         = b.descricao;
        this.efeito            = b.efeito;
        this.custoPE           = b.custoPE;
        this.multiplicadorDano = b.multiplicadorDano;
        this.reducaoDano       = b.reducaoDano;
        this.ehHabilidadeCampo = b.ehHabilidadeCampo;
        this.arvore            = b.arvore;
        this.tipoHabilidade    = b.tipoHabilidade;
        this.elementoArvore    = b.elementoArvore;
        this.classeDona        = b.classeDona;
    }

    // ── Getters ──────────────────────────────────────────────────────
    public String          getNome()              { return nome; }
    public String          getDescricao()         { return descricao; }
    public String          getEfeito()            { return efeito; }
    public int             getCustoPE()           { return custoPE; }
    public double          getMultiplicadorDano() { return multiplicadorDano; }
    public double          getReducaoDano()       { return reducaoDano; }
    public boolean         isHabilidadeCampo()    { return ehHabilidadeCampo; }
    public TipoArvore      getArvore()            { return arvore; }
    public TipoHabilidade  getTipoHabilidade()    { return tipoHabilidade; }
    public Elemento        getElementoArvore()    { return elementoArvore; }
    public ClassePersonagem getClasseDona()       { return classeDona; }

    /** True se esta habilidade for pré-requisito de outra (fraca ou média). */
    public boolean temPreRequisito() {
        return tipoHabilidade != TipoHabilidade.FRACA;
    }

    @Override
    public String toString() { return nome; }

    // ── Builder ──────────────────────────────────────────────────────
    public static class Builder {
        private String          nome         = "";
        private String          descricao    = "";
        private String          efeito       = "";
        private int             custoPE      = 0;
        private double          multiplicadorDano = 0;
        private double          reducaoDano  = 0;
        private boolean         ehHabilidadeCampo = false;
        private TipoArvore      arvore;
        private TipoHabilidade  tipoHabilidade;
        private Elemento        elementoArvore = null;
        private ClassePersonagem classeDona;

        public Builder nome(String v)              { nome = v;              return this; }
        public Builder descricao(String v)         { descricao = v;         return this; }
        public Builder efeito(String v)            { efeito = v;            return this; }
        public Builder custoPE(int v)              { custoPE = v;           return this; }
        public Builder multiplicador(double v)     { multiplicadorDano = v; return this; }
        public Builder reducaoDano(double v)       { reducaoDano = v;       return this; }
        public Builder campo()                     { ehHabilidadeCampo = true; return this; }
        public Builder arvore(TipoArvore v)        { arvore = v;            return this; }
        public Builder tipo(TipoHabilidade v)      { tipoHabilidade = v;    return this; }
        public Builder elemento(Elemento v)        { elementoArvore = v;    return this; }
        public Builder classe(ClassePersonagem v)  { classeDona = v;        return this; }
        public Habilidade build()                  { return new Habilidade(this); }
    }
}
