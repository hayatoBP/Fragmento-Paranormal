package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Artefato;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ArtefatosController {

    private Personagem jogador;

    @FXML private ListView<Artefato> listaNoInventario;   // artefatos não equipados
    @FXML private ListView<Artefato> listaEquipados;      // artefatos equipados (máx 2)

    @FXML private Label lblNome;
    @FXML private Label lblDescricao;
    @FXML private Label lblBonuses;
    @FXML private Label lblSlots;
    @FXML private Label lblFeedback;

    @FXML private Button btnEquipar;
    @FXML private Button btnDesequipar;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        jogador = GameContext.jogadorAtual;
        if (jogador == null) return;

        configurarCelulas(listaNoInventario);
        configurarCelulas(listaEquipados);

        listaNoInventario.getSelectionModel().selectedItemProperty()
            .addListener((obs, ant, sel) -> { mostrarDetalhes(sel); listaEquipados.getSelectionModel().clearSelection(); });

        listaEquipados.getSelectionModel().selectedItemProperty()
            .addListener((obs, ant, sel) -> { mostrarDetalhes(sel); listaNoInventario.getSelectionModel().clearSelection(); });

        carregarListas();
        limparDetalhes();
    }

    private void carregarListas() {
        // Artefatos no inventário (não equipados)
        listaNoInventario.getItems().clear();
        for (Item item : jogador.getInventario().getItens()) {
            if (item instanceof Artefato a) listaNoInventario.getItems().add(a);
        }

        // Artefatos equipados
        listaEquipados.getItems().clear();
        listaEquipados.getItems().addAll(jogador.getArtefatosEquipados());

        int slots = jogador.getArtefatosEquipados().size();
        lblSlots.setText("Equipados: " + slots + " / 2");
        lblSlots.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (slots >= 2 ? "#e74c3c" : "#2ecc71") + ";");
    }

    private void mostrarDetalhes(Artefato a) {
        if (a == null) { limparDetalhes(); return; }
        lblNome.setText(a.getNome());
        lblDescricao.setText(a.getDescricao());
        lblBonuses.setText(a.descricaoBonuses());

        boolean equipado = jogador.getArtefatosEquipados().stream()
            .anyMatch(eq -> eq.getNome().equals(a.getNome()));

        btnEquipar.setDisable(equipado || jogador.getArtefatosEquipados().size() >= 2);
        btnDesequipar.setDisable(!equipado);
        lblFeedback.setText("");
    }

    private void limparDetalhes() {
        lblNome.setText("Selecione um artefato");
        lblDescricao.setText("");
        lblBonuses.setText("");
        btnEquipar.setDisable(true);
        btnDesequipar.setDisable(true);
        lblFeedback.setText("");
    }

    @FXML
    private void equipar() {
        SomUtil.tocarConfirmar();
        Artefato a = listaNoInventario.getSelectionModel().getSelectedItem();
        if (a == null) { lblFeedback.setText("Selecione um artefato para equipar."); return; }

        boolean ok = jogador.equiparArtefato(a);
        if (!ok) {
            lblFeedback.setText("✕ Máximo de 2 artefatos já equipados!");
            lblFeedback.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        jogador.getInventario().getItens().remove(a);
        lblFeedback.setText("🔮 " + a.getNome() + " equipado! Bônus aplicado.");
        lblFeedback.setStyle("-fx-text-fill: #2ecc71;");
        carregarListas();
        limparDetalhes();
    }

    @FXML
    private void desequipar() {
        SomUtil.tocarVoltar();
        Artefato a = listaEquipados.getSelectionModel().getSelectedItem();
        if (a == null) { lblFeedback.setText("Selecione um artefato equipado para remover."); return; }

        jogador.desequiparArtefato(a);
        jogador.getInventario().adicionarItem(a);   // devolve ao inventário
        lblFeedback.setText("🔮 " + a.getNome() + " desequipado. Bônus removido.");
        lblFeedback.setStyle("-fx-text-fill: #f39c12;");
        carregarListas();
        limparDetalhes();
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    private void configurarCelulas(ListView<Artefato> lv) {
        lv.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(Artefato a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) { setText(null); setStyle("-fx-background-color: transparent;"); return; }
                setText(a.getNome());
                setStyle("-fx-text-fill: #e0d0ff; -fx-font-size: 13px; -fx-background-color: transparent;");
            }
        });
    }
}
