package com.mycompany.fragmentoparanormal;

import com.mycompany.fragmentoparanormal.dao.DatabaseInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Fragmento Paranormal");
        primaryStage.setResizable(false);

        // Inicializa o schema do banco de dados (cria tabelas se necessário)
        DatabaseInit.inicializar();

        Parent root = FXMLLoader.load(
            getClass().getResource("/com/mycompany/fragmentoparanormal/view/telaInicial.fxml")
        );
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}