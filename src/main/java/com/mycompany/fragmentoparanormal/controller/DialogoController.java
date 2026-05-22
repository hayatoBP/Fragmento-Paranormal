package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DialogoController {

    @FXML private ImageView imgProtagonista;
    @FXML private ImageView imgCenario;
    @FXML private Label lblNome;
    @FXML private Label lblFala;

    private ArrayList<String[]> dialogos = new ArrayList<>();
    private int indiceDialogo = 0;
    private Personagem jogador;
    private Missao missao;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        missao = GameState.getMissaoAtual();
        carregarImagens();
        carregarDialogos();
        mostrarDialogoAtual();
    }

    private void carregarImagens() {
        try {
            if (jogador != null) {
                var s = getClass().getResourceAsStream(jogador.getImagemAtual());
                if (s != null) imgProtagonista.setImage(new Image(s));
            }
            if (missao != null) {
                var s = getClass().getResourceAsStream(missao.getImagemCenario());
                if (s != null) imgCenario.setImage(new Image(s));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens do diálogo: " + e.getMessage());
        }
    }

    private void carregarDialogos() {
        if (missao == null) return;
        try {
            InputStream input = getClass().getResourceAsStream(missao.getArquivoDialogo());
            if (input == null) {
                // Diálogo padrão se arquivo não existir
                dialogos.add(new String[]{"Ordem Paranormal", "Uma anomalia foi detectada. Investigue o local."});
                dialogos.add(new String[]{"Ordem Paranormal", "Colete os fragmentos e elimine as ameaças."});
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";", 2);
                if (partes.length == 2) dialogos.add(partes);
            }
            reader.close();
        } catch (Exception e) {
            dialogos.add(new String[]{"Ordem Paranormal", "Sua missão começa agora. Boa sorte, agente."});
        }
    }

    private void mostrarDialogoAtual() {
        if (indiceDialogo < dialogos.size()) {
            String[] fala = dialogos.get(indiceDialogo);
            lblNome.setText(fala[0]);
            lblFala.setText(fala[1]);
        } else {
            TelaUtil.trocarTelaPorNode(lblFala, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    @FXML
    private void proximoDialogo(ActionEvent event) {
        indiceDialogo++;
        mostrarDialogoAtual();
    }
}
