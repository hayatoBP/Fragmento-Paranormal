package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TipoInimigo;

public class Inimigo {

    private String nome;
    private Elemento elemento;
    private TipoInimigo tipo;
    private boolean isBoss;

    private int vida;
    private int dano;
    private int xpConcedido;
    private String imagem;

    /** XP por missão conforme especificação. */
    private static final int[][] XP_POR_MISSAO = {
        // { fraco, forte } — índice 0=Sangue, 1=Morte, 2=Energia, 3=Conhecimento
        { 25,  50 },   // Missão 1 — Sangue
        { 40,  75 },   // Missão 2 — Morte
        { 60, 110 },   // Missão 3 — Energia
        { 90, 160 },   // Missão 4 — Conhecimento
    };

    private static final int[] XP_BOSS_POR_MISSAO = { 150, 250, 400, 600 };

    /** Construtor para inimigos normais — recebe índice da missão (0–3). */
    public Inimigo(Elemento elemento, TipoInimigo tipo, int indiceMissao) {
        this.elemento = elemento;
        this.tipo     = tipo;
        this.isBoss   = false;
        configurarInimigo(indiceMissao);
        definirImagem();
    }

    /** Construtor legado sem índice de missão — usa missão 1 como padrão. */
    public Inimigo(Elemento elemento, TipoInimigo tipo) {
        this(elemento, tipo, 0);
    }

    private Inimigo(boolean boss, int indiceMissao) {
        this.isBoss   = boss;
        this.elemento = Elemento.MEDO;
        this.tipo     = TipoInimigo.FORTE;
        this.nome     = "O Quarto Anfitrião";
        this.vida     = 500;
        this.dano     = 50;
        this.xpConcedido = 1500;
        this.imagem   = "/com/mycompany/fragmentoparanormal/images/monstros/conhecimento_forte.png";
    }

    public static Inimigo criarBoss() { return new Inimigo(true, 0); }

    private void configurarInimigo(int idx) {
        int i = Math.max(0, Math.min(3, idx));

        if (tipo == TipoInimigo.FRACO) {
            nome = "Criatura Fraca de " + elemento;
            // Vida e dano escalam levemente por missão
            vida         = 40 + i * 20;
            dano         = 8  + i * 6;
            xpConcedido  = XP_POR_MISSAO[i][0];
        } else {
            nome = "Criatura Forte de " + elemento;
            vida         = 100 + i * 40;
            dano         = 20  + i * 10;
            xpConcedido  = XP_POR_MISSAO[i][1];
        }
    }

    private void definirImagem() {
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

    public boolean estaVivo() { return vida > 0; }
    public boolean isBoss()   { return isBoss; }

    public String     getNome()           { return nome; }
    public Elemento   getElemento()       { return elemento; }
    public TipoInimigo getTipo()          { return tipo; }
    public int        getVida()           { return vida; }
    public void       setVida(int vida)   { this.vida = vida; }
    public int        getDano()           { return dano; }
    public int        getXpConcedido()    { return xpConcedido; }
    public String     getImagem()         { return imagem; }
}
