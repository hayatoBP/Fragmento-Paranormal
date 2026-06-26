
package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class TelaInicialController {

    @FXML
    public void initialize() {
        MusicaManager.tocarMenuInicial();
    }

    @FXML
    private void abrirJogar(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogar.fxml");
    }

    @FXML
    private void abrirRanking(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/ranking.fxml");
    }

    @FXML
    private void abrirCreditos(ActionEvent event) {
        SomUtil.tocarConfirmar();
        CreditosController.contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/creditos.fxml");
    }

    @FXML
    private void encerrar() {
        SomUtil.tocarVoltar();
        System.exit(0);
    }
}
