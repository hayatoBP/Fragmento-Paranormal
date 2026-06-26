package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.LojaService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Tela de distribuição de atributos.
 *
 * Também exibe mensagens de penalidade ao fugir ou ser derrotado:
 *  - Perde páginas da missão atual.
 *  - Perde 25% do dinheiro atual (mínimo 0).
 *  - Mantém nível, evolução e itens.
 *  - Ao morrer: a loja também é resetada.
 */
public class StatusController {

    private Personagem jogador;

    // Rastreia os valores ANTES de distribuir para permitir o botão (-)
    private int forcaOriginal;
    private int investigacaoOriginal;
    private int poderParanormalOriginal;

    @FXML private Label  lblTitulo;
    @FXML private Label  lblMensagemFuga;
    @FXML private Button btnConfirmarFuga;
    @FXML private Label  lblPontos;
    @FXML private Label  lblForca;
    @FXML private Label  lblInvestigacao;
    @FXML private Label  lblPoderParanormal;
    @FXML private Label  lblVida;
    @FXML private Label  lblPE;
    @FXML private Label  lblNivel;
    @FXML private Label  lblDinheiro;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        jogador = GameContext.jogadorAtual;

        boolean fuga    = GameState.isVeioDeFuga();
        boolean morreu  = GameState.isVeioDeDerrota();

        if (fuga || morreu) {
            // Penalidades já foram aplicadas em CombateController.finalizarCombate() ou MissaoController.fugir()
            // Aqui apenas mostramos a mensagem e cuidamos da loja

            // Ao morrer ou fugir: reseta a loja também
            if (morreu) {
                LojaService.resetar();
            }

            if (morreu) {
                lblTitulo.setText("Você foi derrotado");
                lblMensagemFuga.setText(
                    "A escuridão te consumiu...\n\n" +
                    "⚠  Páginas desta missão foram perdidas.\n" +
                    "💰  25% do seu dinheiro foi perdido.\n" +
                    "🛒  A loja foi resetada.\n" +
                    "✔  Seu nível, atributos e itens permanecem."
                );
            } else {
                lblTitulo.setText("Retorno à Ordem");
                lblMensagemFuga.setText(
                    "Uma noite difícil...\nVocê teve que voltar para a Ordem.\n\n" +
                    "⚠  Páginas desta missão foram perdidas.\n" +
                    "💰  10% do seu dinheiro foi perdido.\n" +
                    "✔  Seu nível, atributos, itens e loja permanecem."
                );
            }

            GameState.setVeioDeDerrota(false);
            lblMensagemFuga.setVisible(true);
            btnConfirmarFuga.setVisible(true);
        } else {
            lblTitulo.setText("Distribuição de Status");
            lblMensagemFuga.setVisible(false);
            btnConfirmarFuga.setVisible(false);
        }

        // Salvar valores base para o botão (-)
        if (jogador != null) {
            forcaOriginal            = jogador.getForca();
            investigacaoOriginal     = jogador.getInvestigacao();
            poderParanormalOriginal  = jogador.getPoderParanormal();
        }

        atualizarTela();
    }

    @FXML
    private void confirmarFuga(ActionEvent event) {
        SomUtil.tocarConfirmar();
        GameState.setVeioDeFuga(false);
        // Se o jogador tiver escolha de habilidade pendente, redireciona para ela primeiro
        if (GameContext.jogadorAtual != null && GameContext.jogadorAtual.isEscolhaPendente()) {
            EscolhaHabilidadeController.telaOrigem = "STATUS";
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        }
    }

    // ── Botões (+) ────────────────────────────────────────────────────

    @FXML
    private void adicionarForca() {
        SomUtil.tocarConfirmar();
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setForca(jogador.getForca() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    @FXML
    private void adicionarInvestigacao() {
        SomUtil.tocarConfirmar();
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setInvestigacao(jogador.getInvestigacao() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    @FXML
    private void adicionarPoderParanormal() {
        SomUtil.tocarConfirmar();
        if (jogador != null && jogador.getPontosAtributo() > 0) {
            jogador.setPoderParanormal(jogador.getPoderParanormal() + 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() - 1);
        }
        atualizarTela();
    }

    // ── Botões (-) ────────────────────────────────────────────────────

    @FXML
    private void removerForca() {
        SomUtil.tocarVoltar();
        if (jogador != null && jogador.getForca() > forcaOriginal) {
            jogador.setForca(jogador.getForca() - 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() + 1);
        }
        atualizarTela();
    }

    @FXML
    private void removerInvestigacao() {
        SomUtil.tocarVoltar();
        if (jogador != null && jogador.getInvestigacao() > investigacaoOriginal) {
            jogador.setInvestigacao(jogador.getInvestigacao() - 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() + 1);
        }
        atualizarTela();
    }

    @FXML
    private void removerPoderParanormal() {
        SomUtil.tocarVoltar();
        if (jogador != null && jogador.getPoderParanormal() > poderParanormalOriginal) {
            jogador.setPoderParanormal(jogador.getPoderParanormal() - 1);
            jogador.setPontosAtributo(jogador.getPontosAtributo() + 1);
        }
        atualizarTela();
    }

    // ── Confirmar ─────────────────────────────────────────────────────

    @FXML
    private void confirmar(ActionEvent event) {
        SomUtil.tocarConfirmar();
        GameState.setVeioDeFuga(false);
        // Se o jogador tiver escolha de habilidade pendente, redireciona para ela primeiro
        if (GameContext.jogadorAtual != null && GameContext.jogadorAtual.isEscolhaPendente()) {
            EscolhaHabilidadeController.telaOrigem = "STATUS";
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        }
    }

    // ── Atualização da tela ───────────────────────────────────────────

    private void atualizarTela() {
        if (jogador == null) return;
        lblNivel.setText("Nível: " + jogador.getNivel()
            + "  (próximo em " + (jogador.getXpParaProximoNivel() - jogador.getXpAtual()) + " XP)");
        lblPontos.setText("Pontos disponíveis: " + jogador.getPontosAtributo());
        lblForca.setText("Força: " + jogador.getForca());
        lblInvestigacao.setText("Investigação: " + jogador.getInvestigacao());
        lblPoderParanormal.setText("Poder Paranormal: " + jogador.getPoderParanormal());
        lblVida.setText("Vida máxima: " + jogador.getVidaMaxima());
        lblPE.setText("PE máximo: " + jogador.getPeMaximo());
        if (lblDinheiro != null) {
            lblDinheiro.setText("Dinheiro: " + jogador.getDinheiro() + " moedas");
        }
    }
}
