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

    /** Construtor para inimigos normais. */
    public Inimigo(Elemento elemento, TipoInimigo tipo) {
        this.elemento = elemento;
        this.tipo = tipo;
        this.isBoss = false;
        configurarInimigo();
        definirImagem();
    }

    /**
     * Construtor especial para o Boss Final.
     * Use: new Inimigo() com isBoss=true via factory.
     */
    private Inimigo(boolean boss) {
        this.isBoss = boss;
        this.elemento = Elemento.MEDO;
        this.tipo = TipoInimigo.FORTE;
        this.nome = "O Quarto Anfitrião";
        this.vida = 350;
        this.dano = 40;
        this.xpConcedido = 500;
        // Usa a imagem do monstro forte de conhecimento como placeholder para o boss
        this.imagem = "/com/mycompany/fragmentoparanormal/images/monstros/conhecimento_forte.png";
    }

    /** Factory: cria o Boss Final. */
    public static Inimigo criarBoss() {
        return new Inimigo(true);
    }

    private void configurarInimigo() {
        if (tipo == TipoInimigo.FRACO) {
            nome = "Criatura Fraca de " + elemento;
            vida = 50; dano = 10; xpConcedido = 30;
        } else {
            nome = "Criatura Forte de " + elemento;
            vida = 120; dano = 25; xpConcedido = 80;
        }
    }

    private void definirImagem() {
        imagem = switch (elemento) {
            case SANGUE      -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/sangue_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/sangue_forte.png";
            case MORTE       -> tipo == TipoInimigo.FRACO
                    ? "/com/mycompany/fragmentoparanormal/images/monstros/morte_fraco.png"
                    : "/com/mycompany/fragmentoparanormal/images/monstros/morte_forte.png";
            case ENERGIA     -> tipo == TipoInimigo.FRACO
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

    public String getNome()           { return nome; }
    public Elemento getElemento()     { return elemento; }
    public TipoInimigo getTipo()      { return tipo; }
    public int getVida()              { return vida; }
    public void setVida(int vida)     { this.vida = vida; }
    public int getDano()              { return dano; }
    public int getXpConcedido()       { return xpConcedido; }
    public String getImagem()         { return imagem; }
}
