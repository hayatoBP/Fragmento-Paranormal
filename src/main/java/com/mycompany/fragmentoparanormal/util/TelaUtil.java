package com.mycompany.fragmentoparanormal.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TelaUtil {

    public static void trocarTela(ActionEvent event, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(
                TelaUtil.class.getResource(caminhoFXML)
            );
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao trocar tela: " + caminhoFXML);
            e.printStackTrace();
        }
    }

    public static void trocarTelaPorNode(Node node, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(
                TelaUtil.class.getResource(caminhoFXML)
            );
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao trocar tela: " + caminhoFXML);
            e.printStackTrace();
        }
    }
}
