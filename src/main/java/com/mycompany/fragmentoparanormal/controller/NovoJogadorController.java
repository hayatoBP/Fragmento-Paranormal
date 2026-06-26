package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.Genero;
import com.mycompany.fragmentoparanormal.util.ImagemUtil;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class NovoJogadorController {

    @FXML private TextField txtNome;
    @FXML private ChoiceBox<ClassePersonagem> choiceClasse;
    @FXML private ChoiceBox<Genero> choiceGenero;
    @FXML private ChoiceBox<Elemento> choiceElemento;
    @FXML private ImageView imgPersonagem;
    @FXML private Label lblErro;
    @FXML private Label lblNomePersonagem;

    @FXML
    public void initialize() {
        MusicaManager.tocarMenuInicial();

        choiceClasse.getItems().addAll(ClassePersonagem.values());
        choiceGenero.getItems().addAll(Genero.values());

        // MEDO é exclusivo do Boss Final — não disponível para jogadores
        for (Elemento e : Elemento.values()) {
            if (e != Elemento.MEDO) choiceElemento.getItems().add(e);
        }

        // Atualiza imagem ao mudar classe ou gênero
        choiceClasse.setOnAction(e -> atualizarImagemPersonagem());
        choiceGenero.setOnAction(e -> atualizarImagemPersonagem());

        // Define valores padrão
        choiceClasse.setValue(ClassePersonagem.COMBATENTE);
        choiceGenero.setValue(Genero.HOMEM);
        choiceElemento.setValue(Elemento.SANGUE);

        // Carrega imagem após a cena estar montada
        javafx.application.Platform.runLater(this::atualizarImagemPersonagem);
    }

    private void atualizarImagemPersonagem() {
        ClassePersonagem classe = choiceClasse.getValue();
        Genero genero = choiceGenero.getValue();
        if (classe == null || genero == null) return;

        String[] info = resolverNomeELabel(classe, genero);
        String nomeArquivo = info[0]; // ex: "dominic"

        // Carrega personagem DESARMADO usando ImagemUtil
        var img = ImagemUtil.carregarPersonagem(nomeArquivo);
        if (img != null) {
            ImagemUtil.aplicar(imgPersonagem, img);
            System.out.println("[NovoJogador] Imagem carregada: " + nomeArquivo);
        } else {
            System.err.println("[NovoJogador] Imagem não encontrada: " + nomeArquivo);
        }

        if (lblNomePersonagem != null) {
            lblNomePersonagem.setText(info[1]);
        }
    }

    /** Retorna [nomeArquivo, labelExibição] com base na classe e gênero. */
    private String[] resolverNomeELabel(ClassePersonagem classe, Genero genero) {
        return switch (classe) {
            case COMBATENTE   -> genero == Genero.HOMEM
                ? new String[]{"dominic", "Dominic — Combatente"}
                : new String[]{"carina",  "Carina — Combatente"};
            case ESPECIALISTA -> genero == Genero.HOMEM
                ? new String[]{"arthur",  "Arthur — Especialista"}
                : new String[]{"erin",    "Erin — Especialista"};
            case OCULTISTA    -> genero == Genero.HOMEM
                ? new String[]{"dante",   "Dante — Ocultista"}
                : new String[]{"agatha",  "Agatha — Ocultista"};
        };
    }

    @FXML
    private void criarJogador(ActionEvent event) {
        SomUtil.tocarConfirmar();
        String nome = txtNome.getText();
        ClassePersonagem classe = choiceClasse.getValue();
        Genero genero = choiceGenero.getValue();
        Elemento elemento = choiceElemento.getValue();

        if (nome == null || nome.isBlank() || classe == null || genero == null || elemento == null) {
            if (lblErro != null) lblErro.setText("Preencha todos os campos!");
            return;
        }

        Personagem jogador = new Personagem(nome, classe, genero, elemento);
        GameContext.jogadorAtual = jogador;
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/chamado.fxml");
    }

    @FXML
    private void voltar(ActionEvent event) {
        SomUtil.tocarVoltar();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/jogar.fxml");
    }

    // Mantido para compatibilidade
    public static Personagem getJogadorAtual() {
        return GameContext.jogadorAtual;
    }
}
