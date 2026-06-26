package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.GameState.PaginaDiario;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class DiarioController {

    @FXML private ListView<PaginaDiario> listaPaginas;
    @FXML private Label lblTotal;
    @FXML private Label lblTituloPagina;
    @FXML private Label lblTextoPagina;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        List<PaginaDiario> paginas = GameState.getPaginasEncontradas();
        listaPaginas.getItems().setAll(paginas);

        lblTotal.setText(paginas.size() + " / " + GameState.getTotalPaginasJogo() + " páginas encontradas");

        listaPaginas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(PaginaDiario p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(p.titulo());
                setStyle("-fx-text-fill: #f0d0d0; -fx-font-size: 12px; -fx-background-color: transparent;");
            }
        });

        listaPaginas.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel == null) return;
            lblTituloPagina.setText(sel.titulo());
            lblTextoPagina.setText(sel.texto());
        });

        if (!paginas.isEmpty()) {
            listaPaginas.getSelectionModel().selectFirst();
        } else {
            lblTituloPagina.setText("Nenhuma página encontrada ainda.");
            lblTextoPagina.setText("Continue investigando nas missões para descobrir as páginas do diário.");
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        String origem = GameState.getOrigemInventario();
        if ("MISSAO".equals(origem)) {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        }
    }
}
