package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.ImagemUtil;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DialogoController {

    /** Destino após completar todos os diálogos. "MISSAO" (padrão) ou "COMBATE_FINAL". */
    public static String destinoPosDialogo = "MISSAO";

    @FXML private ImageView imgProtagonista;
    @FXML private ImageView imgCenario;
    @FXML private ImageView imgOrdem;
    @FXML private Label lblNome;
    @FXML private Label lblFala;

    private final ArrayList<String[]> dialogos = new ArrayList<>();
    private int indiceDialogo = 0;
    private Personagem jogador;
    private Missao missao;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        missao  = GameState.getMissaoAtual();

        if (missao != null) {
            MusicaManager.tocarMissao(missao.getElemento().name());
        }

        carregarDialogos();

        // Carrega imagens após a cena estar completamente montada
        Platform.runLater(() -> {
            carregarCenario();
            carregarProtagonista();
            carregarOrdem();
            mostrarDialogoAtual();
        });
    }

    // ── CENÁRIO ─────────────────────────────────────────────────────────

    private void carregarCenario() {
        if (imgCenario == null) return;

        javafx.scene.image.Image img = null;

        // 1ª tentativa: cenário do local atual da missão (ex: sangue/1.png)
        if (missao != null) {
            String elem = missao.getElemento().toString().toLowerCase();
            img = ImagemUtil.carregarCenarioMissao(elem, 1);
        }

        // 2ª tentativa: cenário genérico do elemento
        if (img == null && missao != null) {
            String elem = missao.getElemento().toString().toLowerCase();
            img = ImagemUtil.carregar(
                "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_" + elem + ".png");
        }

        if (img != null) {
            imgCenario.setImage(img);
            System.out.println("[Dialogo] Cenário carregado.");
        } else {
            System.err.println("[Dialogo] Cenário não encontrado.");
        }
    }

    // ── PROTAGONISTA ─────────────────────────────────────────────────────

    private void carregarProtagonista() {
        if (imgProtagonista == null || jogador == null) return;

        String nome = jogador.getNomePersonagemBase(); // ex: "arthur", "erin"

        // Tenta imagem de diálogo dedicada primeiro, depois desarmada normal
        var img = ImagemUtil.carregarPersonagemDialogo(nome);

        if (img != null) {
            imgProtagonista.setImage(img);
            System.out.println("[Dialogo] Protagonista carregado: " + nome);
        } else {
            System.err.println("[Dialogo] Imagem do protagonista não encontrada: " + nome);
        }
    }

    // ── ORDEM (NPC) ──────────────────────────────────────────────────────

    private void carregarOrdem() {
        if (imgOrdem == null) return;

        var img = ImagemUtil.carregar(
            "/com/mycompany/fragmentoparanormal/images/ordem/dialogo");

        if (img != null) {
            imgOrdem.setImage(img);
            System.out.println("[Dialogo] Imagem da Ordem carregada.");
        } else {
            System.err.println("[Dialogo] Imagem da Ordem não encontrada.");
        }
    }

    // ── DIÁLOGOS ─────────────────────────────────────────────────────────

    private void carregarDialogos() {
        if (missao == null) {
            dialogos.add(new String[]{"Ordem Paranormal",
                "Uma anomalia foi detectada. Investigue o local."});
            return;
        }
        try {
            InputStream input = getClass().getResourceAsStream(missao.getArquivoDialogo());
            if (input == null) {
                dialogos.add(new String[]{"Ordem Paranormal",
                    "Uma anomalia foi detectada. Investigue o local."});
                dialogos.add(new String[]{"Ordem Paranormal",
                    "Colete os fragmentos e elimine as ameaças."});
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
            dialogos.add(new String[]{"Ordem Paranormal",
                "Sua missão começa agora. Boa sorte, agente."});
        }
    }

    private void mostrarDialogoAtual() {
        if (indiceDialogo < dialogos.size()) {
            String[] fala = dialogos.get(indiceDialogo);
            if (lblNome != null) lblNome.setText(fala[0]);
            if (lblFala != null) lblFala.setText(fala[1]);
        } else {
            if ("COMBATE_FINAL".equals(destinoPosDialogo)) {
                destinoPosDialogo = "MISSAO"; // reset
                TelaUtil.trocarTelaPorNode(lblFala,
                    "/com/mycompany/fragmentoparanormal/view/combate.fxml");
            } else {
                TelaUtil.trocarTelaPorNode(lblFala,
                    "/com/mycompany/fragmentoparanormal/view/missao.fxml");
            }
        }
    }

    @FXML
    private void proximoDialogo(ActionEvent event) {
        SomUtil.tocarConfirmar();
        indiceDialogo++;
        mostrarDialogoAtual();
    }

    @FXML
    private void pularDialogo(ActionEvent event) {
        SomUtil.tocarVoltar();
        if ("COMBATE_FINAL".equals(destinoPosDialogo)) {
            destinoPosDialogo = "MISSAO";
            TelaUtil.trocarTelaPorNode(lblFala,
                "/com/mycompany/fragmentoparanormal/view/combate.fxml");
        } else {
            TelaUtil.trocarTelaPorNode(lblFala,
                "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }
}
