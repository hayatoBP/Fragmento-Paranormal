package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Habilidade;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class HabilidadesCampoController {

    private Personagem jogador;

    @FXML private ListView<Habilidade> listaHabilidades;
    @FXML private Label lblNome;
    @FXML private Label lblDescricao;
    @FXML private Label lblEfeito;
    @FXML private Label lblPE;
    @FXML private Label lblStatus;
    @FXML private Button btnUsar;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        jogador = GameContext.jogadorAtual;
        if (jogador == null) return;

        List<Habilidade> campo = jogador.getHabilidades().stream()
            .filter(Habilidade::isHabilidadeCampo)
            .toList();

        listaHabilidades.getItems().setAll(campo);

        listaHabilidades.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Habilidade h, boolean empty) {
                super.updateItem(h, empty);
                if (empty || h == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(h.getNome() + "  (" + h.getCustoPE() + " PE)");
                setStyle("-fx-text-fill: #f5cba7; -fx-font-size: 13px; -fx-background-color: transparent;");
            }
        });

        listaHabilidades.getSelectionModel().selectedItemProperty().addListener(
            (obs, ant, sel) -> mostrarDetalhes(sel)
        );

        if (!campo.isEmpty()) listaHabilidades.getSelectionModel().selectFirst();
        limparStatus();
    }

    private void mostrarDetalhes(Habilidade h) {
        if (h == null) { btnUsar.setDisable(true); return; }
        lblNome.setText(h.getNome());
        lblDescricao.setText(h.getDescricao());
        lblEfeito.setText(h.getEfeito());
        lblPE.setText("Custo: " + h.getCustoPE() + " PE  (você tem " + jogador.getPontosEsforco() + ")");
        btnUsar.setDisable(jogador.getPontosEsforco() < h.getCustoPE());
        limparStatus();
    }

    @FXML
    private void usarHabilidade() {
        SomUtil.tocarConfirmar();
        Habilidade h = listaHabilidades.getSelectionModel().getSelectedItem();
        if (h == null) return;

        if (jogador.getPontosEsforco() < h.getCustoPE()) {
            lblStatus.setText("✕ PE insuficiente!");
            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
            return;
        }

        jogador.setPontosEsforco(jogador.getPontosEsforco() - h.getCustoPE());
        aplicarEfeito(h);

        lblPE.setText("Custo: " + h.getCustoPE() + " PE  (você tem " + jogador.getPontosEsforco() + ")");
        btnUsar.setDisable(true);
    }

    private void aplicarEfeito(Habilidade h) {
        switch (h.getArvore()) {
            case INVESTIGACAO -> {
                int nivel = switch (h.getTipoHabilidade()) {
                    case FRACA -> 1; case MEDIA -> 2; default -> 3;
                };
                jogador.ativarBonusInvestigacao(nivel);
                lblStatus.setText("✔ " + h.getNome() + " ativada! Próxima investigação será melhorada.");
                lblStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
            }
            case PREPARACAO -> {
                double multDano = h.getMultiplicadorDano();
                double redDano  = h.getReducaoDano();
                jogador.aplicarBonusPreparacao(multDano, redDano);
                lblStatus.setText("✔ " + h.getNome() + " ativada! Próxima batalha terá bônus de +"
                    + (int)((multDano - 1.0) * 100) + "% dano"
                    + (redDano > 0 ? " e -" + (int)(redDano * 100) + "% dano recebido" : "") + ".");
                lblStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
            }
            default -> {
                lblStatus.setText("✔ " + h.getNome() + " usada.");
                lblStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 13px;");
            }
        }
    }

    private void limparStatus() {
        lblStatus.setText("");
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
    }
}
