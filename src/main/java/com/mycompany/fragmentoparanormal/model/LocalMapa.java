package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;
import java.util.ArrayList;
import java.util.List;

public class LocalMapa {
    private int ordem;
    private String nome;
    private String descricao;
    private String caminhoImagem;
    private boolean liberado;
    private boolean bossRoom;
    private Elemento elementoMissao;
    private List<Integer> paginasEncontradasNoLocal; // Nova lista para armazenar as páginas encontradas neste local

    public LocalMapa(int ordem, String nome, String descricao, String caminhoImagem, Elemento elementoMissao) {
        this.ordem = ordem;
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoImagem = caminhoImagem;
        this.elementoMissao = elementoMissao;
        this.liberado = (ordem == 0); // O primeiro local é sempre liberado
        this.bossRoom = false;
        this.paginasEncontradasNoLocal = new ArrayList<>();
    }

    // Construtor para sala do boss
    public LocalMapa(int ordem, String nome, String descricao, String caminhoImagem, Elemento elementoMissao, boolean bossRoom) {
        this.ordem = ordem;
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoImagem = caminhoImagem;
        this.elementoMissao = elementoMissao;
        this.liberado = false;
        this.bossRoom = bossRoom;
        this.paginasEncontradasNoLocal = new ArrayList<>();
    }

    // Getters
    public int getOrdem() { return ordem; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getCaminhoImagem() { return caminhoImagem; }
    public boolean isPaginaEncontrada() { return !paginasEncontradasNoLocal.isEmpty(); }
    public boolean isLiberado() { return liberado; }
    public boolean isBossRoom() { return bossRoom; }
    public Elemento getElementoMissao() { return elementoMissao; }
    public List<Integer> getPaginasEncontradasNoLocal() { return paginasEncontradasNoLocal; }

    // Setters
    public void setPaginaEncontrada(boolean paginaEncontrada) { /* Lógica de setar página encontrada agora é feita via adicionarPaginaEncontrada */ }
    public void setLiberado(boolean liberado) { this.liberado = liberado; }
    public void adicionarPaginaEncontrada(int pagina) {
        if (!paginasEncontradasNoLocal.contains(pagina)) {
            paginasEncontradasNoLocal.add(pagina);
        }
    }

    public void limparPaginasEncontradasNoLocal() {
        paginasEncontradasNoLocal.clear();
    }

    @Override
    public String toString() {
        return nome;
    }
}
