package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Arma;
import com.mycompany.fragmentoparanormal.model.Item;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class InventarioController {

    private Personagem jogador;

    @FXML private ListView<Item>   listaItens;
    @FXML private ListView<Arma>   listaArmas;
    @FXML private ListView<Ritual> listaRituais;
    @FXML private Label lblArmaEquipada;
    @FXML private Label lblRitualEquipado;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        carregarInventario();
        atualizarEquipados();
    }

    private void carregarInventario() {
        if (jogador == null) return;

        listaItens.getItems().clear();
        listaItens.getItems().addAll(jogador.getInventario().getItens());

        listaRituais.getItems().clear();
        listaRituais.getItems().addAll(jogador.getRituais());

        listaArmas.getItems().clear();
        if (jogador.getArmaEquipada() != null) {
            listaArmas.getItems().add(jogador.getArmaEquipada());
        }
    }

    @FXML
    private void usarItem() {
        Item item = listaItens.getSelectionModel().getSelectedItem();
        if (item == null) { System.out.println("Selecione um item."); return; }
        jogador.getInventario().getItens().remove(item);
        System.out.println("Usou item: " + item.getNome());
        carregarInventario();
    }

    @FXML
    private void equiparArma() {
        Arma arma = listaArmas.getSelectionModel().getSelectedItem();
        if (arma == null) { System.out.println("Selecione uma arma."); return; }
        jogador.setArmaEquipada(arma);
        atualizarEquipados();
    }

    @FXML
    private void equiparRitual() {
        Ritual ritual = listaRituais.getSelectionModel().getSelectedItem();
        if (ritual == null) { System.out.println("Selecione um ritual."); return; }
        jogador.setRitualEquipado(ritual);
        atualizarEquipados();
    }

    private void atualizarEquipados() {
        if (jogador == null) return;
        lblArmaEquipada.setText(jogador.getArmaEquipada() == null
            ? "Arma: nenhuma"
            : "Arma: " + jogador.getArmaEquipada().getNome());
        lblRitualEquipado.setText(jogador.getRitualEquipado() == null
            ? "Ritual: nenhum"
            : "Ritual: " + jogador.getRitualEquipado().getNome());
    }

    /**
     * Volta para a tela de onde o inventário foi aberto:
     *  - "MISSAO"  → tela da missão (botão Inventário da missão)
     *  - "COMBATE" → tela de combate (botão Inventário do combate)
     */
    @FXML
    private void voltar(ActionEvent event) {
        String origem = GameState.getOrigemInventario();
        if ("COMBATE".equals(origem)) {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/combate.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }
}
