
package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public class JogarController {
     @FXML
    private void abrirJogadorExistente(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogadorExistente.fxml");
    }

    @FXML
    private void abrirNovoJogador(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/novoJogador.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
