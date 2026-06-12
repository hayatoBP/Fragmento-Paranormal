package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.util.Elemento;
import java.util.ArrayList;

public class MissaoService {

    // Cada missão exige 7 páginas — total de 28 no jogo
    private static final ArrayList<Missao> missoes = new ArrayList<>();

    static {
        missoes.add(new Missao(
            "Páginas de Sangue", Elemento.SANGUE,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_sangue.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_sangue.txt",
            7, 1
        ));
        missoes.add(new Missao(
            "Ecos da Morte", Elemento.MORTE,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_morte.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_morte.txt",
            7, 2
        ));
        missoes.add(new Missao(
            "Ruído da Energia", Elemento.ENERGIA,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_energia.txt",
            7, 3
        ));
        missoes.add(new Missao(
            "Segredos do Conhecimento", Elemento.CONHECIMENTO,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_conhecimento.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_conhecimento.txt",
            7, 4
        ));
    }

    public static ArrayList<Missao> getMissoes() { return missoes; }

    public static Missao getProximaMissao() {
        for (Missao m : missoes) {
            if (!m.isConcluida()) return m;
        }
        return null;
    }

    public static int getIndiceMissao(Elemento elemento) {
        return switch (elemento) {
            case SANGUE       -> 0;
            case MORTE        -> 1;
            case ENERGIA      -> 2;
            case CONHECIMENTO -> 3;
            default           -> -1;
        };
    }

    public static boolean campanhaConcluida() {
        for (Missao m : missoes) {
            if (!m.isConcluida()) return false;
        }
        return true;
    }
}
