package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChamadoController {

    @FXML private Label lblMensagem;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        if (GameContext.jogadorAtual != null) {
            lblMensagem.setText(
                "Agente " + GameContext.jogadorAtual.getNome()
                + ", a Ordem te chamou para uma missão."
            );
        }
    }

    @FXML
    private void aceitar(ActionEvent event) {
        SomUtil.tocarConfirmar();

        // Salva o jogador no banco (insere se novo, atualiza se existente)
        // e atribui o id gerado de volta ao objeto para que salvarProgressoCampanha funcione
        if (GameContext.jogadorAtual != null && GameContext.jogadorAtual.getId() <= 0) {
            int idGerado = JogadorDAO.salvar(GameContext.jogadorAtual);
            if (idGerado > 0) {
                GameContext.jogadorAtual.setId(idGerado);
                System.out.println("[Chamado] Jogador salvo no banco com id=" + idGerado);
            } else {
                System.err.println("[Chamado] Falha ao salvar jogador no banco.");
            }
        }

        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
    }

    @FXML
    private void recusar(ActionEvent event) {
        SomUtil.tocarVoltar();
        // Ao recusar o chamado → créditos com contexto RECUSA
        CreditosController.contexto = "RECUSA";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/creditos.fxml");
    }
}
