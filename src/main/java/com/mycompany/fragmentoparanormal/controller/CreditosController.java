
package com.mycompany.fragmentoparanormal.controller;


import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class CreditosController {

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
