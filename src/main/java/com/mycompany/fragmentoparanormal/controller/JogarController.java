package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class JogarController {

    @FXML
    public void initialize() {
        MusicaManager.tocarMenuInicial(); // Continua com a música do menu
    }

    @FXML
    private void abrirJogadorExistente(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogadorExistente.fxml");
    }

    @FXML
    private void abrirNovoJogador(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/novoJogador.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
