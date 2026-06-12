
package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class TelaInicialController {
    @FXML
    private void abrirJogar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogar.fxml");
    }

    @FXML
    private void abrirRanking(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/ranking.fxml");
    }

    @FXML
    private void abrirCreditos(ActionEvent event) {
        CreditosController.contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/creditos.fxml");
    }

    @FXML
    private void encerrar() {
        System.exit(0);
    }
}
