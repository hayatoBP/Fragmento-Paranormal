package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.Elemento;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Missao {

    private String nome;
    private Elemento elemento;
    private String imagemCenario;
    private String arquivoDialogo;
    private int fragmentosNecessarios;
    private int ordem;
    private boolean concluida;
    private List<LocalMapa> locais;
    private int localAtual; // Índice do local atual na lista 'locais'

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
        this.locais = new ArrayList<>();
        this.localAtual = 0; // Começa no primeiro local
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

    public List<LocalMapa> getLocais() { return locais; }
    public void setLocais(List<LocalMapa> locais) { this.locais = locais; }

    public int getLocalAtual() { return localAtual; }
    public void setLocalAtual(int localAtual) { this.localAtual = localAtual; }

    public LocalMapa getLocalAtualObj() {
        if (localAtual >= 0 && localAtual < locais.size()) {
            return locais.get(localAtual);
        }
        return null;
    }

    public LocalMapa getProximoLocalLiberado() {
        for (int i = 0; i < locais.size(); i++) {
            if (locais.get(i).isLiberado() && !locais.get(i).isPaginaEncontrada()) {
                return locais.get(i);
            }
        }
        return null;
    }

    public String getPaginasLocaisEncontradasAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locais.size(); i++) {
            sb.append(locais.get(i).isPaginaEncontrada() ? "1" : "0");
            if (i < locais.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public void setPaginasLocaisEncontradasFromString(String paginasStr) {
        String[] paginasArray = paginasStr.split(",");
        for (int i = 0; i < paginasArray.length && i < locais.size(); i++) {
            locais.get(i).setPaginaEncontrada(paginasArray[i].equals("1"));
        }
    }


    @Override
    public String toString() {
        return nome;
    }
}
