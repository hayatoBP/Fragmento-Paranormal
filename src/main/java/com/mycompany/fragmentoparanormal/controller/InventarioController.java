package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Artefato;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.service.ElementoService;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import com.mycompany.fragmentoparanormal.util.TipoItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InventarioController {

    private Personagem jogador;

    @FXML private ListView<Arma>   listaArmas;
    @FXML private ListView<String> listaHabilidades;
    @FXML private ListView<Ritual> listaRituais;
    @FXML private ListView<Item>   listaArtefatos;
    @FXML private ListView<Item>   listaItens;

    @FXML private ImageView imgPersonagem;
    @FXML private Label lblNome;
    @FXML private Label lblClasse;
    @FXML private Label lblVida;
    @FXML private Label lblPE;
    @FXML private Label lblArmaEquipada;
    @FXML private Label lblHabilidadeEquipada;
    @FXML private Label lblRitualEquipado;
    @FXML private Label lblArtefatosEquipados;
    @FXML private Label lblNivel;
    @FXML private Label lblFeedbackItem;
    @FXML private Button btnUsarItem;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        listaArmas.setCellFactory(lv -> celulaArma());
        listaRituais.setCellFactory(lv -> celulaRitual());
        listaHabilidades.setCellFactory(lv -> celulaHabilidade());
        listaArtefatos.setCellFactory(lv -> celulaArtefato());
        listaItens.setCellFactory(lv -> celulaItem());
        listaItens.getSelectionModel().selectedItemProperty().addListener(
            (obs, ant, sel) -> btnUsarItem.setDisable(sel == null || !sel.temEfeito())
        );
        carregarInventario();
        atualizarInfoPersonagem();
    }

    // ── CARREGAMENTO ────────────────────────────────────────────────

    private void carregarInventario() {
        if (jogador == null) return;
        
        // Garante que a lista de armas mostre todas as armas que o jogador possui
        java.util.List<Arma> todasArmas = new java.util.ArrayList<>(jogador.getArmas());
        listaArmas.getItems().setAll(todasArmas);
        
        listaRituais.getItems().setAll(jogador.getRituais());
        carregarHabilidades();

        // Separar artefatos dos consumíveis
        listaArtefatos.getItems().clear();
        listaItens.getItems().clear();
        for (Item item : jogador.getInventario().getItens()) {
            if (item.getTipo() == TipoItem.ARTEFATO) {
                listaArtefatos.getItems().add(item);
            } else {
                listaItens.getItems().add(item);
            }
        }
    }

    private void carregarHabilidades() {
        listaHabilidades.getItems().clear();
        if (jogador == null) return;
        switch (jogador.getClasse()) {
            case COMBATENTE -> {
                listaHabilidades.getItems().add("Golpe Brutal — Dano físico ×1.75  (8 PE)");
                listaHabilidades.getItems().add("Resistência — Reduz próximo dano em 30%  (5 PE)");
            }
            case ESPECIALISTA -> {
                listaHabilidades.getItems().add("Tiro Preciso — Ignora defesa do inimigo  (10 PE)");
                listaHabilidades.getItems().add("Análise Rápida — Revela fraqueza elemental  (6 PE)");
            }
            case OCULTISTA -> {
                listaHabilidades.getItems().add("Absorção Paranormal — Recupera 15 PE ao matar  (passivo)");
                listaHabilidades.getItems().add("Escudo Arcano — Absorve 20 de dano  (12 PE)");
            }
        }
    }

    // ── CÉLULAS ─────────────────────────────────────────────────────

    /**
     * PARTE 4 — Arma com tooltip completo:
     * nome, dano exato, classificação colorida, descrição, comparação com equipada.
     */
    private ListCell<Arma> celulaArma() {
        return new ListCell<>() {
            private final Tooltip tip = new Tooltip();
            {
                tip.setMaxWidth(300);
                tip.setWrapText(true);
                tip.setStyle(
                    "-fx-background-color: #0d0d1a; -fx-text-fill: #ecf0f1; " +
                    "-fx-font-size: 12px; -fx-border-color: #c0392b; " +
                    "-fx-border-width: 1; -fx-padding: 12;");
                setTooltip(tip);
            }

            @Override
            protected void updateItem(Arma arma, boolean empty) {
                super.updateItem(arma, empty);
                if (empty || arma == null) {
                    setText(null); setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                // Rótulo com classificação colorida
                int danoTotal = jogador.getForca() + arma.getBonusDano();
                String classLabel = arma.rotuloclassificacao();
                setText(arma.getNome() + "  " + classLabel);
                setStyle("-fx-text-fill: " + arma.corClassificacao() +
                         "; -fx-background-color: transparent; -fx-font-size: 12px;");

                // Tooltip detalhado
                Arma atual    = jogador.getArmaEquipada();
                int danoAtual = atual != null ? jogador.getForca() + atual.getBonusDano() : jogador.getForca();

                String comp;
                if      (danoTotal > danoAtual) comp = "✅ Mais forte que a atual  (+" + (danoTotal - danoAtual) + ")";
                else if (danoTotal < danoAtual) comp = "⚠  Mais fraca que a atual  (-" + (danoAtual - danoTotal) + ")";
                else                            comp = "➖ Mesma força que a atual";

                tip.setText(
                    "⚔  " + arma.getNome() + "\n" +
                    "─────────────────────────\n" +
                    "Classificação :  " + classLabel + "\n" +
                    "Bônus de dano :  +" + arma.getBonusDano() + "\n" +
                    "Dano total    :  " + danoTotal + "  (Força " + jogador.getForca() + " + " + arma.getBonusDano() + ")\n" +
                    "─────────────────────────\n" +
                    arma.getDescricao() + "\n" +
                    "─────────────────────────\n" +
                    comp
                );
            }
        };
    }

    private ListCell<Ritual> celulaRitual() {
        return new ListCell<>() {
            private final ImageView ico = new ImageView();
            private final Label lbl = new Label();
            private final HBox hbox = new HBox(8, ico, lbl);
            private final Tooltip tip = new Tooltip();
            {
                ico.setFitWidth(20); ico.setFitHeight(20); ico.setPreserveRatio(true);
                lbl.setStyle("-fx-text-fill: #ecf0f1; -fx-font-size: 12px;");
                hbox.setAlignment(Pos.CENTER_LEFT);
                tip.setMaxWidth(280); tip.setWrapText(true);
                tip.setStyle("-fx-background-color: #0d0d1a; -fx-text-fill: #ecf0f1; " +
                    "-fx-font-size: 12px; -fx-border-color: #1abc9c; -fx-border-width: 1; -fx-padding: 10;");
                setTooltip(tip);
            }

            @Override
            protected void updateItem(Ritual ritual, boolean empty) {
                super.updateItem(ritual, empty);
                if (empty || ritual == null) { setGraphic(null); setText(null); setStyle("-fx-background-color: transparent;"); return; }

                lbl.setText(ritual.getNome() + "  [" + ritual.getElemento() + "]");
                setGraphic(hbox); setText(null);
                setStyle("-fx-background-color: transparent;");
                try {
                    var s = getClass().getResourceAsStream(caminhoIconeElemento(ritual.getElemento()));
                    ico.setImage(s != null ? new Image(s) : null);
                } catch (Exception e) { ico.setImage(null); }

                int danoBase = ritual.getDano();
                int danoTotal = danoBase + jogador.calcularDanoRitual();
                Elemento forte = elementoFracoContra(ritual.getElemento());
                Elemento fraco = elementoForteContra(ritual.getElemento());

                tip.setText(
                    "☽  " + ritual.getNome() + "\n" +
                    "─────────────────────────\n" +
                    "Elemento  :  " + ritual.getElemento() + "\n" +
                    (danoBase > 0 ? "Dano base :  " + danoBase : "Cura      :  " + ritual.getCura()) + "\n" +
                    "Dano total:  " + danoTotal + "\n" +
                    "Custo PE  :  " + ritual.getCustoPE() + " PE\n" +
                    "─────────────────────────\n" +
                    "✅ Forte contra :  " + forte + "  (~" + (int)(danoTotal * 1.5) + ")\n" +
                    "⚠  Fraco contra :  " + fraco + "  (~" + (int)(danoTotal * 0.5) + ")"
                );
            }
        };
    }

    private ListCell<String> celulaHabilidade() {
        return new ListCell<>() {
            @Override
            protected void updateItem(String hab, boolean empty) {
                super.updateItem(hab, empty);
                if (empty || hab == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(hab);
                setStyle("-fx-text-fill: #f5cba7; -fx-background-color: transparent; -fx-font-size: 12px;");
            }
        };
    }

    private ListCell<Item> celulaArtefato() {
        return new ListCell<>() {
            private final Tooltip tip = new Tooltip();
            {
                tip.setMaxWidth(260); tip.setWrapText(true);
                tip.setStyle("-fx-background-color: #0d0d1a; -fx-text-fill: #ecf0f1; " +
                    "-fx-font-size: 12px; -fx-border-color: #e67e22; -fx-border-width: 1; -fx-padding: 10;");
                setTooltip(tip);
            }
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText("🔮  " + item.getNome());
                setStyle("-fx-text-fill: #e67e22; -fx-background-color: transparent; -fx-font-size: 12px;");
                tip.setText("🔮  " + item.getNome() + "\n─────────────────\n" + item.getDescricao());
            }
        };
    }

    private ListCell<Item> celulaItem() {
        return new ListCell<>() {
            private final Tooltip tip = new Tooltip();
            {
                tip.setMaxWidth(260); tip.setWrapText(true);
                tip.setStyle("-fx-background-color: #0d0d1a; -fx-text-fill: #ecf0f1; " +
                    "-fx-font-size: 12px; -fx-border-color: #6c3483; -fx-border-width: 1; -fx-padding: 10;");
                setTooltip(tip);
            }
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(item.getNome());
                setStyle("-fx-text-fill: #a569bd; -fx-background-color: transparent; -fx-font-size: 12px;");
                tip.setText(item.getNome() + "\n─────────────────\n" + item.getDescricao());
            }
        };
    }

    // ── AÇÕES ────────────────────────────────────────────────────────

    @FXML
    private void equiparArma() {
        Arma arma = listaArmas.getSelectionModel().getSelectedItem();
        if (arma == null) { feedback("Selecione uma arma para equipar."); return; }
        
        // Equipamos a nova arma
        jogador.setArmaEquipada(arma);
        
        // BUG CORRIGIDO: Não removemos mais a arma da lista de armas possuídas.
        // O jogador pode ter várias armas e alternar entre elas livremente.
        
        atualizarInfoPersonagem();
        carregarInventario();
        feedback("⚔ " + arma.getNome() + " equipada!");
    }

    @FXML
    private void equiparRitual() {
        Ritual ritual = listaRituais.getSelectionModel().getSelectedItem();
        if (ritual == null) { feedback("Selecione um ritual para equipar."); return; }
        jogador.setRitualEquipado(ritual);
        atualizarInfoPersonagem();
        feedback("☽ " + ritual.getNome() + " equipado!");
    }

    @FXML
    private void abrirArtefatos(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/artefatos.fxml");
    }

    @FXML
    private void equiparArtefato() {
        Item item = listaArtefatos.getSelectionModel().getSelectedItem();
        if (!(item instanceof Artefato artefato)) {
            feedback("Selecione um artefato válido."); return;
        }
        boolean ok = jogador.equiparArtefato(artefato);
        if (!ok) { feedback("Máximo de 2 artefatos equipados!"); return; }
        jogador.getInventario().getItens().remove(artefato);
        atualizarInfoPersonagem();
        carregarInventario();
        feedback("🔮 " + artefato.getNome() + " equipado! Bônus aplicado.");
    }

    @FXML
    private void usarItem() {
        Item item = listaItens.getSelectionModel().getSelectedItem();
        if (item == null || !item.temEfeito()) { feedback("Selecione um item para usar."); return; }
        String resultado = aplicarEfeito(item);
        jogador.getInventario().getItens().remove(item);
        carregarInventario();
        atualizarInfoPersonagem();
        feedback("✔ " + resultado);
    }

    @FXML
    private void abrirTelaRituais(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/rituais.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        String origem = GameState.getOrigemInventario();
        if ("COMBATE".equals(origem)) {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    // ── FICHA DO PERSONAGEM ──────────────────────────────────────────

    private void atualizarInfoPersonagem() {
        if (jogador == null) return;
        try {
            var s = getClass().getResourceAsStream(jogador.getImagemAtual());
            if (s != null) imgPersonagem.setImage(new Image(s));
        } catch (Exception ignored) {}

        lblNome.setText(jogador.getNome());
        lblClasse.setText(jogador.getClasse() + "  ·  " + jogador.getElemento());
        lblVida.setText(jogador.getVida() + " / " + jogador.getVidaMaxima());
        lblPE.setText(jogador.getPontosEsforco() + " / " + jogador.getPeMaximo());
        lblNivel.setText("Nível " + jogador.getNivel() +
            "  ·  " + jogador.getXpAtual() + "/" + jogador.getXpParaProximoNivel() + " XP");

        Arma arma = jogador.getArmaEquipada();
        if (arma == null) {
            lblArmaEquipada.setText("⚔  Arma: nenhuma");
        } else {
            int dano = jogador.getForca() + arma.getBonusDano();
            lblArmaEquipada.setText("⚔  " + arma.getNome() +
                "  [" + arma.rotuloclassificacao() + "]  dano: " + dano);
        }

        switch (jogador.getClasse()) {
            case COMBATENTE   -> lblHabilidadeEquipada.setText("✦  Golpe Brutal  (8 PE)");
            case ESPECIALISTA -> lblHabilidadeEquipada.setText("✦  Tiro Preciso  (10 PE)");
            case OCULTISTA    -> lblHabilidadeEquipada.setText("✦  Absorção Paranormal  (passivo)");
        }

        lblRitualEquipado.setText(jogador.getRitualEquipado() == null
            ? "☽  Ritual: nenhum"
            : "☽  " + jogador.getRitualEquipado().getNome() +
              "  (" + jogador.getRitualEquipado().getCustoPE() + " PE)");

        if (jogador.getArtefatosEquipados().isEmpty()) {
            lblArtefatosEquipados.setText("🔮  Artefatos: nenhum");
        } else {
            StringBuilder sb = new StringBuilder("🔮  ");
            for (Item a : jogador.getArtefatosEquipados())
                sb.append(a.getNome()).append(", ");
            lblArtefatosEquipados.setText(sb.toString().replaceAll(", $", ""));
        }
    }

    // ── EFEITOS DE ITENS ─────────────────────────────────────────────

    private String aplicarEfeito(Item item) {
        return switch (item.getTipo()) {
            case CURA -> {
                int ant = jogador.getVida();
                int nova = Math.min(ant + item.getEfeito(), jogador.getVidaMaxima());
                jogador.setVida(nova);
                yield item.getNome() + " usada. Vida +" + (nova - ant) + " (" + nova + "/" + jogador.getVidaMaxima() + ")";
            }
            case RESTAURAR_PE -> {
                int ant = jogador.getPontosEsforco();
                int novo = Math.min(ant + item.getEfeito(), jogador.getPeMaximo());
                jogador.setPontosEsforco(novo);
                yield item.getNome() + " usado. PE +" + (novo - ant) + " (" + novo + "/" + jogador.getPeMaximo() + ")";
            }
            case BOOST_PARANORMAL -> {
                jogador.setPoderParanormal(jogador.getPoderParanormal() + item.getEfeito());
                yield item.getNome() + ". Poder Paranormal +" + item.getEfeito();
            }
            case BOOST_FORCA -> {
                jogador.setForca(jogador.getForca() + item.getEfeito());
                yield item.getNome() + ". Força +" + item.getEfeito();
            }
            case BOOST_VIDA_MAX -> {
                jogador.setVidaMaxima(jogador.getVidaMaxima() + item.getEfeito());
                jogador.setVida(jogador.getVida() + item.getEfeito());
                yield item.getNome() + ". Vida Máx +" + item.getEfeito();
            }
            default -> item.getNome() + " — nenhum efeito ativo.";
        };
    }

    private void feedback(String msg) {
        if (lblFeedbackItem != null) lblFeedbackItem.setText(msg);
    }

    // ── UTILITÁRIOS ELEMENTAIS ───────────────────────────────────────

    private Elemento elementoFracoContra(Elemento e) {
        return switch (e) {
            case SANGUE       -> Elemento.CONHECIMENTO;
            case MORTE        -> Elemento.SANGUE;
            case ENERGIA      -> Elemento.MORTE;
            case CONHECIMENTO -> Elemento.ENERGIA;
            default           -> Elemento.ENERGIA;
        };
    }

    private Elemento elementoForteContra(Elemento e) {
        return switch (e) {
            case SANGUE       -> Elemento.MORTE;
            case MORTE        -> Elemento.ENERGIA;
            case ENERGIA      -> Elemento.CONHECIMENTO;
            case CONHECIMENTO -> Elemento.SANGUE;
            default           -> Elemento.SANGUE;
        };
    }

    private String caminhoIconeElemento(Elemento e) {
        return switch (e) {
            case SANGUE       -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_sangue.png";
            case MORTE        -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_morte.png";
            case ENERGIA      -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
            case CONHECIMENTO -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_conhecimento.png";
            default           -> "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png";
        };
    }
}
