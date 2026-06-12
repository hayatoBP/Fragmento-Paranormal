package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RituaisController {

    private Personagem jogador;

    @FXML private ListView<Ritual> listaRituais;
    @FXML private ImageView imgElemento;
    @FXML private Label lblNomeElemento;
    @FXML private Label lblNomeRitual;
    @FXML private Label lblElemento;
    @FXML private Label lblCustoPE;
    @FXML private Label lblDanoBase;
    @FXML private Label lblDanoTotal;
    @FXML private Label lblEficacia;
    @FXML private Label lblForteContra;
    @FXML private Label lblDanoForte;
    @FXML private Label lblFracoContra;
    @FXML private Label lblDanoFraco;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        if (jogador == null) return;

        listaRituais.getItems().setAll(jogador.getRituais());

        // Célula com ícone do elemento
        listaRituais.setCellFactory(lv -> new ListCell<>() {
            private final ImageView ico = new ImageView();
            private final javafx.scene.control.Label lbl = new javafx.scene.control.Label();
            private final javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(8, ico, lbl);
            {
                ico.setFitWidth(20); ico.setFitHeight(20); ico.setPreserveRatio(true);
                lbl.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 13px;");
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }
            @Override
            protected void updateItem(Ritual r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) { setGraphic(null); setStyle("-fx-background-color: transparent;"); return; }
                lbl.setText(r.getNome() + "  [" + r.getElemento() + "]");
                try {
                    var s = getClass().getResourceAsStream(caminhoIcone(r.getElemento()));
                    ico.setImage(s != null ? new Image(s) : null);
                } catch (Exception e) { ico.setImage(null); }
                setGraphic(hbox);
                setText(null);
                setStyle("-fx-background-color: transparent;");
            }
        });

        // Ao selecionar, preenche o painel de detalhes
        listaRituais.getSelectionModel().selectedItemProperty().addListener(
            (obs, ant, sel) -> { if (sel != null) mostrarDetalhes(sel); }
        );

        // Seleciona o primeiro automaticamente
        if (!listaRituais.getItems().isEmpty()) {
            listaRituais.getSelectionModel().selectFirst();
        }
    }

    private void mostrarDetalhes(Ritual ritual) {
        int danoJogador = jogador.calcularDanoRitual();
        int danoNeutro  = ritual.getDano() + danoJogador;

        Elemento forte = elementoFracoContra(ritual.getElemento());
        Elemento fraco = elementoForteContra(ritual.getElemento());

        int danoForte = (int)(danoNeutro * 1.5);
        int danoFraco = (int)(danoNeutro * 0.5);

        String grau = danoNeutro >= 60 ? "Forte" : danoNeutro >= 35 ? "Média" : "Fraca";
        String corGrau = danoNeutro >= 60 ? "#2ecc71" : danoNeutro >= 35 ? "#f39c12" : "#e74c3c";

        // Imagem do elemento
        try {
            var s = getClass().getResourceAsStream(caminhoIcone(ritual.getElemento()));
            imgElemento.setImage(s != null ? new Image(s) : null);
        } catch (Exception e) { imgElemento.setImage(null); }

        lblNomeElemento.setText(ritual.getElemento().toString());
        lblNomeRitual.setText(ritual.getNome());
        lblElemento.setText(ritual.getElemento().toString());
        lblCustoPE.setText(ritual.getCustoPE() + " PE");
        lblDanoBase.setText(ritual.getDano() > 0
            ? String.valueOf(ritual.getDano())
            : "cura: " + ritual.getCura());
        lblDanoTotal.setText(danoNeutro + "  (base " + ritual.getDano() + " + " + danoJogador + " paranormal)");
        lblEficacia.setText(grau);
        lblEficacia.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + corGrau + ";");
        lblForteContra.setText(forte.toString());
        lblDanoForte.setText("~" + danoForte + " de dano  (×1.5)");
        lblFracoContra.setText(fraco.toString());
        lblDanoFraco.setText("~" + danoFraco + " de dano  (×0.5)");
    }

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    private Elemento elementoFracoContra(Elemento e) {
        return switch (e) {
            case SANGUE       -> Elemento.CONHECIMENTO;
            case MORTE        -> Elemento.SANGUE;
            case ENERGIA      -> Elemento.MORTE;
            case CONHECIMENTO -> Elemento.ENERGIA;
            default           -> Elemento.ENERGIA;
        };
    }

    private Elemento elementoForteContra(Elemento e) {
        return switch (e) {
            case SANGUE       -> Elemento.MORTE;
            case MORTE        -> Elemento.ENERGIA;
            case ENERGIA      -> Elemento.CONHECIMENTO;
            case CONHECIMENTO -> Elemento.SANGUE;
            default           -> Elemento.SANGUE;
        };
    }

    private String caminhoIcone(Elemento e) {
        return switch (e) {
            case SANGUE       -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_sangue.png";
            case MORTE        -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_morte.png";
            case ENERGIA      -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
            case CONHECIMENTO -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_conhecimento.png";
            default           -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
        };
    }
}
