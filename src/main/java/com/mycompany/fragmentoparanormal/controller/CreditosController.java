package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller da tela de créditos.
 * Pode ser acessado a partir do menu principal (contexto = "MENU")
 * ou quando o jogador recusa o chamado (contexto = "RECUSA").
 */
public class CreditosController {

    /** Contexto de onde a tela de créditos foi aberta. */
    public static String contexto = "MENU";

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblCreditos;

    @FXML
    public void initialize() {
        if ("RECUSA".equals(contexto)) {
            if (lblTitulo    != null) lblTitulo.setText("Fim de Jogo");
            if (lblSubtitulo != null) lblSubtitulo.setText(
                "Você recusou o chamado da Ordem.\n" +
                "O paranormal avança sem resistência...\n\n" +
                "Talvez na próxima vez você encontre coragem.");
        } else {
            if (lblTitulo    != null) lblTitulo.setText("Fragmento Paranormal");
            if (lblSubtitulo != null) lblSubtitulo.setText("Obrigado por jogar!");
        }

        if (lblCreditos != null) {
            lblCreditos.setText(
                "Desenvolvimento: Equipe Fragmento Paranormal\n\n" +
                "Design & Narrativa: Equipe Fragmento Paranormal\n\n" +
                "Tecnologias: Java 21 · JavaFX · SQLite\n\n" +
                "© 2024 — Todos os direitos reservados"
            );
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
