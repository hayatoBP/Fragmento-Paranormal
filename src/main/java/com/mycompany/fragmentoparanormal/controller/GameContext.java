package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.CampanhaDAO;
import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GameContext {
    public static Personagem jogadorAtual;
    public static Inimigo inimigoAtual;

    public static void salvarProgressoCampanha() {
        if (jogadorAtual != null && GameState.getMissaoAtual() != null) {
            Missao missao = GameState.getMissaoAtual();
            CampanhaDAO.salvarCampanha(
                jogadorAtual.getId(),
                missao.getElemento(),
                missao.getNome(),
                (int) missao.getLocais().stream().filter(l -> l.isPaginaEncontrada()).count(), // paginasColetadas
                missao.isConcluida(),
                GameState.isBossDesbloqueado(),
                missao.getLocalAtual(),
                missao.getPaginasLocaisEncontradasAsString()
            );
        }
    }

    private static Stage mainStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static void loadScene(String fxml) throws IOException {
        if (mainStage == null) {
            System.err.println("MainStage não configurado no GameContext.");
            return;
        }
        Parent root = FXMLLoader.load(Objects.requireNonNull(GameContext.class.getResource("/com/mycompany/fragmentoparanormal/view/" + fxml + ".fxml")));
        mainStage.setScene(new Scene(root));
        mainStage.show();
    }

    public static void loadScene(String fxml, Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(GameContext.class.getResource("/com/mycompany/fragmentoparanormal/view/" + fxml + ".fxml")));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void loadScene(String fxml, Node node) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(GameContext.class.getResource("/com/mycompany/fragmentoparanormal/view/" + fxml + ".fxml")));
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
