package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.util.GameState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Objects;

public class MapaController {

    @FXML private AnchorPane rootPane;
    @FXML private ImageView imgFundoMapa;
    @FXML private Label lblTituloMissao;
    @FXML private Button btnVoltar;
    @FXML private VBox vboxLocais;
    @FXML private VBox vboxDetalhesLocal;
    @FXML private Label lblNomeLocal;
    @FXML private Label lblDescricaoLocal;
    @FXML private Label lblStatusPagina;
    @FXML private Button btnAvancarLocal;

    private Missao missaoAtual;
    private LocalMapa localSelecionado;

    @FXML
    public void initialize() {
        missaoAtual = GameState.getMissaoAtual();
        if (missaoAtual == null) {
            // Tratar erro ou voltar para tela anterior
            System.err.println("Nenhuma missão atual definida no GameState.");
            return;
        }

        lblTituloMissao.setText("Missão: " + missaoAtual.getNome());
        // Carregar imagem de fundo do mapa (pode ser genérica ou específica da missão)
        // imgFundoMapa.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/mycompany/fragmentoparanormal/images/map_background.png"))));

        atualizarMapaVisual();
    }

    private void atualizarMapaVisual() {
        vboxLocais.getChildren().clear();
        int localAtualIdx = missaoAtual.getLocalAtual();

        for (LocalMapa local : missaoAtual.getLocais()) {
            HBox localNode = criarNoLocal(local, local.getOrdem() == localAtualIdx);
            vboxLocais.getChildren().add(localNode);
        }
    }

    private HBox criarNoLocal(LocalMapa local, boolean isLocalAtual) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5; -fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 5;");

        ImageView icon = new ImageView();
        icon.setFitHeight(40);
        icon.setFitWidth(40);
        // Usar uma imagem genérica ou específica para o local
        // icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(local.getCaminhoImagem()))));
        try {
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(local.getCaminhoImagem()))));
        } catch (NullPointerException e) {
            System.err.println("Imagem do local não encontrada: " + local.getCaminhoImagem());
            icon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/mycompany/fragmentoparanormal/images/icons/map_location_icon.png")))); // Fallback
        }

        Label lblNome = new Label(local.getNome());
        lblNome.setTextFill(Color.WHITE);
        lblNome.setFont(new Font("System Bold", 16));

        // Indicador de página encontrada
        if (local.isPaginaEncontrada()) {
            ImageView checkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/mycompany/fragmentoparanormal/images/icons/check_icon.png"))));
            checkIcon.setFitHeight(20);
            checkIcon.setFitWidth(20);
            hbox.getChildren().add(checkIcon);
        }

        // Indicador de Boss
        if (local.isBossRoom()) {
            ImageView bossIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/mycompany/fragmentoparanormal/images/icons/boss_icon.png"))));
            bossIcon.setFitHeight(30);
            bossIcon.setFitWidth(30);
            hbox.getChildren().add(bossIcon);
        }

        // Indicador de local atual
        if (isLocalAtual) {
            DropShadow ds = new DropShadow();
            ds.setColor(Color.YELLOW);
            ds.setRadius(10);
            hbox.setEffect(ds);
        }

        // Indicador de bloqueado
        if (!local.isLiberado()) {
            ImageView lockIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/mycompany/fragmentoparanormal/images/icons/lock_icon.png"))));
            lockIcon.setFitHeight(20);
            lockIcon.setFitWidth(20);
            hbox.getChildren().add(lockIcon);
            hbox.setDisable(true); // Desabilita interação com locais bloqueados
        } else if (local.getOrdem() > missaoAtual.getLocalAtual()) {
            hbox.setDisable(true); // Desabilita interação com locais futuros
            hbox.setStyle("-fx-opacity: 0.5; -fx-alignment: CENTER_LEFT; -fx-padding: 5; -fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 5;");
        }

        hbox.getChildren().addAll(icon, lblNome);
        hbox.setOnMouseClicked(event -> selecionarLocal(local));

        return hbox;
    }

    private void selecionarLocal(LocalMapa local) {
        localSelecionado = local;
        lblNomeLocal.setText(local.getNome());
        lblDescricaoLocal.setText(local.getDescricao());
        lblStatusPagina.setText("Página: " + (local.isPaginaEncontrada() ? "Encontrada" : "Não Encontrada"));
        vboxDetalhesLocal.setVisible(true);

        // Habilitar botão de avançar se for o próximo local liberado e a página foi encontrada
        boolean podeAvancar = false;
        if (local.getOrdem() == missaoAtual.getLocalAtual() && local.isPaginaEncontrada()) {
            podeAvancar = true;
            } else if (local.isBossRoom() && GameState.isBossDesbloqueado()) {
            podeAvancar = true;
        }
        btnAvancarLocal.setDisable(!podeAvancar);
    }

    @FXML
    private void avancarParaLocal() throws IOException {
        if (localSelecionado != null) {
            if (localSelecionado.isBossRoom() && GameState.isBossDesbloqueado()) {
                // Ir para a tela do Boss
                GameContext.loadScene("combate"); // Assumindo que a tela de combate é a do boss
            } else if (localSelecionado.getOrdem() == missaoAtual.getLocalAtual() && localSelecionado.isPaginaEncontrada()) {
                // Avança para o próximo local
                missaoAtual.setLocalAtual(missaoAtual.getLocalAtual() + 1);
                // Salvar progresso no banco de dados
                GameContext.salvarProgressoCampanha();
                // Voltar para a tela de missão, que agora carregará o novo local
                GameContext.loadScene("missao");
            } else {
                System.out.println("Não é possível avançar para este local ainda.");
            }
        }

    }

    @FXML
    private void voltarParaMissao() throws IOException {
        // Se o jogador está no mapa, ele pode voltar para a tela da missão atual
        GameContext.loadScene("missao");
    }
}
