package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.Genero;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NovoJogadorController {

    @FXML private TextField txtNome;
    @FXML private ChoiceBox<ClassePersonagem> choiceClasse;
    @FXML private ChoiceBox<Genero> choiceGenero;
    @FXML private ChoiceBox<Elemento> choiceElemento;
    @FXML private ImageView imgPersonagem;
    @FXML private Label lblErro;

    @FXML
    public void initialize() {
        choiceClasse.getItems().addAll(ClassePersonagem.values());
        choiceGenero.getItems().addAll(Genero.values());
        // MEDO é exclusivo do Boss Final — não disponível para jogadores
        for (Elemento e : Elemento.values()) {
            if (e != Elemento.MEDO) choiceElemento.getItems().add(e);
        }

        choiceClasse.setOnAction(e -> atualizarImagemPersonagem());
        choiceGenero.setOnAction(e -> atualizarImagemPersonagem());
    }

    private void atualizarImagemPersonagem() {
        ClassePersonagem classe = choiceClasse.getValue();
        Genero genero = choiceGenero.getValue();
        if (classe == null || genero == null) return;

        String caminho = resolverImagem(classe, genero);
        try {
            var stream = getClass().getResourceAsStream(caminho);
            if (stream != null) {
                imgPersonagem.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.err.println("Imagem não encontrada: " + caminho);
        }
    }

    private String resolverImagem(ClassePersonagem classe, Genero genero) {
        String base = "/com/mycompany/fragmentoparanormal/images/personagens/";
        return switch (classe) {
            case COMBATENTE  -> base + (genero == Genero.HOMEM ? "dominic.png" : "carina.png");
            case ESPECIALISTA -> base + (genero == Genero.HOMEM ? "arthur.png"  : "erin.png");
            case OCULTISTA   -> base + (genero == Genero.HOMEM ? "dante.png"   : "agatha.png");
        };
    }

    @FXML
    private void criarJogador(ActionEvent event) {
        String nome = txtNome.getText();
        ClassePersonagem classe = choiceClasse.getValue();
        Genero genero = choiceGenero.getValue();
        Elemento elemento = choiceElemento.getValue();

        if (nome == null || nome.isBlank() || classe == null || genero == null || elemento == null) {
            if (lblErro != null) lblErro.setText("Preencha todos os campos!");
            System.out.println("Preencha todos os campos.");
            return;
        }

        Personagem jogador = new Personagem(nome, classe, genero, elemento);
        GameContext.jogadorAtual = jogador;

        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/chamado.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogar.fxml");
    }

    // Mantido para compatibilidade
    public static Personagem getJogadorAtual() {
        return GameContext.jogadorAtual;
    }
}
