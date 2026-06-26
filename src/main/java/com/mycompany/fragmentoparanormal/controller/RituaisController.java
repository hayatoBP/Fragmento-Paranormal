package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.service.CatalogoRituaisService;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.stream.Collectors;

public class RituaisController {

    private Personagem jogador;

    @FXML private ListView<Ritual> listaRituais;
    @FXML private ImageView imgElemento;
    @FXML private Label lblNomeElemento;
    @FXML private Label lblNomeRitual;
    @FXML private Label lblElemento;
    @FXML private Label lblCustoPE;
    @FXML private Label lblDanoBase;
    @FXML private Label lblDanoTotal;
    @FXML private Label lblEficacia;
    @FXML private Label lblForteContra;
    @FXML private Label lblDanoForte;
    @FXML private Label lblFracoContra;
    @FXML private Label lblDanoFraco;

    @FXML
    public void initialize() {
        MusicaManager.tocarResto();
        jogador = GameContext.jogadorAtual;
        
        listaRituais.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) exibirDetalhes(sel);
        });

        atualizarInterface();
    }

    private void atualizarInterface() {
        if (jogador == null) return;
        listaRituais.getItems().setAll(jogador.getRituais());
        if (!listaRituais.getItems().isEmpty()) {
            listaRituais.getSelectionModel().selectFirst();
        }
    }

    private void exibirDetalhes(Ritual r) {
        lblNomeRitual.setText(r.getNome());
        lblElemento.setText(r.getElemento().toString());
        
        int custo = jogador.getCustoPEComAfinidade(r.getCustoPE(), r.getElemento());
        lblCustoPE.setText(custo + " PE" + (custo < r.getCustoPE() ? " (Afinidade!)" : ""));
        
        lblDanoBase.setText(String.valueOf(r.getDano()));
        
        var entrada = CatalogoRituaisService.getTodos().stream()
                .filter(e -> e.ritual().getNome().equals(r.getNome()))
                .findFirst().orElse(null);
        double multPP = (entrada != null) ? entrada.multiplicadorPP() : 1.0;
        double afinidade = jogador.getBonusAfinidade(r.getElemento());
        
        int danoTotal = (int)((r.getDano() + jogador.getPoderParanormal() * multPP) * afinidade);
        lblDanoTotal.setText(String.valueOf(danoTotal));
        
        // Vantagens
        com.mycompany.fragmentoparanormal.util.Elemento forte = elementoFracoContra(r.getElemento());
        com.mycompany.fragmentoparanormal.util.Elemento fraco = elementoForteContra(r.getElemento());
        
        lblForteContra.setText(forte.toString());
        lblDanoForte.setText(String.valueOf((int)(danoTotal * 1.5)));
        lblFracoContra.setText(fraco.toString());
        lblDanoFraco.setText(String.valueOf((int)(danoTotal * 0.5)));
    }

    private com.mycompany.fragmentoparanormal.util.Elemento elementoFracoContra(com.mycompany.fragmentoparanormal.util.Elemento e) {
        return switch (e) {
            case SANGUE -> com.mycompany.fragmentoparanormal.util.Elemento.CONHECIMENTO;
            case CONHECIMENTO -> com.mycompany.fragmentoparanormal.util.Elemento.ENERGIA;
            case ENERGIA -> com.mycompany.fragmentoparanormal.util.Elemento.MORTE;
            case MORTE -> com.mycompany.fragmentoparanormal.util.Elemento.SANGUE;
            default -> e;
        };
    }

    private com.mycompany.fragmentoparanormal.util.Elemento elementoForteContra(com.mycompany.fragmentoparanormal.util.Elemento e) {
        return switch (e) {
            case SANGUE -> com.mycompany.fragmentoparanormal.util.Elemento.MORTE;
            case MORTE -> com.mycompany.fragmentoparanormal.util.Elemento.ENERGIA;
            case ENERGIA -> com.mycompany.fragmentoparanormal.util.Elemento.CONHECIMENTO;
            case CONHECIMENTO -> com.mycompany.fragmentoparanormal.util.Elemento.SANGUE;
            default -> e;
        };
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }
}
