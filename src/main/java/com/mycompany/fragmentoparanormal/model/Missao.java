package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;



public class Missao {

    private String nome;
    private Elemento elemento;
    private String imagemCenario;
    private String arquivoDialogo;
    private int fragmentosNecessarios;
    private int ordem;
    private boolean concluida;

    public Missao(String nome,
                  Elemento elemento,
                  String imagemCenario,
                  String arquivoDialogo,
                  int fragmentosNecessarios,
                  int ordem) {

        this.nome = nome;
        this.elemento = elemento;
        this.imagemCenario = imagemCenario;
        this.arquivoDialogo = arquivoDialogo;
        this.fragmentosNecessarios = fragmentosNecessarios;
        this.ordem = ordem;
        this.concluida = false;
    }

    public String getNome() {
        return nome;
    }

    public Elemento getElemento() {
        return elemento;
    }

    public String getImagemCenario() {
        return imagemCenario;
    }

    public String getArquivoDialogo() {
        return arquivoDialogo;
    }

    public int getFragmentosNecessarios() {
        return fragmentosNecessarios;
    }

    public int getOrdem() {
        return ordem;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void concluir() {
        concluida = true;
    }

    @Override
    public String toString() {
        return nome;
    }
}
