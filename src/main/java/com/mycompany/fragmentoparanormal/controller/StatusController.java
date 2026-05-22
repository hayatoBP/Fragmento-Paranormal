package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class StatusController {

    private Personagem jogador;

    @FXML private Label lblTitulo;
    @FXML private Label lblMensagemFuga;   // visível apenas quando veio de fuga
    @FXML private Button btnConfirmarFuga; // visível apenas quando veio de fuga
    @FXML private Label lblPontos;
    @FXML private Label lblForca;
    @FXML private Label lblInvestigacao;
    @FXML private Label lblPoderParanormal;
    @FXML private Label lblVida;
    @FXML private Label lblPE;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;

        boolean fuga = GameState.isVeioDeFuga();

        if (fuga) {
            // Exibe painel de fuga
            lblTitulo.setText("Retorno à Ordem");
            lblMensagemFuga.setText(
                "Uma noite difícil...\nVocê teve que voltar para a Ordem."
            );
            lblMensagemFuga.setVisible(true);
            btnConfirmarFuga.setVisible(true);
        } else {
            // Veio de derrota ou conclusão de missão — fluxo normal
            lblTitulo.setText("Distribuição de Status");
            lblMensagemFuga.setVisible(false);
            btnConfirmarFuga.setVisible(false);
        }

        atualizarTela();
    }

    // Botão exclusivo da fuga — confirma e vai ao menu
    @FXML
    private void confirmarFuga(ActionEvent event) {
        GameState.setVeioDeFuga(false);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    @FXML
    private void adicionarForca() {
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setForca(jogador.getForca() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    @FXML
    private void adicionarInvestigacao() {
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setInvestigacao(jogador.getInvestigacao() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    @FXML
    private void adicionarPoderParanormal() {
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setPoderParanormal(jogador.getPoderParanormal() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    @FXML
    private void confirmar(ActionEvent event) {
        GameState.setVeioDeFuga(false);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    private void atualizarTela() {
        if (jogador == null) return;
        lblPontos.setText("Pontos disponíveis: " + jogador.getPontosAtributo());
        lblForca.setText("Força: " + jogador.getForca());
        lblInvestigacao.setText("Investigação: " + jogador.getInvestigacao());
        lblPoderParanormal.setText("Poder Paranormal: " + jogador.getPoderParanormal());
        lblVida.setText("Vida máxima: " + jogador.getVidaMaxima());
        lblPE.setText("PE máximo: " + jogador.getPeMaximo());
    }
}
