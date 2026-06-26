package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.TipoItem;

/**
 * Representa um item disponível na loja da Ivete.
 * Cada item tem raridade, preço e pode ser comprado apenas uma vez por ciclo de loja.
 */
public class ItemLoja {

    public enum Raridade { COMUM, RARO, MUITO_RARO }

    private final String   nome;
    private final String   descricao;
    private final TipoItem tipo;
    private final int      efeito;
    private final Raridade raridade;
    private final int      preco;
    private boolean        comprado = false;

    public ItemLoja(String nome, String descricao, TipoItem tipo, int efeito,
                    Raridade raridade, int preco) {
        this.nome      = nome;
        this.descricao = descricao;
        this.tipo      = tipo;
        this.efeito    = efeito;
        this.raridade  = raridade;
        this.preco     = preco;
    }

    public String   getNome()       { return nome; }
    public String   getDescricao()  { return descricao; }
    public TipoItem getTipo()       { return tipo; }
    public int      getEfeito()     { return efeito; }
    public Raridade getRaridade()   { return raridade; }
    public int      getPreco()      { return preco; }
    public boolean  isComprado()    { return comprado; }
    public void     setComprado()   { this.comprado = true; }

    public String getCorRaridade() {
        return switch (raridade) {
            case COMUM      -> "#aaaaaa";
            case RARO       -> "#6c9eff";
            case MUITO_RARO -> "#d4a0ff";
        };
    }

    public String getLabelRaridade() {
        return switch (raridade) {
            case COMUM      -> "⬜ Comum";
            case RARO       -> "🔵 Raro";
            case MUITO_RARO -> "🟣 Muito Raro";
        };
    }

    /** Cria uma cópia nova deste item com comprado = false, para ciclos de loja frescos. */
    public ItemLoja copiar() {
        return new ItemLoja(nome, descricao, tipo, efeito, raridade, preco);
    }

    /** Converte para Item do inventário para ser guardado. */
    public Item toItem() {
        return new Item(nome, descricao, tipo, efeito);
    }

    @Override public String toString() { return nome; }
}
