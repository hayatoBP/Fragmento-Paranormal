package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;

public class MenuMissoesController {

    @FXML private ListView<Missao> listaMissoes;
    @FXML private Label lblProgresso;
    @FXML private Button btnBoss;
    @FXML private Button btnComecar;

    @FXML
    public void initialize() {
        // Mostra TODAS as missões — concluídas aparecem marcadas
        listaMissoes.getItems().addAll(MissaoService.getMissoes());

        // Estilo customizado: missão concluída aparece com ✔
        listaMissoes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Missao m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    setText(null);
                } else {
                    String prefixo = m.isConcluida() ? "✔ " : "   ";
                    setText(prefixo + m.getNome() + "  [" + m.getElemento() + "]"
                            + "  (" + m.getFragmentosNecessarios() + " pág.)");
                }
            }
        });

        // Progresso de páginas
        int total = GameState.totalPaginasColetadas();
        lblProgresso.setText("Páginas do diário: " + total + "/" + com.mycompany.fragmentoparanormal.util.GameState.getTotalPaginasJogo());

        // Boss só aparece quando todas as 7 páginas foram coletadas
        btnBoss.setVisible(GameState.isBossDesbloqueado());
        btnBoss.setManaged(GameState.isBossDesbloqueado());

        // Seleciona automaticamente a próxima missão não concluída
        Missao proxima = MissaoService.getProximaMissao();
        if (proxima != null) listaMissoes.getSelectionModel().select(proxima);
    }

    @FXML
    private void comecarMissao(ActionEvent event) {
        Missao missao = listaMissoes.getSelectionModel().getSelectedItem();
        if (missao == null) return;

        // Verifica ordem obrigatória: só pode jogar se a missão anterior foi concluída
        if (!podeComecarMissao(missao)) {
            // Força seleção da próxima disponível
            lblProgresso.setText("Complete as missões anteriores primeiro!");
            return;
        }

        // Se é uma missão diferente da atual (ou nenhuma em andamento), reseta o estado
        com.mycompany.fragmentoparanormal.model.Missao missaoAnterior = GameState.getMissaoAtual();
        boolean mesmaMissao = missaoAnterior != null
                && missaoAnterior.getElemento() == missao.getElemento()
                && GameState.isMissaoEmAndamento();

        GameState.setMissaoAtual(missao);
        if (!mesmaMissao) {
            // Nova missão: reseta estado e mostra diálogo
            GameState.setMissaoEmAndamento(false);
            GameState.setInvestigouNesteAvanco(false);
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/dialogo.fxml");
        } else {
            // Mesma missão em andamento: vai direto, sem resetar vida
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    @FXML
    private void irParaBoss(ActionEvent event) {
        GameContext.inimigoAtual = Inimigo.criarBoss();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
    }

    @FXML
    private void abrirLoja(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/ivete.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    /**
     * Regra de progressão: o jogador só pode jogar uma missão se todas
     * as de ordem menor já foram concluídas — OU se a missão já foi concluída
     * antes (permite rejogo).
     */
    private boolean podeComecarMissao(Missao alvo) {
        for (Missao m : MissaoService.getMissoes()) {
            if (m.getOrdem() < alvo.getOrdem() && !m.isConcluida()) return false;
        }
        return true;
    }
}
