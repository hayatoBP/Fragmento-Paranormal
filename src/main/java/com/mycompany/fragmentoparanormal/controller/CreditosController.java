package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CreditosController {

    public static String contexto = "MENU";

    @FXML private Label lblTituloJogo;
    @FXML private Label lblMensagemContexto;

    @FXML
    public void initialize() {
        MusicaManager.tocarCreditos();
        if ("RECUSA".equals(contexto)) {
            if (lblTituloJogo != null) lblTituloJogo.setText("Fim de Jogo");
            if (lblMensagemContexto != null) lblMensagemContexto.setText(
                "Você recusou o chamado da Ordem.\n" +
                "O paranormal avança sem resistência...\n\n" +
                "Talvez na próxima vez você encontre coragem.");
        } else {
            if (lblTituloJogo != null) lblTituloJogo.setText("Fragmento Paranormal");
            if (lblMensagemContexto != null) lblMensagemContexto.setText("Obrigado por jogar!");
        }
    }

    @FXML
    private void recomecar(ActionEvent event) {
        SomUtil.tocarConfirmar();
        contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        SomUtil.tocarVoltar();
        contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    @FXML
    private void encerrar() {
        SomUtil.tocarVoltar();
        System.exit(0);
    }
}
