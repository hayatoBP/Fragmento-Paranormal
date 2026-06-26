package com.mycompany.fragmentoparanormal.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.mycompany.fragmentoparanormal.controller.GameContext;

public class TelaUtil {

    // Tamanho padrão da janela — deve coincidir com o prefWidth/prefHeight dos FXMLs
    private static final double LARGURA = 1024;
    private static final double ALTURA  = 700;

    public static void trocarTela(ActionEvent event, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(
                TelaUtil.class.getResource(caminhoFXML)
            );
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            GameContext.setMainStage(stage);
            aplicarCena(stage, root);
        } catch (Exception e) {
            System.err.println("[TelaUtil] Erro ao trocar tela: " + caminhoFXML);
            e.printStackTrace();
        }
    }

    public static void trocarTelaPorNode(Node node, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(
                TelaUtil.class.getResource(caminhoFXML)
            );
            Stage stage = (Stage) node.getScene().getWindow();
            GameContext.setMainStage(stage);
            aplicarCena(stage, root);
        } catch (Exception e) {
            System.err.println("[TelaUtil] Erro ao trocar tela: " + caminhoFXML);
            e.printStackTrace();
        }
    }

    private static void aplicarCena(Stage stage, Parent root) {
        Scene scene = new Scene(root, LARGURA, ALTURA);
        try {
            var cssUrl = TelaUtil.class.getResource(
                "/com/mycompany/fragmentoparanormal/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("[TelaUtil] CSS não encontrado.");
        }
        stage.setScene(scene);
        stage.setMinWidth(LARGURA);
        stage.setMinHeight(ALTURA);
        stage.show();
    }
}
