package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TipoInimigo;

/**
 * Representa um inimigo ou boss do jogo.
 *
 * Escalonamento por missão (índice 0–3):
 *   Missão 1 (Sangue)       → base
 *   Missão 2 (Morte)        → +25% vida/dano
 *   Missão 3 (Energia)      → +55% vida/dano
 *   Missão 4 (Conhecimento) → +90% vida/dano
 *
 * Bosses de missão são significativamente mais fortes que inimigos fortes normais.
 * O Boss Final (MEDO) é muito superior a todos os demais bosses.
 */
public class Inimigo {

    // ── Tipos de boss ─────────────────────────────────────────────────
    public enum TipoBoss { NENHUM, MISSAO, FINAL }

    private String     nome;
    private Elemento   elemento;
    private TipoInimigo tipo;
    private TipoBoss   tipoBoss;
    private int        vida;
    private int        dano;
    private int        xpConcedido;
    private String     imagem;

    // ── Tabelas de XP ─────────────────────────────────────────────────
    /** XP para inimigos normais { fraco, forte } por missão (índice 0–3). */
    private static final int[][] XP_NORMAL = {
        {  90,  160 },   // Missão 1 — Sangue
        { 130,  240 },   // Missão 2 — Morte
        { 190,  340 },   // Missão 3 — Energia
        { 260,  480 },   // Missão 4 — Conhecimento
    };

    /** XP dos bosses de missão (índice 0–3). */
    private static final int[] XP_BOSS_MISSAO = { 600, 950, 1400, 2200 };

    /** XP do boss final. */
    private static final int XP_BOSS_FINAL = 5000;

    // ── Construtores ──────────────────────────────────────────────────

    /** Inimigo normal (fraco ou forte) com escalonamento por missão. */
    public Inimigo(Elemento elemento, TipoInimigo tipo, int indiceMissao) {
        this.elemento = elemento;
        this.tipo     = tipo;
        this.tipoBoss = TipoBoss.NENHUM;
        configurarNormal(indiceMissao);
        definirImagemNormal();
    }

    /** Construtor legado sem índice de missão — usa missão 1 como padrão. */
    public Inimigo(Elemento elemento, TipoInimigo tipo) {
        this(elemento, tipo, 0);
    }

    /** Construtor privado para bosses. */
    private Inimigo(TipoBoss tipoBoss, Elemento elemento, int indiceMissao) {
        this.elemento = elemento;
        this.tipo     = TipoInimigo.FORTE;
        this.tipoBoss = tipoBoss;
        if (tipoBoss == TipoBoss.MISSAO) {
            configurarBossMissao(indiceMissao);
            definirImagemBossMissao(elemento);
        } else {
            configurarBossFinal();
            this.imagem = "/com/mycompany/fragmentoparanormal/images/monstros/boss_medo.png";
        }
    }

    // ── Fábricas ──────────────────────────────────────────────────────

    /**
     * Cria o boss de uma missão específica.
     * @param elemento     elemento da missão (SANGUE, MORTE, ENERGIA ou CONHECIMENTO)
     * @param indiceMissao índice 0–3
     */
    public static Inimigo criarBossMissao(Elemento elemento, int indiceMissao) {
        return new Inimigo(TipoBoss.MISSAO, elemento, indiceMissao);
    }

    /** Cria o Boss Final (MEDO). */
    public static Inimigo criarBossFinal() {
        return new Inimigo(TipoBoss.FINAL, Elemento.MEDO, -1);
    }

    /** Compatibilidade com código legado — cria o Boss Final. */
    public static Inimigo criarBoss() {
        return criarBossFinal();
    }

    // ── Configuração interna ──────────────────────────────────────────

    private void configurarNormal(int idx) {
        int i = Math.max(0, Math.min(3, idx));
        // Multiplicadores de escalonamento por missão
        double[] multVida = { 1.0, 1.25, 1.55, 1.90 };
        double[] multDano = { 1.0, 1.20, 1.45, 1.75 };

        if (tipo == TipoInimigo.FRACO) {
            nome        = "Criatura Fraca de " + elemento;
            vida        = (int)(45  * multVida[i]);
            dano        = (int)(9   * multDano[i]);
            xpConcedido = XP_NORMAL[i][0];
        } else {
            nome        = "Criatura Forte de " + elemento;
            vida        = (int)(110 * multVida[i]);
            dano        = (int)(22  * multDano[i]);
            xpConcedido = XP_NORMAL[i][1];
        }
    }

    private void configurarBossMissao(int idx) {
        int i = Math.max(0, Math.min(3, idx));
        // Multiplicadores de escalonamento por missão (mais agressivos que inimigos normais)
        double[] multVida = { 1.0, 1.35, 1.70, 2.10 };
        double[] multDano = { 1.0, 1.30, 1.60, 2.00 };

        String[] nomesBoss = {
            "O Devorador de Sangue",
            "O Senhor da Morte",
            "O Condutor de Energia",
            "O Guardião do Conhecimento"
        };
        nome        = nomesBoss[i];
        vida        = (int)(280 * multVida[i]);
        dano        = (int)(35  * multDano[i]);
        xpConcedido = XP_BOSS_MISSAO[i];
    }

    private void configurarBossFinal() {
        nome        = "O Quarto Anfitrião — Medo Absoluto";
        vida        = 1200;
        dano        = 80;
        xpConcedido = XP_BOSS_FINAL;
    }

    private void definirImagemNormal() {
        imagem = switch (elemento) {
            case SANGUE       -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/sangue_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/sangue_forte.png";
            case MORTE        -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/morte_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/morte_forte.png";
            case ENERGIA      -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/energia_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/energia_forte.png";
            case CONHECIMENTO -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/conhecimento_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/conhecimento_forte.png";
            default -> "/com/mycompany/fragmentoparanormal/images/monstros/conhecimento_forte.png";
        };
    }

    private void definirImagemBossMissao(Elemento elem) {
        imagem = switch (elem) {
            case SANGUE       -> "/com/mycompany/fragmentoparanormal/images/monstros/boss_sangue.png";
            case MORTE        -> "/com/mycompany/fragmentoparanormal/images/monstros/boss_morte.png";
            case ENERGIA      -> "/com/mycompany/fragmentoparanormal/images/monstros/boss_energia.png";
            case CONHECIMENTO -> "/com/mycompany/fragmentoparanormal/images/monstros/boss_conhecimento.png";
            default           -> "/com/mycompany/fragmentoparanormal/images/monstros/boss_medo.png";
        };
    }

    // ── Consultas ─────────────────────────────────────────────────────

    public boolean estaVivo()      { return vida > 0; }
    public boolean isBoss()        { return tipoBoss != TipoBoss.NENHUM; }
    public boolean isBossMissao()  { return tipoBoss == TipoBoss.MISSAO; }
    public boolean isBossFinal()   { return tipoBoss == TipoBoss.FINAL; }
    public TipoBoss getTipoBoss()  { return tipoBoss; }

    // ── Getters / Setters ─────────────────────────────────────────────

    public String      getNome()           { return nome; }
    public Elemento    getElemento()       { return elemento; }
    public TipoInimigo getTipo()           { return tipo; }
    public int         getVida()           { return vida; }
    public void        setVida(int vida)   { this.vida = vida; }
    public int         getDano()           { return dano; }
    public int         getXpConcedido()    { return xpConcedido; }
    public String      getImagem()         { return imagem; }
}
