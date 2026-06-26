package com.mycompany.fragmentoparanormal;

import com.mycompany.fragmentoparanormal.dao.DatabaseInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Inicializa o banco de dados na primeira execução
        DatabaseInit.inicializar();
        
        scene = new Scene(loadFXML("view/telaInicial"), 1024, 700);
        // Aplica CSS global
        try {
            var cssUrl = App.class.getResource("/com/mycompany/fragmentoparanormal/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
        } catch (Exception ignored) {}
        stage.setScene(scene);
        stage.setTitle("Fragmento Paranormal");
        stage.setMinWidth(1024);
        stage.setMinHeight(700);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/fragmentoparanormal/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
