package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.TipoItem;

public class Item {

    private String   nome;
    private String   descricao;
    private TipoItem tipo;
    private int      efeito;   // valor do efeito (ex: +30 vida, +10 PE, +5 Poder)

    /** Construtor completo — para itens mágicos com efeito. */
    public Item(String nome, String descricao, TipoItem tipo, int efeito) {
        this.nome     = nome;
        this.descricao = descricao;
        this.tipo     = tipo;
        this.efeito   = efeito;
    }

    /** Construtor legado — item comum sem efeito ativo. */
    public Item(String nome, String descricao) {
        this(nome, descricao, TipoItem.COMUM, 0);
    }

    public String   getNome()      { return nome; }
    public String   getDescricao() { return descricao; }
    public TipoItem getTipo()      { return tipo; }
    public int      getEfeito()    { return efeito; }

    public boolean temEfeito() { return tipo != TipoItem.COMUM; }

    @Override
    public String toString() { return nome; }
}
