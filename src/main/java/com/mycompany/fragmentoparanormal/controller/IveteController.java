package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.service.LojaService;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class IveteController {

    @FXML private Label lblDialogo;

    @FXML
    public void initialize() {
        MusicaManager.tocarIvete();
        lblDialogo.setText(LojaService.getDialogoEntrada());
    }

    @FXML
    private void entrarNaLoja(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/loja.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }
}
