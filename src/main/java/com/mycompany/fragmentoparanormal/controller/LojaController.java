package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.ItemLoja;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.LojaService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TipoItem;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class LojaController {

    @FXML private Label  lblDinheiro;
    @FXML private Label  lblMensagem;
    @FXML private HBox   painelItens;

    private Personagem      jogador;
    private List<ItemLoja>  itens;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        itens   = LojaService.getItens();
        atualizarDinheiro();
        renderizarItens();

        boolean todoComprados = itens.stream().allMatch(ItemLoja::isComprado);
        if (todoComprados) {
            lblMensagem.setText(
                "Ivete: \"Você comprou tudo que eu tinha... Volte depois de uma boa noite de trabalho. " +
                "Quem sabe eu consiga mais coisas.\""
            );
            lblMensagem.setStyle("-fx-text-fill: #a080c0; -fx-font-style: italic;");
        }
    }

    private void atualizarDinheiro() {
        lblDinheiro.setText("💰 " + jogador.getDinheiro() + " moedas");
    }

    private void renderizarItens() {
        painelItens.getChildren().clear();
        painelItens.setSpacing(20);
        painelItens.setAlignment(Pos.CENTER);

        for (ItemLoja item : itens) {
            painelItens.getChildren().add(criarCartaoItem(item));
        }
    }

    private VBox criarCartaoItem(ItemLoja item) {
        VBox cartao = new VBox(12);
        cartao.setAlignment(Pos.TOP_CENTER);
        cartao.setPadding(new Insets(18));
        cartao.setPrefWidth(200);
        cartao.setPrefHeight(280);

        // Cor de borda por raridade
        String corBorda = item.getCorRaridade();
        String corFundo = item.isComprado() ? "#1a1a1a" : "#16213e";

        cartao.setStyle(
            "-fx-background-color: " + corFundo + ";" +
            "-fx-border-color: " + corBorda + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-background-radius: 10;"
        );

        // Efeito de brilho na borda
        if (!item.isComprado()) {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web(corBorda, 0.6));
            glow.setRadius(12);
            cartao.setEffect(glow);
        }

        // Badge raridade
        Label lblRaridade = new Label(item.getLabelRaridade());
        lblRaridade.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + corBorda + ";" +
            "-fx-font-weight: bold;"
        );

        // Nome
        Text lblNome = new Text(item.getNome());
        lblNome.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblNome.setStyle("-fx-fill: #f0e6ff;");
        lblNome.setTextAlignment(TextAlignment.CENTER);
        lblNome.setWrappingWidth(170);

        // Separador
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + corBorda + "; -fx-opacity: 0.4;");

        // Descrição
        Text lblDesc = new Text(item.getDescricao());
        lblDesc.setStyle("-fx-fill: #c0b0dd; -fx-font-size: 12px;");
        lblDesc.setTextAlignment(TextAlignment.CENTER);
        lblDesc.setWrappingWidth(170);

        // Preço
        Label lblPreco = new Label("💰 " + item.getPreco() + " moedas");
        lblPreco.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + (item.isComprado() ? "#555" : "#f0c040") + ";"
        );

        // Botão comprar
        Button btnComprar = new Button(item.isComprado() ? "✓ Comprado" : "Comprar");
        btnComprar.setDisable(item.isComprado());
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setStyle(
            "-fx-background-color: " + (item.isComprado() ? "#333" : "#6c3483") + ";" +
            "-fx-text-fill: " + (item.isComprado() ? "#666" : "white") + ";" +
            "-fx-font-weight: bold;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-cursor: " + (item.isComprado() ? "default" : "hand") + ";"
        );

        btnComprar.setOnAction(e -> comprar(item, btnComprar, cartao, lblPreco));

        cartao.getChildren().addAll(lblRaridade, lblNome, sep, lblDesc, lblPreco, btnComprar);
        VBox.setVgrow(lblDesc, Priority.ALWAYS);
        return cartao;
    }

    private void comprar(ItemLoja item, Button btnComprar, VBox cartao, Label lblPreco) {
        if (item.isComprado()) return;

        if (jogador.getDinheiro() < item.getPreco()) {
            lblMensagem.setText("Ivete: \"Sinto muito, mas isso não é caridade. Você precisa de "
                + item.getPreco() + " moedas. Tem " + jogador.getDinheiro() + ".\"");
            lblMensagem.setStyle("-fx-text-fill: #e74c3c; -fx-font-style: italic;");
            return;
        }

        // Efetua a compra
        jogador.gastarDinheiro(item.getPreco());
        item.setComprado();

        // Aplica o efeito
        aplicarItem(item);

        // Atualiza visual
        atualizarDinheiro();
        btnComprar.setText("✓ Comprado");
        btnComprar.setDisable(true);
        btnComprar.setStyle(
            "-fx-background-color: #333; -fx-text-fill: #666;" +
            "-fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6;"
        );
        lblPreco.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555;");
        cartao.setStyle(
            "-fx-background-color: #1a1a1a; -fx-border-color: #444;" +
            "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
        );
        cartao.setEffect(null);

        lblMensagem.setText("Ivete: \"Boa escolha. Espero que te mantenha vivo por mais um pouco.\"");
        lblMensagem.setStyle("-fx-text-fill: #a0d0a0; -fx-font-style: italic;");

        salvar();
    }

    private void aplicarItem(ItemLoja item) {
        switch (item.getTipo()) {
            case CURA            -> jogador.setVida(Math.min(jogador.getVidaMaxima(),
                                        jogador.getVida() + item.getEfeito()));
            case RESTAURAR_PE    -> jogador.setPontosEsforco(Math.min(jogador.getPeMaximo(),
                                        jogador.getPontosEsforco() + item.getEfeito()));
            case BOOST_FORCA     -> jogador.setForca(jogador.getForca() + item.getEfeito());
            case BOOST_VIDA_MAX  -> {
                jogador.setVidaMaxima(jogador.getVidaMaxima() + item.getEfeito());
                jogador.setVida(jogador.getVida() + item.getEfeito());
            }
            case BOOST_PARANORMAL-> jogador.setPoderParanormal(
                                        jogador.getPoderParanormal() + item.getEfeito());
            default              -> jogador.getInventario().adicionarItem(item.toItem());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        salvar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    private void salvar() {
        try { JogadorDAO.salvar(jogador); } catch (Exception e) {
            System.err.println("[LojaController] Erro ao salvar: " + e.getMessage());
        }
    }
}
