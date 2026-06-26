package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.List;

/**
 * Controller da tela de ranking.
 * Exibe todos os jogadores salvos ordenados por nível (decrescente) e XP.
 */
public class RankingController {

    @FXML private TableView<Personagem>          tabelaRanking;
    @FXML private TableColumn<Personagem, Integer> colPosicao;
    @FXML private TableColumn<Personagem, String>  colElementoIcone;
    @FXML private TableColumn<Personagem, String>  colNome;
    @FXML private TableColumn<Personagem, String>  colClasse;
    @FXML private TableColumn<Personagem, String>  colElemento;
    @FXML private TableColumn<Personagem, Integer> colNivel;
    @FXML private TableColumn<Personagem, Integer> colXP;
    @FXML private Label lblAviso;

    @FXML
    public void initialize() {
        MusicaManager.tocarMenuInicial(); // Ranking fica no menu, usa música do menu
        configurarColunas();
        carregarRanking();
    }

    private void configurarColunas() {
        if (colNome    != null) colNome   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        if (colClasse  != null) colClasse .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasse().toString()));
        if (colElemento!= null) colElemento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getElemento().toString()));
        if (colNivel   != null) colNivel  .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNivel()).asObject());
        if (colXP      != null) colXP     .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getXpAtual()).asObject());

        // Coluna de posição — usa índice da tabela
        if (colPosicao != null) {
            colPosicao.setCellValueFactory(c -> {
                int idx = tabelaRanking.getItems().indexOf(c.getValue()) + 1;
                return new SimpleIntegerProperty(idx).asObject();
            });
        }

        // Coluna de ícone do elemento
        if (colElementoIcone != null) {
            colElementoIcone.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getElemento().toString().toLowerCase()));
            colElementoIcone.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
                private final javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView();
                { iv.setFitWidth(24); iv.setFitHeight(24); iv.setPreserveRatio(true); setGraphic(iv); setText(null); }
                @Override protected void updateItem(String elem, boolean empty) {
                    super.updateItem(elem, empty);
                    if (empty || elem == null) { iv.setImage(null); return; }
                    String ext = ".png";
                    try {
                        var s = getClass().getResourceAsStream(
                            "/com/mycompany/fragmentoparanormal/images/simbolos/" + elem + ext);
                        iv.setImage(s != null ? new javafx.scene.image.Image(s) : null);
                    } catch (Exception e) { iv.setImage(null); }
                }
            });
        }
    }

    private void carregarRanking() {
        try {
            List<Personagem> jogadores = JogadorDAO.listarTodos();
            if (jogadores.isEmpty()) {
                if (lblAviso != null) lblAviso.setText("Nenhum jogador salvo encontrado.");
                return;
            }
            // Ordena por nível decrescente, depois por XP decrescente
            jogadores.sort(Comparator
                .comparingInt(Personagem::getNivel).reversed()
                .thenComparingInt(Personagem::getXpAtual).reversed());

            if (tabelaRanking != null)
                tabelaRanking.setItems(FXCollections.observableArrayList(jogadores));
            if (lblAviso != null) lblAviso.setText("");
        } catch (Exception e) {
            if (lblAviso != null)
                lblAviso.setText("Banco de dados indisponível. Verifique a conexão.");
            System.err.println("[RankingController] Erro ao carregar ranking: " + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/telaInicial.fxml");
    }
}
