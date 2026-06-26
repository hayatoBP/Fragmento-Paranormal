package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class MenuMissoesController {

    @FXML private Label lblProgresso;
    @FXML private ListView<Missao> listaMissoes;
    @FXML private Button btnBoss;
    @FXML private Button btnComecar;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        // Se o jogador upou e tem escolha pendente, abre tela de escolha imediatamente
        if (GameContext.jogadorAtual != null && GameContext.jogadorAtual.isEscolhaPendente()) {
            EscolhaHabilidadeController.telaOrigem = "MISSAO";
            javafx.application.Platform.runLater(() -> {
                try {
                    com.mycompany.fragmentoparanormal.util.TelaUtil.trocarTelaPorNode(
                        btnBoss,
                        "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
                } catch (Exception ex) {
                    System.err.println("Erro ao abrir escolha de habilidade: " + ex.getMessage());
                }
            });
            return;
        }

        List<Missao> missoes = MissaoService.carregarMissoes();
        listaMissoes.setItems(FXCollections.observableArrayList(missoes));
        // Célula com ícone do elemento + nome + status
        listaMissoes.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            private final javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView();
            { iv.setFitWidth(22); iv.setFitHeight(22); iv.setPreserveRatio(true); }
            @Override protected void updateItem(com.mycompany.fragmentoparanormal.model.Missao m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) { setGraphic(null); setText(null); return; }
                String elem = m.getElemento().toString().toLowerCase();
                String ext  = ".png";
                try {
                    var s = getClass().getResourceAsStream(
                        "/com/mycompany/fragmentoparanormal/images/simbolos/" + elem + ext);
                    iv.setImage(s != null ? new javafx.scene.image.Image(s) : null);
                } catch (Exception e) { iv.setImage(null); }
                String status = m.isConcluida() ? " ✔" : "";
                setGraphic(iv);
                setText("  " + m.getNome() + status);
                setStyle("-fx-font-size: 14px; -fx-text-fill: " + (m.isConcluida() ? "#888;" : "white;"));
            }
        });

        // Progresso geral
        long paginas = missoes.stream()
                .flatMap(m -> m.getLocais().stream())
                .filter(LocalMapa::isPaginaEncontrada)
                .count();
        lblProgresso.setText("Páginas do diário: " + paginas + "/24");

        // Boss final só aparece se todas as 4 missões concluídas
        boolean bossDisponivel = GameState.isBossDesbloqueado();
        btnBoss.setVisible(bossDisponivel);
        btnBoss.setManaged(bossDisponivel);
    }

    @FXML
    private void comecarMissao(ActionEvent event) {
        SomUtil.tocarConfirmar();
        Missao selecionada = listaMissoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            mostrarAviso("Selecione uma missão da lista.");
            return;
        }

        // Verificar trava de progressão: missões devem ser feitas em ordem
        List<Missao> missoes = MissaoService.carregarMissoes();
        for (Missao m : missoes) {
            if (m == selecionada) break; // chegou na selecionada, todas anteriores OK
            if (!m.isConcluida()) {
                mostrarAviso("Você precisa concluir \"" + m.getNome() + "\" antes de jogar esta missão.");
                return;
            }
        }

        GameState.setMissaoAtual(selecionada);
        GameState.setMissaoEmAndamento(false);
        GameState.setCombateVencidoNesteLocal(false);
        GameState.setInvestigouNesteAvanco(false);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/dialogo.fxml");
    }

    @FXML
    private void abrirLoja(ActionEvent event) {
        SomUtil.tocarConfirmar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/loja.fxml");
    }

    @FXML
    private void irParaBoss(ActionEvent event) {
        SomUtil.tocarConfirmar();
        if (!GameState.isBossDesbloqueado()) {
            mostrarAviso("Conclua todas as missões para enfrentar o Boss Final.");
            return;
        }
        // Prepara o boss final como inimigo atual
        GameContext.inimigoAtual = com.mycompany.fragmentoparanormal.model.Inimigo.criarBossFinal();
        GameState.setOrigemInventario("COMBATE");

        // Cria missão fictícia com o diálogo final da Ordem
        com.mycompany.fragmentoparanormal.model.Missao missaoFinal =
            new com.mycompany.fragmentoparanormal.model.Missao(
                "Instalação Gênese",
                com.mycompany.fragmentoparanormal.util.Elemento.MEDO,
                null, "dialogo_final.txt", 0, -1);
        GameState.setMissaoAtual(missaoFinal);

        // Após o diálogo, vai direto para o combate
        com.mycompany.fragmentoparanormal.controller.DialogoController.destinoPosDialogo = "COMBATE_FINAL";

        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/dialogo.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
