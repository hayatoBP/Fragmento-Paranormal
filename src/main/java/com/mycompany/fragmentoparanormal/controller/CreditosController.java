package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class CreditosController {

    /**
     * Contexto de onde vieram os créditos:
     *  "MENU"    — botão Créditos da tela inicial (padrão)
     *  "RECUSA"  — jogador recusou o chamado
     *  "FIM"     — jogador derrotou o boss final
     */
    public static String contexto = "MENU";

    @FXML private Label  lblMensagemContexto;
    @FXML private Button btnRecomecar;

    @FXML
    public void initialize() {
        switch (contexto) {
            case "RECUSA" -> {
                lblMensagemContexto.setText(
                    "\"Alguns chamados não podem ser ignorados, Agente.\n" +
                    "O paranormal não esperará para sempre.\"\n\n" +
                    "— A Ordem"
                );
                btnRecomecar.setVisible(true);
                btnRecomecar.setManaged(true);
            }
            case "FIM" -> {
                lblMensagemContexto.setText(
                    "🎉  Parabéns! Você coletou todos os fragmentos e derrotou o Quarto Anfitrião.\n" +
                    "O paranormal recuou — por enquanto.\n\n" +
                    "Obrigado por jogar Fragmento Paranormal!"
                );
                btnRecomecar.setVisible(true);
                btnRecomecar.setManaged(true);
            }
            default -> {
                lblMensagemContexto.setText("");
                btnRecomecar.setVisible(false);
                btnRecomecar.setManaged(false);
            }
        }
    }

    @FXML
    private void recomecar(ActionEvent event) {
        // Reseta estado global para nova partida
        GameState.setMissaoEmAndamento(false);
        GameState.setInvestigouNesteAvanco(false);
        GameState.setBossDesbloqueado(false);
        GameState.setVeioDeFuga(false);
        GameState.setMissaoAtual(null);
        GameContext.jogadorAtual  = null;
        GameContext.inimigoAtual  = null;
        contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    @FXML
    private void voltarMenu(ActionEvent event) {
        contexto = "MENU";
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }

    @FXML
    private void encerrar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
