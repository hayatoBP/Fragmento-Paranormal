package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class JogadorExistenteController {

    @FXML private TableView<Personagem> tabelaJogadores;
    @FXML private TableColumn<Personagem, String> colNome;
    @FXML private TableColumn<Personagem, String> colClasse;
    @FXML private TableColumn<Personagem, String> colElemento;
    @FXML private TableColumn<Personagem, Integer> colNivel;
    @FXML private TableColumn<Personagem, String> colArma;
    @FXML private Label lblAviso;

    @FXML
    public void initialize() {
        // Configura as colunas
        colNome    .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colClasse  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasse().toString()));
        colElemento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getElemento().toString()));
        colNivel   .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNivel()).asObject());
        colArma    .setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getArmaEquipada() != null
                        ? c.getValue().getArmaEquipada().getNome()
                        : "Nenhuma"));

        carregarJogadores();
    }

    private void carregarJogadores() {
        try {
            List<Personagem> jogadores = JogadorDAO.listarTodos();
            if (jogadores.isEmpty()) {
                if (lblAviso != null)
                    lblAviso.setText("Nenhum jogador salvo encontrado.");
            } else {
                tabelaJogadores.setItems(FXCollections.observableArrayList(jogadores));
            }
        } catch (Exception e) {
            if (lblAviso != null)
                lblAviso.setText("Banco de dados indisponível. Verifique a conexão.");
            System.err.println("[Controller] Erro ao carregar jogadores: " + e.getMessage());
        }
    }

    @FXML
    private void confirmar(ActionEvent event) {
        Personagem selecionado = tabelaJogadores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            if (lblAviso != null) lblAviso.setText("Selecione um jogador na tabela.");
            return;
        }
        // Carrega o jogador completo (com itens) do banco
        Personagem carregado = JogadorDAO.buscarPorNome(selecionado.getNome());
        if (carregado == null) carregado = selecionado;

        GameContext.jogadorAtual = carregado;
        GameState.setMissaoEmAndamento(false);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/chamado.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogar.fxml");
    }
}