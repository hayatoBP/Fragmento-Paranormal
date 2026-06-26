package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

/**
 * MapaController — mapa visual horizontal com nós conectados por corredores.
 *
 * Layout visual:
 *   [Entrada] ─corredor─ [Local 1] ─corredor─ [Local 2] ─ ... ─ [💀 Boss]
 *
 * Regras visuais:
 *   - Nó FUTURO     → cinza escuro, cadeado
 *   - Nó VISITADO   → vermelho escuro, check ✔
 *   - Nó ATUAL      → vermelho brilhante, ícone pulsante do personagem
 *   - Nó BOSS ROOM  → caveira, vermelho intenso
 *   - Corredor       → linha vermelha (visitado) ou cinza (futuro)
 *
 * Fluxo:
 *   Avançar na missão = entra no corredor (batalha.png) → combate →
 *   Investigar = encontra a página → avança para o próximo nó.
 *   Na sala do boss: investigar encontra a última página E dispara a boss fight.
 */
public class MapaController {

    @FXML private ImageView imgFundoMapa;
    @FXML private ImageView imgElementoIcone;
    @FXML private Label     lblTituloMissao;
    @FXML private Label     lblProgresso;
    @FXML private StackPane painelMapa;
    @FXML private VBox      painelInfo;
    @FXML private Label     lblNomeLocal;
    @FXML private Label     lblDescricaoLocal;
    @FXML private Label     lblStatusPagina;
    @FXML private Button    btnAvancarLocal;

    private Missao    missaoAtual;
    private LocalMapa localSelecionado;

    // ---------------------------------------------------------------
    // Constantes visuais
    // ---------------------------------------------------------------
    private static final double NO_RAIO      = 36;
    private static final double NO_ESPACAMENTO = 128; // px entre centros dos nós
    private static final String COR_VISITADO = "#8b0000";
    private static final String COR_ATUAL    = "#cc0000";
    private static final String COR_FUTURO   = "#2a2a2a";
    private static final String COR_BOSS     = "#cc0000";
    private static final String COR_LINHA_VISITADA = "#8b0000";
    private static final String COR_LINHA_FUTURA   = "#2a2a2a";

    @FXML
    public void initialize() {
        // Mapa usa a música do elemento da missão
        Missao m = GameState.getMissaoAtual();
        if (m != null) MusicaManager.tocarMissao(m.getElemento().name());
        missaoAtual = GameState.getMissaoAtual();
        if (missaoAtual == null) return;

        carregarFundo();
        carregarIconeElemento();
        lblTituloMissao.setText(missaoAtual.getNome());
        atualizarProgresso();
        desenharMapa();
    }

    // ---------------------------------------------------------------
    // Carregamento de imagens
    // ---------------------------------------------------------------
    private void carregarFundo() {
        try {
            String elem = missaoAtual.getElemento().toString().toLowerCase();
            // Usa o primeiro cenário como fundo do mapa
            var s = getClass().getResourceAsStream(
                "/com/mycompany/fragmentoparanormal/images/cenarios/" + elem + "/1.png");
            if (s != null) imgFundoMapa.setImage(new Image(s));
        } catch (Exception e) {
            System.err.println("Fundo do mapa não encontrado: " + e.getMessage());
        }
    }

    private void carregarIconeElemento() {
        try {
            String elem = missaoAtual.getElemento().toString().toLowerCase();
            String ext  = ".png";
            var s = getClass().getResourceAsStream(
                "/com/mycompany/fragmentoparanormal/images/simbolos/" + elem + ext);
            if (s != null && imgElementoIcone != null)
                imgElementoIcone.setImage(new Image(s));
        } catch (Exception ignored) {}
    }

    private void atualizarProgresso() {
        long encontradas = missaoAtual.getLocais().stream()
                .filter(l -> !l.isBossRoom())
                .filter(LocalMapa::isPaginaEncontrada)
                .count();
        long total = missaoAtual.getLocais().stream()
                .filter(l -> !l.isBossRoom())
                .count();
        lblProgresso.setText(encontradas + " / " + total + " páginas encontradas");
    }

    // ---------------------------------------------------------------
    // DESENHO DO MAPA
    // ---------------------------------------------------------------
    private void desenharMapa() {
        painelMapa.getChildren().clear();

        List<LocalMapa> locais = missaoAtual.getLocais();
        int qtd = locais.size();  // normalmente 7 (6 normais + 1 boss)
        int localAtualIdx = missaoAtual.getLocalAtual();

        // Painel horizontal para os nós
        HBox trilha = new HBox();
        trilha.setAlignment(Pos.CENTER);
        trilha.setSpacing(0);

        for (int i = 0; i < qtd; i++) {
            LocalMapa local = locais.get(i);
            boolean isAtual    = (local.getOrdem() == localAtualIdx);
            boolean isVisitado = local.isPaginaEncontrada() || (local.getOrdem() < localAtualIdx);
            boolean isFuturo   = (local.getOrdem() > localAtualIdx) && !local.isBossRoom()
                                 || (local.isBossRoom() && local.getOrdem() > localAtualIdx && !todosPaginasNormaisColetadas());

            // Corredor ANTES do nó (exceto no primeiro)
            if (i > 0) {
                boolean corredorVisitado = locais.get(i - 1).isPaginaEncontrada()
                                           || (locais.get(i - 1).getOrdem() < localAtualIdx);
                trilha.getChildren().add(criarCorredor(corredorVisitado));
            }

            // Nó do local
            StackPane no = criarNo(local, isAtual, isVisitado, isFuturo);
            // Clique no nó
            final LocalMapa localFinal = local;
            no.setOnMouseClicked(e -> selecionarLocal(localFinal));
            trilha.getChildren().add(no);
        }

        // Centraliza verticalmente
        VBox wrapper = new VBox(trilha);
        wrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(wrapper, Priority.ALWAYS);

        painelMapa.getChildren().add(wrapper);
    }

    private boolean todosPaginasNormaisColetadas() {
        return missaoAtual.getLocais().stream()
                .filter(l -> !l.isBossRoom())
                .allMatch(LocalMapa::isPaginaEncontrada);
    }

    // ---------------------------------------------------------------
    // Criar nó visual de um local
    // ---------------------------------------------------------------
    private StackPane criarNo(LocalMapa local, boolean isAtual, boolean isVisitado, boolean isFuturo) {
        StackPane no = new StackPane();
        no.setPrefSize(NO_RAIO * 2, NO_RAIO * 2 + 56);
        no.setAlignment(Pos.CENTER);
        no.setCursor(javafx.scene.Cursor.HAND);

        // Círculo principal
        Circle circulo = new Circle(NO_RAIO);
        if (local.isBossRoom()) {
            circulo.setFill(Color.web("#3d0000"));
            circulo.setStroke(Color.web(COR_BOSS));
            circulo.setStrokeWidth(3);
        } else if (isAtual) {
            circulo.setFill(Color.web("#3d0000"));
            circulo.setStroke(Color.web(COR_ATUAL));
            circulo.setStrokeWidth(3);
        } else if (isVisitado) {
            circulo.setFill(Color.web(COR_VISITADO));
            circulo.setStroke(Color.web("#cc0000"));
            circulo.setStrokeWidth(2);
        } else {
            circulo.setFill(Color.web(COR_FUTURO));
            circulo.setStroke(Color.web("#444"));
            circulo.setStrokeWidth(1.5);
        }

        // Ícone dentro do círculo
        Label iconeLabel = new Label(getIconeNo(local, isAtual, isVisitado, isFuturo));
        iconeLabel.setFont(Font.font("System", FontWeight.BOLD, local.isBossRoom() ? 26 : 20));
        iconeLabel.setTextFill(isFuturo ? Color.web("#444") : Color.WHITE);

        // Nome abaixo do círculo
        Label nome = new Label(local.isBossRoom() ? "BOSS" : (local.getOrdem() + 1) + ". " + local.getNome());
        nome.setFont(Font.font("System", FontWeight.BOLD, 11));
        nome.setTextFill(isFuturo ? Color.web("#444")
                        : isAtual ? Color.web(COR_ATUAL)
                        : isVisitado ? Color.web("#cc6666")
                        : Color.web("#666"));
        nome.setWrapText(true);
        nome.setMaxWidth(NO_RAIO * 2 + 20);
        nome.setAlignment(Pos.CENTER);
        nome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Miniatura do cenário
        ImageView miniatura = new ImageView();
        miniatura.setFitWidth(NO_RAIO * 2 - 8);
        miniatura.setFitHeight(NO_RAIO * 2 - 8);
        miniatura.setPreserveRatio(true);
        miniatura.setClip(new Circle(NO_RAIO - 4));
        try {
            var s = getClass().getResourceAsStream(local.getCaminhoImagem());
            if (s != null) miniatura.setImage(new Image(s));
        } catch (Exception ignored) {}

        // Stack: círculo + miniatura (se visitado) + ícone por cima
        StackPane circuloStack = new StackPane();
        circuloStack.getChildren().add(circulo);
        if (isVisitado || isAtual) {
            circuloStack.getChildren().add(miniatura);
        }
        circuloStack.getChildren().add(iconeLabel);

        // Animação pulse no local atual
        if (isAtual) {
            ScaleTransition pulse = new ScaleTransition(Duration.millis(800), circulo);
            pulse.setFromX(1.0); pulse.setToX(1.12);
            pulse.setFromY(1.0); pulse.setToY(1.12);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(ScaleTransition.INDEFINITE);
            pulse.play();
        }

        // VBox com círculo + nome
        VBox conteudo = new VBox(6, circuloStack, nome);
        conteudo.setAlignment(Pos.CENTER);

        no.getChildren().add(conteudo);
        return no;
    }

    private String getIconeNo(LocalMapa local, boolean isAtual, boolean isVisitado, boolean isFuturo) {
        if (local.isBossRoom()) return "☠";
        if (isAtual)    return "◉";   // posição atual
        if (isVisitado) return "✔";   // já passou
        if (isFuturo)   return "🔒";  // bloqueado
        return "○";
    }

    // ---------------------------------------------------------------
    // Corredor entre nós (linha horizontal)
    // ---------------------------------------------------------------
    private StackPane criarCorredor(boolean visitado) {
        double largura = NO_ESPACAMENTO - (NO_RAIO * 2);
        StackPane corredor = new StackPane();
        corredor.setPrefSize(largura, NO_RAIO * 2 + 56);
        corredor.setAlignment(Pos.CENTER);

        // Linha principal
        Rectangle linha = new Rectangle(largura, 4);
        linha.setFill(visitado ? Color.web(COR_LINHA_VISITADA) : Color.web(COR_LINHA_FUTURA));
        linha.setArcWidth(4);
        linha.setArcHeight(4);

        // Seta indicando direção
        Label seta = new Label("▶");
        seta.setFont(Font.font(10));
        seta.setTextFill(visitado ? Color.web("#cc4444") : Color.web("#333"));

        corredor.getChildren().addAll(linha, seta);
        return corredor;
    }

    // ---------------------------------------------------------------
    // Seleção de local
    // ---------------------------------------------------------------
    private void selecionarLocal(LocalMapa local) {
        localSelecionado = local;

        lblNomeLocal.setText(local.isBossRoom() ? "☠ Sala do Boss — " + missaoAtual.getNome()
                                                : local.getNome());
        lblDescricaoLocal.setText(local.getDescricao());

        boolean jaEstaqui   = (local.getOrdem() == missaoAtual.getLocalAtual());
        boolean paginaAchada = local.isPaginaEncontrada();
        boolean visitado     = local.getOrdem() < missaoAtual.getLocalAtual();
        boolean bloqueado    = local.getOrdem() > missaoAtual.getLocalAtual()
                               && !(local.isBossRoom() && todosPaginasNormaisColetadas());

        String status;
        if (local.isBossRoom()) {
            int idx = com.mycompany.fragmentoparanormal.service.MissaoService
                      .getIndiceMissao(missaoAtual.getElemento());
            boolean bossDerrota = GameState.isBossMissaoDerrotado(idx);
            status = bossDerrota ? "✔ Boss derrotado — missão concluída!"
                    : todosPaginasNormaisColetadas() ? "⚠ Boss aguarda! Entre para iniciar o confronto."
                    : "🔒 Colete todas as páginas para desbloquear o Boss.";
        } else if (visitado) {
            status = "✔ Página coletada — local já explorado.";
        } else if (jaEstaqui && paginaAchada) {
            status = "✔ Página encontrada! Avance para o próximo corredor.";
        } else if (jaEstaqui) {
            status = GameState.isCombateVencidoNesteLocal()
                ? "🔍 Inimigo derrotado — investigue para encontrar a página."
                : "⚔ Derrote o inimigo deste local para poder investigar.";
        } else {
            status = "🔒 Acesse os locais anteriores primeiro.";
        }
        lblStatusPagina.setText(status);

        // Botão ativo só no local atual OU boss room (se todas págs coletadas)
        boolean podeIr = jaEstaqui
                || (local.isBossRoom() && todosPaginasNormaisColetadas()
                    && !GameState.isBossMissaoDerrotado(
                        com.mycompany.fragmentoparanormal.service.MissaoService
                        .getIndiceMissao(missaoAtual.getElemento())));

        btnAvancarLocal.setDisable(!podeIr);
        btnAvancarLocal.setText(local.isBossRoom() ? "☠ Enfrentar o Boss" : "▶ Ir para este local");

        painelInfo.setVisible(true);
        painelInfo.setManaged(true);
    }

    // ---------------------------------------------------------------
    // Ações dos botões
    // ---------------------------------------------------------------
    @FXML
    private void avancarParaLocal(ActionEvent event) {
        if (localSelecionado == null) return;

        if (localSelecionado.isBossRoom()) {
            int idx = com.mycompany.fragmentoparanormal.service.MissaoService
                      .getIndiceMissao(missaoAtual.getElemento());
            if (!GameState.isBossMissaoDerrotado(idx)) {
                GameContext.inimigoAtual = com.mycompany.fragmentoparanormal.model.Inimigo
                        .criarBossMissao(missaoAtual.getElemento(), idx);
                GameState.setOrigemInventario("COMBATE");
                SomUtil.tocarConfirmar();
            com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTela(
                    event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
            }
        } else {
            // Volta para a missão (tela principal da missão com os botões Avançar/Investigar)
            com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTela(
                event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    @FXML
    private void voltarParaMissao(ActionEvent event) {
        SomUtil.tocarVoltar();
        com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTela(
            event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
    }
}
