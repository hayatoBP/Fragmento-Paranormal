package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.GeradorInimigoService;
import com.mycompany.fragmentoparanormal.service.InvestigacaoService;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Optional;

/**
 * MissaoController — versão corrigida P12.
 *
 * Fluxo correto:
 *   1. Jogador entra no local → pode INVESTIGAR (1x) e AVANÇAR.
 *   2. AVANÇAR → gera combate contra inimigo do local atual.
 *      - Se vencer o combate, volta aqui com combateVencido=true → libera INVESTIGAR.
 *      - Se ainda não venceu combate, INVESTIGAR fica bloqueado.
 *   3. INVESTIGAR (só após vencer combate) → tenta achar página.
 *      - Achou página → libera o próximo local (avançar avança de local).
 *      - Não achou → pode tentar avançar de novo para novo combate e tentar investigar.
 *   4. Quando página encontrada no local atual → próximo AVANÇAR move para o próximo local.
 *   5. Último local (boss room) → AVANÇAR aciona boss da missão.
 *   6. Após boss da missão derrotado → missão concluída.
 *   7. Todas as missões concluídas → boss final desbloqueado.
 */
public class MissaoController {

    private Personagem jogador;
    private Missao     missao;
    private int        indiceMissao;

    @FXML private Label     lblNomeMissao;
    @FXML private Label     lblFragmentos;
    @FXML private Label     lblVida;
    @FXML private Label     lblPE;
    @FXML private Label     lblEventos;
    @FXML private ImageView imgCena;
    @FXML private ImageView imgPersonagemMissao;
    @FXML private ImageView imgElementoIcone;
    @FXML private Button    btnInvestigar;
    @FXML private Button    btnAvancar;
    @FXML private StackPane rootPane;

    @FXML
    public void initialize() {
        jogador      = GameContext.jogadorAtual;
        missao       = GameState.getMissaoAtual();
        indiceMissao = missao != null
                ? MissaoService.getIndiceMissao(missao.getElemento()) : 0;

        // Se o jogador upou e tem escolha pendente, redireciona para tela de escolha
        if (jogador != null && jogador.isEscolhaPendente()) {
            EscolhaHabilidadeController.telaOrigem = "MISSAO";
            javafx.application.Platform.runLater(() -> {
                try {
                    com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTelaPorNode(
                        btnAvancar,
                        "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
                } catch (Exception ex) {
                    System.err.println("Erro ao abrir escolha de habilidade: " + ex.getMessage());
                }
            });
            return;
        }

        if (!GameState.isMissaoEmAndamento()) {
            if (jogador != null) jogador.resetarParaMissao();
            GameState.setMissaoEmAndamento(true);
            GameState.setInvestigouNesteAvanco(false);
            GameState.setCombateVencidoNesteLocal(false);
        }

        // Música da missão (padrão do elemento)
        if (missao != null) {
            MusicaManager.tocarMissao(missao.getElemento().name());
        }

        if (missao != null) {
            lblNomeMissao.setText(missao.getNome());
            carregarImagemLocal();
            // Mostra informação do local atual
            LocalMapa localAtual = missao.getLocalAtualObj();
            if (localAtual != null) {
                String estadoCombate = GameState.isCombateVencidoNesteLocal()
                    ? (GameState.isInvestigouNesteAvanco()
                        ? "✔ Local investigado — avance para o próximo."
                        : "✔ Local limpo — INVESTIGUE para encontrar a página!")
                    : "⚔ Avance para enfrentar o inimigo deste local.";
                lblEventos.setText("📍 " + localAtual.getNome()
                    + "\n" + localAtual.getDescricao()
                    + "\n\n" + estadoCombate);
            }
        }

        // INVESTIGAR só fica disponível após vencer o combate do local atual
        // E apenas se ainda não investigou neste local
        boolean podeInvestigar = GameState.isCombateVencidoNesteLocal()
                && !GameState.isInvestigouNesteAvanco();
        btnInvestigar.setDisable(!podeInvestigar);

        atualizarTela();
        animarEntradaCena();
    }

    /** Animação de fade-in ao entrar em um novo local */
    private void animarEntradaCena() {
        if (imgCena == null) return;
        imgCena.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(700), imgCena);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void carregarImagemLocal() {
        try {
            LocalMapa localAtual = missao.getLocalAtualObj();
            if (localAtual != null) {
                String caminho;
                if (!GameState.isCombateVencidoNesteLocal() && !localAtual.isBossRoom()) {
                    // Corredor — ainda tem inimigo → usa cenário de batalha
                    String elem = missao.getElemento().toString().toLowerCase();
                    caminho = "/com/mycompany/fragmentoparanormal/images/cenarios/" + elem + "/batalha.png";
                } else {
                    // Local explorado ou boss room → usa cenário do local
                    caminho = localAtual.getCaminhoImagem();
                }
                var stream = getClass().getResourceAsStream(caminho);
                if (stream != null) imgCena.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.err.println("Cenário não encontrado: " + e.getMessage());
        }
        // Ícone do elemento
        try {
            String nomeElem = missao.getElemento().toString().toLowerCase();
            String ext = ".png";
            var iconStream = getClass().getResourceAsStream(
                "/com/mycompany/fragmentoparanormal/images/simbolos/" + nomeElem + ext);
            if (iconStream != null && imgElementoIcone != null)
                imgElementoIcone.setImage(new Image(iconStream));
        } catch (Exception e) {
            System.err.println("Ícone elemento não encontrado: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Avançar — gera combate OU avança de local se página já encontrada
    // ------------------------------------------------------------------
    @FXML
    private void avancar(ActionEvent event) {
        SomUtil.tocarConfirmar();
        if (jogador == null || missao == null) return;

        LocalMapa localAtual = missao.getLocalAtualObj();
        if (localAtual == null) return;

        // Se é sala do boss (último local) → combate boss da missão
        if (localAtual.isBossRoom()) {
            if (!GameState.isBossMissaoDerrotado(indiceMissao)) {
                if (GameState.isCombateVencidoNesteLocal()) {
                    // Combate já vencido: mostra texto e libera investigar
                    try {
                        String elem = missao.getElemento().toString().toLowerCase();
                        var s = getClass().getResourceAsStream(
                            "/com/mycompany/fragmentoparanormal/images/cenarios/" + elem + "/boss.png");
                        if (s != null) imgCena.setImage(new Image(s));
                    } catch (Exception ignored) {}
                    lblEventos.setText("☠ Você entrou na Sala do Boss!\n\n"
                        + "Investigue para encontrar a última página\n"
                        + "— mas cuidado, o Guardião está aqui.");
                    btnInvestigar.setDisable(false);
                } else {
                    // Ainda não venceu combate: gera inimigo para liberar investigação
                    GameState.setCombateVencidoNesteLocal(false);
                    GameState.setInvestigouNesteAvanco(false);
                    Inimigo inimigo = GeradorInimigoService.gerarInimigo(jogador);
                    GameContext.inimigoAtual = inimigo;
                    GameState.setOrigemInventario("COMBATE");
                    animarSaidaCena(() -> TelaUtil.trocarTela(event,
                        "/com/mycompany/fragmentoparanormal/view/combate.fxml"));
                }
            } else {
                lblEventos.setText("✔ Boss já derrotado. Missão concluída!");
            }
            return;
        }

        // Se página já encontrada no local atual → avança para próximo local
        if (localAtual.isPaginaEncontrada()) {
            avancarParaProximoLocal(event);
            return;
        }

        // Ainda não tem página: gera combate para liberar investigação
        GameState.setCombateVencidoNesteLocal(false);
        GameState.setInvestigouNesteAvanco(false);

        Inimigo inimigo = GeradorInimigoService.gerarInimigo(jogador);
        GameContext.inimigoAtual = inimigo;
        GameState.setOrigemInventario("COMBATE");

        // Animação de saída antes de ir para o combate
        animarSaidaCena(() -> TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml"));
    }

    /** Animação de saída: desliza a cena para a esquerda antes de trocar de tela */
    private void animarSaidaCena(Runnable aoTerminar) {
        if (imgCena == null) {
            aoTerminar.run();
            return;
        }
        // Desabilita botões durante a animação
        btnAvancar.setDisable(true);
        btnInvestigar.setDisable(true);

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), imgCena);
        slide.setFromX(0);
        slide.setToX(-80);
        slide.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fade = new FadeTransition(Duration.millis(400), imgCena);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ParallelTransition saida = new ParallelTransition(slide, fade);
        saida.setOnFinished(e -> aoTerminar.run());
        saida.play();
    }

    private void avancarParaProximoLocal(ActionEvent event) {
        int proximoIdx = missao.getLocalAtual() + 1;
        if (proximoIdx >= missao.getLocais().size()) {
            lblEventos.setText("✔ Você já completou todos os locais desta missão.");
            return;
        }
        // Libera o próximo local
        missao.getLocais().get(proximoIdx).setLiberado(true);
        missao.setLocalAtual(proximoIdx);

        // Reseta estado para o novo local
        GameState.setCombateVencidoNesteLocal(false);
        GameState.setInvestigouNesteAvanco(false);

        GameContext.salvarProgressoCampanha();
        // Animação de saída antes de recarregar a tela de missão
        animarSaidaCena(() -> TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml"));
    }

    // ------------------------------------------------------------------
    // Personagem no cenário (armado se tiver arma)
    // ------------------------------------------------------------------
    private void carregarPersonagemMissao() {
        if (imgPersonagemMissao == null || jogador == null) return;
        try {
            javafx.scene.image.Image imgPersonagem =
                jogador.getArmaEquipada() != null
                    ? com.mycompany.fragmentoparanormal.util.ImagemUtil.carregarPersonagemArmado(jogador.getNomePersonagemBase())
                    : com.mycompany.fragmentoparanormal.util.ImagemUtil.carregarPersonagem(jogador.getNomePersonagemBase());
            if (imgPersonagem != null) {
                imgPersonagemMissao.setImage(imgPersonagem);
                imgPersonagemMissao.setVisible(true);
            }
        } catch (Exception ignored) {}
    }

    // ------------------------------------------------------------------
    // Investigar — 1x por local, só após vencer combate
    // ------------------------------------------------------------------
    @FXML
    private void investigar() {
        if (jogador == null || GameState.isInvestigouNesteAvanco()) return;
        if (!GameState.isCombateVencidoNesteLocal()) {
            lblEventos.setText("⚠ Derrote o inimigo deste local antes de investigar!");
            return;
        }

        GameState.setInvestigouNesteAvanco(true);
        btnInvestigar.setDisable(true);

        String resultado = InvestigacaoService.investigar(jogador);
        LocalMapa localAtual = missao.getLocalAtualObj();

        if (resultado.equals("FRAGMENTO")) {
            if (localAtual != null && !localAtual.isPaginaEncontrada()) {
                localAtual.setPaginaEncontrada(true);
                int numPag = localAtual.getOrdem() + 1;
                GameState.registrarPagina(indiceMissao, numPag);
                jogador.ganharXp(150);

                // ── SALA DO BOSS: última página encontrada → boss fight automático
                if (localAtual.isBossRoom()) {
                    lblEventos.setText("📖 ÚLTIMA PÁGINA ENCONTRADA! +150 XP\n\n"
                        + "☠ O GUARDIÃO FOI DESPERTO!\nA batalha começa agora...");
                    atualizarTela();
                    GameContext.salvarProgressoCampanha();
                    javafx.animation.PauseTransition pausa =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                    pausa.setOnFinished(ev -> {
                        GameContext.inimigoAtual =
                            com.mycompany.fragmentoparanormal.model.Inimigo
                            .criarBossMissao(missao.getElemento(), indiceMissao);
                        GameState.setOrigemInventario("COMBATE");
                        javafx.application.Platform.runLater(() -> {
                            try {
                                com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTelaPorNode(
                                    btnInvestigar,
                                    "/com/mycompany/fragmentoparanormal/view/combate.fxml");
                            } catch (Exception ex) {
                                System.err.println("Erro ao ir para boss: " + ex.getMessage());
                            }
                        });
                    });
                    pausa.play();
                    return;
                }

                // Verifica se todas as páginas normais foram coletadas → libera boss room
                long paginasColetadas = missao.getLocais().stream()
                        .filter(l -> !l.isBossRoom())
                        .filter(LocalMapa::isPaginaEncontrada)
                        .count();
                long totalLocais = missao.getLocais().stream()
                        .filter(l -> !l.isBossRoom())
                        .count();

                if (paginasColetadas == totalLocais) {
                    int idxBoss = missao.getLocais().size() - 1;
                    missao.getLocais().get(idxBoss).setLiberado(true);
                    lblEventos.setText("📖 Página encontrada! +150 XP\n\n"
                        + "⚠ Todas as páginas coletadas!\n"
                        + "☠ A SALA DO BOSS foi desbloqueada!\n"
                        + "Avance para o corredor final!");
                } else {
                    lblEventos.setText("📖 Página encontrada! +150 XP\n"
                        + "Avance para o próximo corredor.");
                }

                try {
                    var stream = getClass().getResourceAsStream(localAtual.getCaminhoImagem());
                    if (stream != null) imgCena.setImage(new Image(stream));
                } catch (Exception e) {
                    System.err.println("Erro ao atualizar imagem: " + e.getMessage());
                }
            } else {
                lblEventos.setText("Você já coletou a página deste local.");
            }

        } else if (resultado.equals("INIMIGO_SURPRESA")) {
            // Inimigo surpresa não bloqueia investigação — apenas avisa
            lblEventos.setText("💀 Um inimigo surge! Ele foi afugentado mas você não achou nada útil.");

        } else if (resultado.startsWith("EVENTO_RUIM:")) {
            lblEventos.setText(InvestigacaoService.descricaoEventoRuim(resultado));

        } else if (resultado.startsWith("ARMA:")) {
            String[] partes = resultado.split(":", 3);
            Arma armaAchada = new Arma(partes[1], Integer.parseInt(partes[2]));
            lblEventos.setText("⚔ Você encontrou uma arma: " + partes[1] + "!");
            abrirDialogoComparacao(armaAchada);

        } else if (resultado.startsWith("ITEM_CONSUMIVEL:") || resultado.startsWith("ITEM_PERMANENTE:")) {
            lblEventos.setText("🎒 Item encontrado: " + resultado.split(":", 2)[1] + "\nGuardado no inventário.");

        } else if (resultado.startsWith("ITEM_ARTEFATO:")) {
            lblEventos.setText("🔮 Artefato encontrado: " + resultado.split(":", 2)[1] + "\nGuardado no inventário.");

        } else if (resultado.equals("PISTA_RARA")) {
            jogador.ganharXp(80);
            lblEventos.setText("🔍 Você encontrou uma pista rara! +80 XP.");

        } else {
            lblEventos.setText("Você vasculhou o local, mas não encontrou nada útil.\nTente avançar novamente para outro combate.");
        }

        atualizarTela();
        GameContext.salvarProgressoCampanha();
    }

    // ------------------------------------------------------------------
    // Dialog de comparação de armas (inalterado)
    // ------------------------------------------------------------------
    private void abrirDialogoComparacao(Arma armaAchada) {
        Arma armaAtual = jogador.getArmaEquipada();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("⚔ Arma Encontrada!");
        dialog.setHeaderText("Você encontrou uma arma. Deseja equipá-la?");

        HBox conteudo = new HBox(30);
        conteudo.setPadding(new Insets(20));
        conteudo.setAlignment(Pos.CENTER);
        conteudo.getChildren().addAll(
            painelArma("Arma Atual", armaAtual, "#2c3e50"),
            criarSeparador(),
            painelArma("Arma Encontrada", armaAchada, "#6c3483")
        );

        int danoAtual  = armaAtual  != null ? armaAtual.getBonusDano()  : 0;
        int danoAchada = armaAchada.getBonusDano();
        String comparacao = danoAchada > danoAtual
            ? "✅ A arma encontrada é mais forte! (+" + (danoAchada - danoAtual) + " de dano)"
            : danoAchada < danoAtual
            ? "⚠ Sua arma atual é mais forte. (" + (danoAtual - danoAchada) + " a menos)"
            : "➖ Mesma força de dano.";

        Label lblComp = new Label(comparacao);
        lblComp.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        lblComp.setPadding(new Insets(8, 0, 0, 0));

        VBox raiz = new VBox(10, conteudo, lblComp);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(10, 20, 10, 20));
        raiz.setStyle("-fx-background-color: #1a1a2e;");

        dialog.getDialogPane().setContent(raiz);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a2e;");

        ButtonType btnEquipar   = new ButtonType("Equipar Nova Arma",    ButtonBar.ButtonData.OK_DONE);
        ButtonType btnGuardar   = new ButtonType("Guardar no Inventário", ButtonBar.ButtonData.OTHER);
        ButtonType btnDescartar = new ButtonType("Descartar",              ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEquipar, btnGuardar, btnDescartar);

        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isPresent() && resposta.get() == btnEquipar) {
            Arma anterior = jogador.getArmaEquipada();
            if (anterior != null) jogador.adicionarArma(anterior);
            jogador.setArmaEquipada(armaAchada);
            lblEventos.setText("⚔ " + armaAchada.getNome() + " equipada!");
        } else if (resposta.isPresent() && resposta.get() == btnGuardar) {
            jogador.adicionarArma(armaAchada);
            lblEventos.setText("⚔ " + armaAchada.getNome() + " guardada no inventário.");
        } else {
            lblEventos.setText("⚔ " + armaAchada.getNome() + " descartada.");
        }
        atualizarTela();
    }

    private VBox painelArma(String titulo, Arma arma, String corBorda) {
        VBox painel = new VBox(8);
        painel.setAlignment(Pos.CENTER_LEFT);
        painel.setPadding(new Insets(14));
        painel.setMinWidth(180);
        painel.setStyle("-fx-background-color: #16213e; -fx-border-color: " + corBorda
            + "; -fx-border-width: 2; -fx-border-radius: 6; -fx-background-radius: 6;");

        Text lblTitulo = new Text(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitulo.setStyle("-fx-fill: #ecf0f1;");

        if (arma == null) {
            Text lblNenhuma = new Text("Nenhuma arma equipada");
            lblNenhuma.setStyle("-fx-fill: #aaa; -fx-font-style: italic;");
            painel.getChildren().addAll(lblTitulo, lblNenhuma);
        } else {
            Text lblNome  = new Text("🗡 " + arma.getNome());
            Text lblDano  = new Text("Bônus de Dano:  +" + arma.getBonusDano());
            Text lblForca = new Text("Dano total base: " + (jogador.getForca() + arma.getBonusDano()));
            lblNome .setStyle("-fx-fill: #f0e6ff; -fx-font-size: 13px; -fx-font-weight: bold;");
            lblDano .setStyle("-fx-fill: #e74c3c; -fx-font-size: 13px;");
            lblForca.setStyle("-fx-fill: #f39c12; -fx-font-size: 12px;");
            painel.getChildren().addAll(lblTitulo, new Separator(), lblNome, lblDano, lblForca);
        }
        return painel;
    }

    private Region criarSeparador() {
        Region sep = new Region();
        sep.setStyle("-fx-background-color: #444; -fx-min-width: 1; -fx-max-width: 1;");
        sep.setPrefHeight(120);
        return sep;
    }

    // ------------------------------------------------------------------
    // Outros botões
    // ------------------------------------------------------------------
    @FXML
    private void abrirMapa(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/mapa.fxml");
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("MISSAO");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void abrirDiario(ActionEvent event) {
        SomUtil.tocarConfirmar();
        GameState.setOrigemInventario("MISSAO");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/diario.fxml");
    }

    @FXML
    private void abrirHabilidadesCampo(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/habilidadesCampo.fxml");
    }

    @FXML
    private void fugir(ActionEvent event) {
        SomUtil.tocarVoltar();
        // Penalidade por fuga: 10% do dinheiro + perde páginas
        if (jogador != null && jogador.getDinheiro() > 0) {
            int perda = jogador.getDinheiro() / 10;
            jogador.setDinheiro(jogador.getDinheiro() - perda);
        }
        GameState.setVeioDeFuga(true);
        GameState.setMissaoEmAndamento(false);
        GameState.setInvestigouNesteAvanco(false);
        GameState.setCombateVencidoNesteLocal(false);
        GameState.perderPaginasParcial(false);
        GameContext.salvarProgressoCampanha();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    private void atualizarTela() {
        carregarPersonagemMissao();
        if (jogador == null || missao == null) return;
        lblVida.setText(jogador.getVidaAtual() + " / " + jogador.getVidaMaxima());
        lblPE.setText(jogador.getPeAtual() + " / " + jogador.getPeMaximo());

        long paginasColetadas = missao.getLocais().stream()
                .filter(l -> !l.isBossRoom())
                .filter(LocalMapa::isPaginaEncontrada)
                .count();
        long totalNormais = missao.getLocais().stream().filter(l -> !l.isBossRoom()).count();
        lblFragmentos.setText("Páginas: " + paginasColetadas + "/" + totalNormais);
    }
}
