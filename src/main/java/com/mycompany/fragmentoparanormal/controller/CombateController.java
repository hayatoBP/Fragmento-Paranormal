package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.CombateService;
import com.mycompany.fragmentoparanormal.service.ElementoService;
import com.mycompany.fragmentoparanormal.service.RitualService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CombateController {

    private Personagem jogador;
    private Inimigo inimigo;
    private boolean combateEncerrado = false;

    @FXML private Label lblNomeInimigo;
    @FXML private Label lblVidaJogador;
    @FXML private Label lblPEJogador;
    @FXML private Label lblVidaInimigo;
    @FXML private Label lblElementoInimigo;
    @FXML private Label lblEfetividade;    // mostra "SUPER EFETIVO!", "Não muito efetivo..." etc.
    @FXML private Label lblEventos;
    @FXML private ImageView imgJogador;
    @FXML private ImageView imgInimigo;
    @FXML private Button btnAtacar;
    @FXML private Button btnRitual;
    @FXML private Button btnFugir;
    @FXML private Button btnVoltarMissao;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        inimigo = GameContext.inimigoAtual;
        btnVoltarMissao.setVisible(false);
        lblEfetividade.setText("");
        carregarImagens();
        atualizarTela();

        if (inimigo != null && inimigo.isBoss()) {
            lblNomeInimigo.setText("⚠ BOSS FINAL: " + inimigo.getNome());
            lblEventos.setText("O Quarto Anfitrião emerge das sombras...\n\"Bem-vindo ao ritual, agente.\"");
        }
    }

    private void carregarImagens() {
        try {
            if (jogador != null) {
                var s = getClass().getResourceAsStream(jogador.getImagemAtual());
                if (s != null) imgJogador.setImage(new Image(s));
            }
            if (inimigo != null) {
                var s = getClass().getResourceAsStream(inimigo.getImagem());
                if (s != null) imgInimigo.setImage(new Image(s));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }
    }

    @FXML
    private void atacar(ActionEvent event) {
        if (combateEncerrado || jogador == null || inimigo == null) return;

        // Dano físico + multiplicador elemental do jogador vs inimigo
        int danoBase = jogador.calcularDanoFisico();
        double mult = ElementoService.calcularMultiplicador(jogador.getElemento(), inimigo.getElemento());
        int danoFinal = (int)(danoBase * mult);
        inimigo.setVida(inimigo.getVida() - danoFinal);

        String efetividade = descreveEfetividade(mult);
        lblEfetividade.setText(efetividade);

        if (!inimigo.estaVivo()) {
            jogador.ganharXp(inimigo.getXpConcedido());
            String extra = inimigo.isBoss() ? "\n\n🎉 Parabéns! Você completou Fragmento Paranormal!" : "";
            encerrarCombate("Você derrotou o inimigo! +" + inimigo.getXpConcedido() + " XP." + extra);
            return;
        }

        // Contra-ataque do inimigo — MEDO aplica 2x
        int danoInimigo = aplicarDanoInimigo();
        lblEventos.setText("Você atacou por " + danoFinal + " de dano" + efetividade
                + ". O inimigo contra-atacou por " + danoInimigo + ".");

        if (!jogador.estaVivo()) { encerrarDerrota(); return; }
        atualizarTela();
    }

    @FXML
    private void usarRitual(ActionEvent event) {
        if (combateEncerrado || jogador == null || inimigo == null) return;

        boolean conseguiu = RitualService.usarRitual(jogador, inimigo);
        if (!conseguiu) {
            lblEventos.setText("Ritual falhou! PE insuficiente ou nenhum ritual equipado.");
            lblEfetividade.setText("");
            return;
        }

        // Multiplicador elemental do ritual vs inimigo
        double mult = jogador.getRitualEquipado() != null
                ? ElementoService.calcularMultiplicador(jogador.getRitualEquipado().getElemento(), inimigo.getElemento())
                : 1.0;
        String efetividade = descreveEfetividade(mult);
        lblEfetividade.setText(efetividade);

        if (!inimigo.estaVivo()) {
            jogador.ganharXp(inimigo.getXpConcedido());
            String extra = inimigo.isBoss() ? "\n\n🎉 Parabéns! Você completou Fragmento Paranormal!" : "";
            encerrarCombate("Ritual devastador! Inimigo derrotado! +" + inimigo.getXpConcedido() + " XP." + extra);
            return;
        }

        int danoInimigo = aplicarDanoInimigo();
        lblEventos.setText("Ritual usado" + efetividade + ". O inimigo contra-atacou por " + danoInimigo + ".");

        if (!jogador.estaVivo()) { encerrarDerrota(); return; }
        atualizarTela();
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    /** Fugir do combate → sinaliza fuga → tela de status com mensagem especial */
    @FXML
    private void fugir(ActionEvent event) {
        GameState.setVeioDeFuga(true);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    @FXML
    private void voltarMissao(ActionEvent event) {
        // Ao voltar da batalha, reseta vida/PE
        if (jogador != null) jogador.resetarParaMissao();
        if (inimigo != null && inimigo.isBoss()) {
            // Boss derrotado → vai ao menu
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    // ---- helpers ----

    /** Aplica o dano do inimigo ao jogador e retorna o valor aplicado. */
    private int aplicarDanoInimigo() {
        double multInimigo = ElementoService.calcularMultiplicador(inimigo.getElemento(), jogador.getElemento());
        int dano = (int)(inimigo.getDano() * multInimigo);
        jogador.setVida(jogador.getVida() - dano);
        return dano;
    }

    private void encerrarCombate(String mensagem) {
        combateEncerrado = true;
        lblEventos.setText(mensagem);
        atualizarTela();
        btnAtacar.setDisable(true);
        btnRitual.setDisable(true);
        btnFugir.setDisable(true);
        btnVoltarMissao.setVisible(true);
    }

    private void encerrarDerrota() {
        combateEncerrado = true;
        lblEventos.setText("Você foi derrotado...");
        lblEfetividade.setText("");
        atualizarTela();
        btnAtacar.setDisable(true);
        btnRitual.setDisable(true);
        btnFugir.setDisable(true);
        // Derrota → tela de status sem mensagem de fuga
        GameState.setVeioDeFuga(false);
        TelaUtil.trocarTelaPorNode(lblEventos, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    private String descreveEfetividade(double mult) {
        if (mult >= 2.0) return " ⚠ TERROR ABSOLUTO!";
        if (mult >= 1.5) return " ✦ Super efetivo!";
        if (mult <= 0.5) return " ▼ Não muito efetivo...";
        return "";
    }

    private void atualizarTela() {
        if (jogador == null || inimigo == null) return;
        lblVidaJogador.setText("Vida: " + Math.max(0, jogador.getVida()) + "/" + jogador.getVidaMaxima());
        lblPEJogador.setText("PE: " + jogador.getPontosEsforco() + "/" + jogador.getPeMaximo());
        lblVidaInimigo.setText("Vida: " + Math.max(0, inimigo.getVida()));
        lblElementoInimigo.setText("Elemento: " + inimigo.getElemento());
        if (lblNomeInimigo != null && !inimigo.isBoss()) lblNomeInimigo.setText(inimigo.getNome());
    }
}
