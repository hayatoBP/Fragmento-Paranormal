
package com.mycompany.fragmentoparanormal.controller;


import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;


public class RankingController {

    @FXML
    private TableView<Personagem> tabelaRanking;

    @FXML
    private TableColumn<Personagem, String> colNome;

    @FXML
    private TableColumn<Personagem, Integer> colNivel;

    @FXML
    private TableColumn<Personagem, String> colClasse;

    @FXML
    private TableColumn<Personagem, String> colFase;

    @FXML
    public void initialize() {
        // Depois você pode ligar isso com ObservableList e PropertyValueFactory.
    }

    @FXML
    private void voltar(ActionEvent event) {

        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
