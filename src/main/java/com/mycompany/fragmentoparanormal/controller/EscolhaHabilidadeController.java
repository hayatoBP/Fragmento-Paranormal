package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Habilidade;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.service.CatalogoHabilidadesService;
import com.mycompany.fragmentoparanormal.service.CatalogoRituaisService;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

public class EscolhaHabilidadeController {

    /** Tela para retornar após a escolha: "MISSAO", "COMBATE" ou "STATUS". */
    public static String telaOrigem = "MISSAO";

    private Personagem jogador;

    @FXML private Label   lblTitulo;
    @FXML private Label   lblDescricao;
    @FXML private FlowPane painelOpcoes;
    @FXML private Label   lblAviso;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        jogador = GameContext.jogadorAtual;
        if (jogador == null) return;

        lblTitulo.setText("Nível " + jogador.getNivel() + " — Escolha uma habilidade");
        lblDescricao.setText(jogador.getClasse() == ClassePersonagem.OCULTISTA
            ? "Escolha um novo ritual para aprender. Apenas um."
            : "Escolha uma nova habilidade para desbloquear. Apenas uma.");

        carregarOpcoes();
    }

    private void carregarOpcoes() {
        painelOpcoes.getChildren().clear();

        if (jogador.getClasse() == ClassePersonagem.OCULTISTA) {
            List<CatalogoRituaisService.EntradaRitual> disponiveis =
                CatalogoRituaisService.getDisponiveis(jogador.getNomesRituais());

            if (disponiveis.isEmpty()) {
                lblAviso.setText("Você já aprendeu todos os rituais disponíveis!");
                return;
            }
            for (var entrada : disponiveis) {
                painelOpcoes.getChildren().add(criarCartaoRitual(entrada));
            }
        } else {
            // Combatente e Especialista: elemental só do próprio elemento
            com.mycompany.fragmentoparanormal.util.Elemento elemJogador =
                (jogador.getClasse() == ClassePersonagem.OCULTISTA) ? null : jogador.getElemento();
            List<Habilidade> disponiveis =
                CatalogoHabilidadesService.getDisponiveis(jogador.getClasse(), jogador.getNomesHabilidades(), elemJogador);

            if (disponiveis.isEmpty()) {
                lblAviso.setText("Você já aprendeu todas as habilidades disponíveis!");
                return;
            }
            for (Habilidade h : disponiveis) {
                painelOpcoes.getChildren().add(criarCartaoHabilidade(h));
            }
        }
    }

    // ── Cartão de Ritual ─────────────────────────────────────────────
    private VBox criarCartaoRitual(CatalogoRituaisService.EntradaRitual entrada) {
        Ritual r = entrada.ritual();

        VBox cartao = new VBox(8);
        cartao.setPrefWidth(200);
        cartao.setPadding(new Insets(14));
        cartao.setAlignment(Pos.TOP_CENTER);
        cartao.setStyle(
            "-fx-background-color: #1a0000; -fx-border-color: #cc0000;" +
            "-fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");

        // Ícone do elemento
        ImageView img = new ImageView();
        img.setFitWidth(60); img.setFitHeight(60); img.setPreserveRatio(true);
        try {
            var s = getClass().getResourceAsStream(caminhoImagem(r.getElemento().toString()));
            if (s != null) img.setImage(new Image(s));
        } catch (Exception ignored) {}

        Label nome  = new Label(r.getNome());
        Label elem  = new Label(r.getElemento().toString());
        Label grau  = new Label("[ " + entrada.grau() + " ]");
        Label dano  = new Label("Dano base: " + r.getDano());
        Label pe    = new Label("Custo PE: " + r.getCustoPE());
        Label mult  = new Label("PP ×" + entrada.multiplicadorPP());

        nome.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e0d0ff; -fx-wrap-text: true;");
        nome.setWrapText(true); nome.setMaxWidth(180);
        elem.setStyle("-fx-font-size: 12px; -fx-text-fill: #cc4444;");
        grau.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
        dano.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");
        pe  .setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
        mult.setStyle("-fx-font-size: 11px; -fx-text-fill: #f39c12;");

        Button btn = new Button("Aprender");
        btn.setPrefWidth(160);
        btn.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnAction(e -> {
            SomUtil.tocarConfirmar();
            jogador.aprenderRitual(r);
            voltar(e);
        });

        cartao.getChildren().addAll(img, nome, elem, grau, new Separator(), dano, pe, mult, btn);
        return cartao;
    }

    // ── Cartão de Habilidade ─────────────────────────────────────────
    private VBox criarCartaoHabilidade(Habilidade h) {
        String corBorda = h.isHabilidadeCampo() ? "#f39c12" : "#c0392b";

        VBox cartao = new VBox(8);
        cartao.setPrefWidth(200);
        cartao.setPadding(new Insets(14));
        cartao.setAlignment(Pos.TOP_CENTER);
        cartao.setStyle(
            "-fx-background-color: #16213e; -fx-border-color: " + corBorda + ";" +
            "-fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label nome  = new Label(h.getNome());
        Label arvore = new Label(h.getArvore() + " — " + h.getTipoHabilidade());
        Label desc  = new Label(h.getDescricao());
        Label efeito = new Label(h.getEfeito());
        Label pe    = new Label("Custo PE: " + h.getCustoPE());
        Label campo = h.isHabilidadeCampo() ? new Label("✦ Habilidade de Campo") : new Label("");

        nome.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e0d0ff;");
        nome.setWrapText(true); nome.setMaxWidth(180);
        arvore.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #bdc3c7;");
        desc.setWrapText(true); desc.setMaxWidth(180);
        efeito.setStyle("-fx-font-size: 12px; -fx-text-fill: #f39c12;");
        efeito.setWrapText(true); efeito.setMaxWidth(180);
        pe.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
        campo.setStyle("-fx-font-size: 11px; -fx-text-fill: #f39c12; -fx-font-weight: bold;");

        Button btn = new Button("Desbloquear");
        btn.setPrefWidth(160);
        btn.setStyle("-fx-background-color: " + corBorda + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnAction(e -> {
            SomUtil.tocarConfirmar();
            jogador.aprenderHabilidade(h);
            voltar(e);
        });

        cartao.getChildren().addAll(nome, arvore, new Separator(), desc, efeito, pe, campo, btn);
        return cartao;
    }

    private void voltar(ActionEvent event) {
        switch (telaOrigem) {
            case "COMBATE" -> TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
            case "STATUS"  -> TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
            default        -> TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    private String caminhoImagem(String elemento) {
        return switch (elemento.toUpperCase()) {
            case "SANGUE"       -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_sangue.png";
            case "MORTE"        -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_morte.png";
            case "ENERGIA"      -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
            case "CONHECIMENTO" -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_conhecimento.png";
            default             -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
        };
    }
}
