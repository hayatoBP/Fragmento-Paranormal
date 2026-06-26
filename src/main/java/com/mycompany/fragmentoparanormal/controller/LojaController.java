package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.ItemLoja;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.LojaService;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class LojaController {

    private Personagem jogador;

    @FXML private Label lblDinheiro;
    @FXML private Label lblMensagem;
    @FXML private VBox  painelIvete;
    @FXML private VBox  painelItens;
    @FXML private HBox  painelItensConteudo;

    @FXML
    public void initialize() {
        MusicaManager.tocarIvete(); // Loja continua com a música da Ivete
        jogador = GameContext.jogadorAtual;
        atualizarInterface();
    }

    private void atualizarInterface() {
        if (jogador != null) {
            lblDinheiro.setText("💰 " + jogador.getDinheiro() + " Fragmentos");
        }
        renderizarItens();
    }

    private void renderizarItens() {
        if (painelItensConteudo != null) painelItensConteudo.getChildren().clear();
        List<ItemLoja> itens = LojaService.getItens();

        for (ItemLoja item : itens) {
            VBox card = criarCardItem(item);
            if (painelItensConteudo != null) painelItensConteudo.getChildren().add(card);
        }
    }

    private VBox criarCardItem(ItemLoja item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #16213e; -fx-border-color: " + item.getCorRaridade() + "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label lblNome = new Label(item.getNome());
        lblNome.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblRaridade = new Label(item.getLabelRaridade());
        lblRaridade.setStyle("-fx-text-fill: " + item.getCorRaridade() + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        Label lblDesc = new Label(item.getDescricao());
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);
        lblDesc.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 12px;");
        lblDesc.setMinHeight(60);

        Label lblPreco = new Label("💰 " + item.getPreco());
        lblPreco.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnComprar = new Button(item.isComprado() ? "Vendido" : "Comprar");
        btnComprar.setDisable(item.isComprado() || (jogador != null && jogador.getDinheiro() < item.getPreco()));
        btnComprar.setStyle("-fx-background-color: " + (item.isComprado() ? "#555" : "#27ae60") + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btnComprar.setPrefWidth(120);

        btnComprar.setOnAction(e -> {
            if (jogador.getDinheiro() >= item.getPreco()) {
                SomUtil.tocarConfirmar();
                jogador.setDinheiro(jogador.getDinheiro() - item.getPreco());
                item.setComprado();
                jogador.getInventario().adicionarItem(item.toItem());
                lblMensagem.setText("✔ Você comprou " + item.getNome() + "!");
                atualizarInterface();
            }
        });

        card.getChildren().addAll(lblNome, lblRaridade, lblDesc, lblPreco, btnComprar);
        return card;
    }

    @FXML
    private void mostrarItens() {
        if (painelIvete  != null) { painelIvete.setVisible(false);  painelIvete.setManaged(false);  }
        if (painelItens  != null) { painelItens.setVisible(true);   painelItens.setManaged(true);   }
        atualizarInterface();
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/ivete.fxml");
    }
}
