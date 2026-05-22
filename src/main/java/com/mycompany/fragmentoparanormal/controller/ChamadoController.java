package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChamadoController {

    @FXML
    private Label lblMensagem;

    @FXML
    public void initialize() {
        if (GameContext.jogadorAtual != null) {
            lblMensagem.setText(
                "Agente " + GameContext.jogadorAtual.getNome()
                + ", a Ordem te chamou para uma missão."
            );
        }
    }

    @FXML
    private void aceitar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    @FXML
    private void recusar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
