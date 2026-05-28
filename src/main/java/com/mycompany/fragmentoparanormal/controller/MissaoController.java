package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.CampanhaDAO;
import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.GeradorInimigoService;
import com.mycompany.fragmentoparanormal.service.InvestigacaoService;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.util.GameState;
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

import java.util.Optional;

public class MissaoController {

    private Personagem jogador;
    private Missao     missao;
    private int        indiceMissao;

    private int paginasEncontradasSessao = 0;
    private static final int PAGINAS_NECESSARIAS = 7;

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

        paginasEncontradasSessao = GameState.getPaginasMissao(indiceMissao);

        if (!GameState.isMissaoEmAndamento()) {
            if (jogador != null) jogador.resetarParaMissao();
            GameState.setMissaoEmAndamento(true);
            GameState.setInvestigouNesteAvanco(false);
        }

        if (missao != null) {
            lblNomeMissao.setText(missao.getNome());
            try {
                var stream = getClass().getResourceAsStream(missao.getImagemCenario());
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
            if (paginasEncontradasSessao < PAGINAS_NECESSARIAS) {
                paginasEncontradasSessao++;
                GameState.incrementarPaginaMissao(indiceMissao);
                String texto = obterTextoPagina(indiceMissao, paginasEncontradasSessao);
                lblEventos.setText("📖 Você encontrou uma página do diário!\n\n" + texto);
                if (GameState.totalPaginasColetadas() >= GameState.getTotalPaginasJogo()) {
                    GameState.setBossDesbloqueado(true);
                    lblEventos.setText(lblEventos.getText()
                        + "\n\n⚠ Todas as páginas coletadas! O Boss Final foi desbloqueado!");
                }
                verificarConclusao();
            } else {
                lblEventos.setText("Você já coletou todas as páginas desta missão.");
            }

        } else if (resultado.startsWith("ARMA:")) {
            // formato: "ARMA:<nome>:<bonusDano>"
            String[] partes    = resultado.split(":", 3);
            String   nomeArma  = partes[1];
            int      bonusDano = Integer.parseInt(partes[2]);
            Arma     armaAchada = new Arma(nomeArma, bonusDano);

            lblEventos.setText("⚔ Você encontrou uma arma: " + nomeArma + "!\nVerifique a comparação...");
            abrirDialogoComparacao(armaAchada);

        } else if (resultado.equals("ITEM_PARANORMAL")) {
            lblEventos.setText("✦ Você encontrou um amuleto paranormal! +5 Poder Paranormal.");

        } else {
            lblEventos.setText("Você vasculhou o local, mas não encontrou nada útil.");
        }

        atualizarTela();
        salvarProgresso();
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
        ButtonType btnDescartar = new ButtonType("Descartar",            ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnEquipar, btnDescartar);

        // Estilo dos botões
        dialog.getDialogPane().lookupButton(btnEquipar)
              .setStyle("-fx-background-color: #6c3483; -fx-text-fill: white; -fx-font-weight: bold;");
        dialog.getDialogPane().lookupButton(btnDescartar)
              .setStyle("-fx-background-color: #555; -fx-text-fill: white;");

        Optional<ButtonType> resposta = dialog.showAndWait();
        if (resposta.isPresent() && resposta.get() == btnEquipar) {
            jogador.setArmaEquipada(armaAchada);
            lblEventos.setText("⚔ " + armaAchada.getNome() + " equipada com sucesso!");
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

        Inimigo inimigo = GeradorInimigoService.gerarInimigo(jogador);
        GameContext.inimigoAtual = inimigo;
        GameState.setOrigemInventario("COMBATE");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("MISSAO");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void fugir(ActionEvent event) {
        GameState.setVeioDeFuga(true);
        GameState.setMissaoEmAndamento(false);
        GameState.setInvestigouNesteAvanco(false);
        salvarProgresso();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private void verificarConclusao() {
        if (paginasEncontradasSessao >= PAGINAS_NECESSARIAS) {
            if (missao != null) missao.concluir();
            lblEventos.setText(lblEventos.getText()
                + "\n\n✔ Missão concluída! Continue explorando ou volte ao menu via Fugir.");
            salvarProgresso();
        }
    }

    private void salvarProgresso() {
        if (jogador == null) return;
        try {
            int jogadorId = JogadorDAO.salvar(jogador);
            if (jogadorId > 0 && missao != null) {
                CampanhaDAO.salvarCampanha(
                    jogadorId,
                    missao.getElemento(),
                    missao.getNome(),
                    GameState.getPaginasMissao(indiceMissao),
                    missao.isConcluida(),
                    GameState.isBossDesbloqueado()
                );
            }
        } catch (Exception e) {
            System.err.println("[MissaoController] Erro ao salvar progresso: " + e.getMessage());
        }
    }

    private void atualizarTela() {
        if (jogador == null) return;
        lblFragmentos.setText("Páginas: " + paginasEncontradasSessao + "/" + PAGINAS_NECESSARIAS);
        lblVida.setText("Vida: " + jogador.getVida() + "/" + jogador.getVidaMaxima());
        lblPE.setText("PE: " + jogador.getPontosEsforco() + "/" + jogador.getPeMaximo());
    }

    private String obterTextoPagina(int missaoIdx, int numeroPagina) {
        String[] textosSangue = {
            "\"O hospital estava vazio quando cheguei. Mas as marcas nas paredes contavam outra história — alguém sangrou aqui por horas antes de desaparecer.\"\n— Diário, Página 1 [Sangue]",
            "\"Encontrei um prontuário médico rasgado. O paciente respondia por 'Sujeito Alfa'. Último registro: 'estado irreversível'. Nenhuma data.\"\n— Diário, Página 2 [Sangue]",
            "\"As criaturas daqui se alimentam de memória — não de carne. Cada ferida que causam apaga um fragmento de quem você é.\"\n— Diário, Página 3 [Sangue]",
            "\"Lívia disse que a medicina e o paranormal não eram diferentes. Ambos tentavam consertar o que não deveria estar quebrado. Ela estava certa — da pior forma.\"\n— Diário, Página 4 [Sangue]",
            "\"O elemento Sangue não representa violência. Representa vínculo. Quem o invoca se conecta a tudo que já viveu — e a tudo que perdeu.\"\n— Diário, Página 5 [Sangue]",
            "\"Vi meu próprio reflexo numa poça de sangue seco. Ele piscou antes de mim.\"\n— Diário, Página 6 [Sangue]",
            "\"Saí do hospital com mais dúvidas do que respostas. Mas levei algo comigo que não estava lá quando entrei. Algo que ainda não consigo nomear.\"\n— Diário, Página 7 [Sangue]"
        };
        String[] textosMorte = {
            "\"O cemitério tem lápides sem nome. Centenas delas. Alguém as colocou aqui deliberadamente — para honrar os que não podiam ser lembrados.\"\n— Diário, Página 1 [Morte]",
            "\"Corvin era coveiro antes de se tornar agente. Dizia que a morte não era um fim — era uma mudança de endereço. Sinto falta do cinismo dele.\"\n— Diário, Página 2 [Morte]",
            "\"As criaturas aqui não atacam por instinto. Elas esperam. Observam. Escolhem o momento em que você baixa a guarda.\"\n— Diário, Página 3 [Morte]",
            "\"Encontrei flores frescas sobre uma das lápides sem nome. Alguém ainda vem aqui. Alguém ainda lembra.\"\n— Diário, Página 4 [Morte]",
            "\"O elemento Morte não destrói. Ele preserva o que foi, exatamente como era no último momento.\"\n— Diário, Página 5 [Morte]",
            "\"Ouvi passos no necrotério. Eram meus próprios — ecoando de dez minutos atrás.\"\n— Diário, Página 6 [Morte]",
            "\"Deixei o cemitério ao amanhecer. As lápides sem nome tinham nomes agora. Todos iguais. Todos o meu.\"\n— Diário, Página 7 [Morte]"
        };
        String[] textosEnergia = {
            "\"O laboratório ainda tem eletricidade — mas nenhuma fonte de energia conhecida está conectada.\"\n— Diário, Página 1 [Energia]",
            "\"Mara acreditava que o paranormal era física mal compreendida. Seus experimentos a consumiram.\"\n— Diário, Página 2 [Energia]",
            "\"As distorções aqui dobram o tempo localmente. Entrei às 14h. A vela já está completamente consumida.\"\n— Diário, Página 3 [Energia]",
            "\"Os inimigos de energia não causam dano físico. Eles drenam — PE, força de vontade, certeza.\"\n— Diário, Página 4 [Energia]",
            "\"Encontrei as anotações de Mara. A última termina no meio de uma frase.\"\n— Diário, Página 5 [Energia]",
            "\"As luzes piscaram em código Morse. Traduzi: 'VOCÊ JÁ ESTEVE AQUI ANTES'.\"\n— Diário, Página 6 [Energia]",
            "\"Saí do laboratório carregando uma carga estática que não dissipa. Toda tela que toco mostra o rosto de Mara.\"\n— Diário, Página 7 [Energia]"
        };
        String[] textosConhecimento = {
            "\"A biblioteca parece normal à primeira vista. Mas todos os livros têm o mesmo número de páginas. Exatamente 312. Todos.\"\n— Diário, Página 1 [Conhecimento]",
            "\"Os cultos que operaram aqui adoravam informação. Acreditavam que saber o suficiente tornava qualquer coisa possível.\"\n— Diário, Página 2 [Conhecimento]",
            "\"Encontrei documentos proibidos selados com cera negra. O símbolo era o mesmo dos quatro círculos conectados.\"\n— Diário, Página 3 [Conhecimento]",
            "\"O elemento Conhecimento não dá poder diretamente. Ele mostra onde o poder já existe — e como tomá-lo.\"\n— Diário, Página 4 [Conhecimento]",
            "\"Li o suficiente para entender o que o autor do diário estava tentando fazer.\"\n— Diário, Página 5 [Conhecimento]",
            "\"A última seção da biblioteca está trancada por dentro. Empurrei a porta. Ela abriu. Não havia ninguém.\"\n— Diário, Página 6 [Conhecimento]",
            "\"Encontrei o espelho. Vi o rosto do autor. Era alguém da Ordem — alguém que me enviou para cá.\n\nEle sabia. Ele sempre soube.\"\n— Diário, Página 7 [Conhecimento] ⚠ ÚLTIMA PÁGINA"
        };

        String[] textos = switch (missaoIdx) {
            case 0 -> textosSangue;
            case 1 -> textosMorte;
            case 2 -> textosEnergia;
            case 3 -> textosConhecimento;
            default -> textosSangue;
        };
        int idx = Math.min(numeroPagina - 1, textos.length - 1);
        return textos[idx];
    }
}
