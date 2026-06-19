package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.CampanhaDAO;
import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.service.GeradorInimigoService;
import com.mycompany.fragmentoparanormal.service.InvestigacaoService;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

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
    @FXML private Button    btnInvestigar;
    @FXML private Button    btnAvancar;

    @FXML
    public void initialize() {
        jogador      = GameContext.jogadorAtual;
        missao       = GameState.getMissaoAtual();
        indiceMissao = missao != null
                ? MissaoService.getIndiceMissao(missao.getElemento()) : 0;

        if (!GameState.isMissaoEmAndamento()) {
            if (jogador != null) jogador.resetarParaMissao();
            GameState.setMissaoEmAndamento(true);
            GameState.setInvestigouNesteAvanco(false);
        }

        if (missao != null) {
            lblNomeMissao.setText(missao.getNome());
            try {
                var stream = getClass().getResourceAsStream(missao.getLocalAtualObj().getCaminhoImagem());
                if (stream != null) imgCena.setImage(new Image(stream));
            } catch (Exception e) {
                System.err.println("Cenário não encontrado: " + missao.getImagemCenario());
            }
        }

        btnInvestigar.setDisable(GameState.isInvestigouNesteAvanco());
        atualizarTela();
    }

    // ------------------------------------------------------------------
    // Investigar — 1x por avanço
    // ------------------------------------------------------------------
    @FXML
    private void investigar() {
        if (jogador == null || GameState.isInvestigouNesteAvanco()) return;

        GameState.setInvestigouNesteAvanco(true);
        btnInvestigar.setDisable(true);

        String resultado = InvestigacaoService.investigar(jogador);

        if (resultado.equals("FRAGMENTO")) {
                LocalMapa localAtual = missao.getLocalAtualObj();
                if (localAtual != null && !localAtual.isPaginaEncontrada()) {
                    localAtual.setPaginaEncontrada(true);
                    GameState.registrarPagina(indiceMissao, localAtual.getOrdem() + 1); // +1 porque a ordem começa em 0

                jogador.ganharXp(150); // XP por página de diário — aumentado para facilitar progressao
                String texto = GameState.getTextoPagina(indiceMissao, localAtual.getOrdem() + 1);
                lblEventos.setText("📖 Você encontrou uma página do diário!\n\n" + texto);
                if (missao.getLocais().stream().filter(LocalMapa::isPaginaEncontrada).count() == 7) {
                    GameState.setBossDesbloqueado(true);
                    lblEventos.setText(lblEventos.getText()
                        + "\n\n⚠ Todos os locais investigados! A Sala do Boss foi desbloqueada!");
                }
                // Não precisa de verificarConclusao() aqui, pois a lógica de avanço é pelo mapa
                // O botão de avançar para o próximo local será habilitado no mapa se a página for encontrada
                // E o botão de avançar para o boss será habilitado no mapa se todas as páginas forem encontradas
                
            } else {
                lblEventos.setText("Você já coletou todas as páginas desta missão.");
            }

        } else if (resultado.equals("INIMIGO_SURPRESA")) {
            lblEventos.setText("💀 Um inimigo surge do nada! Prepare-se para combater!");
            // Lança combate imediatamente
            Inimigo inimigo = GeradorInimigoService.gerarInimigo(jogador);
            GameContext.inimigoAtual = inimigo;
            GameState.setOrigemInventario("COMBATE");
            GameState.setInvestigouNesteAvanco(false); // volta após o combate
            javafx.application.Platform.runLater(() -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) lblEventos.getScene().getWindow();
                try {
                    javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/com/mycompany/fragmentoparanormal/view/combate.fxml"));
                    javafx.scene.Parent root = loader.load();
                    stage.setScene(new javafx.scene.Scene(root, 800, 600));
                } catch (Exception e) { e.printStackTrace(); }
            });

        } else if (resultado.startsWith("EVENTO_RUIM:")) {
            String desc = InvestigacaoService.descricaoEventoRuim(resultado);

            lblEventos.setText(desc);

        } else if (resultado.startsWith("ARMA:")) {
            String[] partes   = resultado.split(":", 3);
            String   nomeArma = partes[1];
            int      bonusDano = Integer.parseInt(partes[2]);
            Arma     armaAchada = new Arma(nomeArma, bonusDano);

            lblEventos.setText("⚔ Você encontrou uma arma: " + nomeArma + "!\nVerifique a comparação...");
            abrirDialogoComparacao(armaAchada);

        } else if (resultado.startsWith("ITEM_MAGICO:")) {

            int idxItem = Integer.parseInt(resultado.split(":")[1]);
            Arma itemAchado = InvestigacaoService.ARMAS_LOOT[idxItem];
            lblEventos.setText("✦ Item encontrado!\nGuardado no inventário.");

        } else if (resultado.startsWith("ITEM_CONSUMIVEL:") || resultado.startsWith("ITEM_PERMANENTE:")) {

            String nomeItem = resultado.split(":", 2)[1];
            lblEventos.setText("🎒 Item encontrado: " + nomeItem + "\nGuardado no inventário.");

        } else if (resultado.startsWith("ITEM_ARTEFATO:")) {

            String nomeArtefato = resultado.split(":", 2)[1];
            lblEventos.setText("🔮 Artefato encontrado: " + nomeArtefato
                + "\nGuardado no inventário de artefatos.");

        } else if (resultado.equals("PISTA_RARA")) {
            jogador.ganharXp(80); // XP por pista rara — aumentado para facilitar progressao
            lblEventos.setText("🔍 Você encontrou uma pista rara! +80 XP.");

        } else {
            lblEventos.setText("Você vasculhou o local, mas não encontrou nada útil.\nContinue investigando ou avance para o próximo local.");
        }

        atualizarTela();
        GameContext.salvarProgressoCampanha();
    }

    // ------------------------------------------------------------------
    // Dialog de comparação de armas
    // ------------------------------------------------------------------
    private void abrirDialogoComparacao(Arma armaAchada) {
        Arma armaAtual = jogador.getArmaEquipada();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("⚔ Arma Encontrada!");
        dialog.setHeaderText("Você encontrou uma arma. Deseja equipá-la?");

        // --- Layout com dois painéis lado a lado ---
        HBox conteudo = new HBox(30);
        conteudo.setPadding(new Insets(20));
        conteudo.setAlignment(Pos.CENTER);

        conteudo.getChildren().addAll(
            painelArma("Arma Atual", armaAtual, "#2c3e50"),
            criarSeparador(),
            painelArma("Arma Encontrada", armaAchada, "#6c3483")
        );

        // Comparação de dano — destaca qual é melhor
        int danoAtual   = armaAtual   != null ? armaAtual.getBonusDano()   : 0;
        int danoAchada  = armaAchada.getBonusDano();
        String comparacao;
        if (danoAchada > danoAtual) {
            comparacao = "✅ A arma encontrada é mais forte! (+" + (danoAchada - danoAtual) + " de dano)";
        } else if (danoAchada < danoAtual) {
            comparacao = "⚠ Sua arma atual é mais forte. (" + (danoAtual - danoAchada) + " a menos)";
        } else {
            comparacao = "➖ Mesma força de dano.";
        }
        Label lblComparacao = new Label(comparacao);
        lblComparacao.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        lblComparacao.setPadding(new Insets(8, 0, 0, 0));

        VBox raiz = new VBox(10, conteudo, lblComparacao);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(10, 20, 10, 20));
        raiz.setStyle("-fx-background-color: #1a1a2e;");

        dialog.getDialogPane().setContent(raiz);
        dialog.getDialogPane().setStyle("-fx-background-color: #1a1a2e;");

        // Botões
        ButtonType btnEquipar   = new ButtonType("Equipar Nova Arma",   ButtonBar.ButtonData.OK_DONE);
        ButtonType btnGuardar   = new ButtonType("Guardar no Inventário", ButtonBar.ButtonData.OTHER);
        ButtonType btnDescartar = new ButtonType("Descartar",              ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEquipar, btnGuardar, btnDescartar);

        // Estilo dos botões
        dialog.getDialogPane().lookupButton(btnEquipar)
              .setStyle("-fx-background-color: #6c3483; -fx-text-fill: white; -fx-font-weight: bold;");
        dialog.getDialogPane().lookupButton(btnGuardar)
              .setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-weight: bold;");
        dialog.getDialogPane().lookupButton(btnDescartar)
              .setStyle("-fx-background-color: #555; -fx-text-fill: white;");

        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isPresent() && resposta.get() == btnEquipar) {
            // Manda arma anterior pro inventário (não perde)
            Arma anterior = jogador.getArmaEquipada();
            if (anterior != null) jogador.adicionarArma(anterior);
            jogador.setArmaEquipada(armaAchada);
            lblEventos.setText("⚔ " + armaAchada.getNome() + " equipada! Arma anterior guardada no inventário.");
        } else if (resposta.isPresent() && resposta.get() == btnGuardar) {
            // Guarda a nova arma no inventário de armas
            jogador.adicionarArma(armaAchada);
            lblEventos.setText("⚔ " + armaAchada.getNome() + " guardada no inventário de armas.");
        } else {
            lblEventos.setText("⚔ " + armaAchada.getNome() + " descartada. Você mantém sua arma atual.");
        }
        atualizarTela();
    }

    /** Cria um painel visual para exibir uma arma. */
    private VBox painelArma(String titulo, Arma arma, String corBorda) {
        VBox painel = new VBox(8);
        painel.setAlignment(Pos.CENTER_LEFT);
        painel.setPadding(new Insets(14));
        painel.setMinWidth(180);
        painel.setStyle(
            "-fx-background-color: #16213e;" +
            "-fx-border-color: " + corBorda + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );

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

    /** Linha vertical separando os dois painéis. */
    private Region criarSeparador() {
        Region sep = new Region();
        sep.setStyle("-fx-background-color: #444; -fx-min-width: 1; -fx-max-width: 1;");
        sep.setPrefHeight(120);
        return sep;
    }

    // ------------------------------------------------------------------
    // Avançar
    // ------------------------------------------------------------------
    @FXML
    private void avancar(ActionEvent event) {
        if (jogador == null) return;
        GameState.setInvestigouNesteAvanco(false);
        GameState.setMissaoEmAndamento(true);
        btnInvestigar.setDisable(false);

        // Em vez de gerar combate aleatório, o botão "Avançar" agora abre o mapa
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/mapa.fxml");
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("MISSAO");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void abrirDiario(ActionEvent event) {
        GameState.setOrigemInventario("MISSAO");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/diario.fxml");
    }

    @FXML
    private void abrirHabilidadesCampo(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/habilidadesCampo.fxml");
    }

    /** Verifica se há escolha pendente e redireciona para a tela de escolha. */
    private void verificarEscolhaPendente(ActionEvent event) {
        if (jogador != null && jogador.isEscolhaPendente()) {
            EscolhaHabilidadeController.telaOrigem = "MISSAO";
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
        }
    }

    @FXML
    private void fugir(ActionEvent event) {
        GameState.setVeioDeFuga(true);
        GameState.setMissaoEmAndamento(false);
        GameState.setInvestigouNesteAvanco(false);
        GameState.perderPaginasParcial(false);
        GameContext.salvarProgressoCampanha();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/qg.fxml");
    }

    private void atualizarTela() {
        lblVida.setText(jogador.getVidaAtual() + " / " + jogador.getVidaMaxima());
        lblPE.setText(jogador.getPeAtual() + " / " + jogador.getPeMaximo());
        lblFragmentos.setText(String.valueOf(missao.getLocais().get(missao.getLocalAtual()).getPaginasEncontradasNoLocal().size()));
    }
}
