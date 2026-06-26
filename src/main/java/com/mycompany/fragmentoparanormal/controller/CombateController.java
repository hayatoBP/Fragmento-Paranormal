package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.CombateService;
import com.mycompany.fragmentoparanormal.service.ElementoService;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.service.RitualService;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.MusicaManager;
import com.mycompany.fragmentoparanormal.util.SomUtil;
import com.mycompany.fragmentoparanormal.util.ImagemUtil;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller da tela de combate.
 * Gerencia turnos, animações de combate, efeitos visuais e vantagens elementais.
 */
public class CombateController {

    private Personagem jogador;
    private Inimigo    inimigo;

    @FXML private Label lblNomeInimigo, lblNomeInimigoArena, lblVidaJogador, lblPEJogador, lblVidaInimigo,
                        lblElementoInimigo, lblAmaldicao, lblEfetividade, lblEventos, lblDicaAtaque;
    @FXML private ImageView imgJogador, imgInimigo, imgCenarioCombate;
    @FXML private HBox painelPrincipal, painelMenuAtaque;
    @FXML private Button btnAtacar, btnFugir, btnVoltarMissao, btnAtaqueArma,
                         btnAtaqueEspecial, btnRitual, btnAmaldicoar;
    @FXML private StackPane rootPane;

    // Posições originais para animação de tremor
    private double imgJogadorOrigX;
    private double imgInimigoOrigX;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        inimigo = GameContext.inimigoAtual;

        if (jogador == null || inimigo == null) {
            System.err.println("[Combate] Jogador ou Inimigo nulo!");
            return;
        }

        atualizarInterface();
        // Carrega imagens após a cena estar montada para garantir que os ImageViews existem
        javafx.application.Platform.runLater(() -> {
            carregarImagens();
            animarEntrada();
        });

        // Música de combate: boss final, boss de missão ou missão padrão
        if (inimigo.isBossFinal()) {
            MusicaManager.tocarBossFinal();
        } else if (inimigo.isBossMissao()) {
            String elem = GameState.getMissaoAtual() != null
                ? GameState.getMissaoAtual().getElemento().name() : null;
            MusicaManager.tocarBossMissao(elem);
        } else {
            String elem = GameState.getMissaoAtual() != null
                ? GameState.getMissaoAtual().getElemento().name() : null;
            MusicaManager.tocarMissao(elem);
        }

        // Se o inimigo já estiver morto (ao voltar do inventário), mostra botão de continuar
        if (!inimigo.estaVivo()) {
            finalizarCombate(true);
        }
    }

    // ── CARREGAMENTO DE IMAGENS ──────────────────────────────────────────

    private void carregarImagens() {
        // ── Jogador: ARMADO se tiver arma equipada, senão desarmado
        String nomeBase = jogador.getNomePersonagemBase();
        Image imgJ = jogador.getArmaEquipada() != null
            ? ImagemUtil.carregarPersonagemArmado(nomeBase)
            : ImagemUtil.carregarPersonagem(nomeBase);
        if (imgJ != null) {
            ImagemUtil.aplicar(imgJogador, imgJ);
            System.out.println("[Combate] Jogador carregado: " + nomeBase
                + (jogador.getArmaEquipada() != null ? "_arma" : ""));
        } else {
            System.err.println("[Combate] Imagem do jogador NÃO encontrada: " + nomeBase);
        }

        // ── Inimigo/Monstro (caminho completo com extensão)
        Image imgI = ImagemUtil.carregar(inimigo.getImagem());
        if (imgI != null) {
            ImagemUtil.aplicar(imgInimigo, imgI);
            System.out.println("[Combate] Inimigo carregado: " + inimigo.getImagem());
        } else {
            System.err.println("[Combate] Imagem do inimigo NÃO encontrada: " + inimigo.getImagem());
        }

        // ── Cenário de batalha
        if (imgCenarioCombate != null) {
            String elem = GameState.getMissaoAtual() != null
                ? GameState.getMissaoAtual().getElemento().toString().toLowerCase()
                : "sangue";
            Image imgC = inimigo.isBoss()
                ? ImagemUtil.carregarCenarioBoss(elem)
                : ImagemUtil.carregarCenarioBatalha(elem);
            ImagemUtil.aplicar(imgCenarioCombate, imgC);
            if (imgC != null) System.out.println("[Combate] Cenário carregado: " + elem);
        }
    }

    // ── ANIMAÇÃO DE ENTRADA ──────────────────────────────────────────────

    /** Tenta carregar imagem com extensões .png, .webp, .jpg em sequência. */
    private Image carregarImagem(String basePath) {
        // Se já tem extensão, tenta direto
        if (basePath.matches(".*\\.(png|webp|jpg|gif)$")) {
            var s = getClass().getResourceAsStream(basePath);
            if (s != null) return new Image(s);
        }
        // Tenta com cada extensão
        for (String ext : new String[]{".png", ".webp", ".jpg"}) {
            String path = basePath.replaceAll("\\.(png|webp|jpg|gif)$", "") + ext;
            var s = getClass().getResourceAsStream(path);
            if (s != null) return new Image(s);
        }
        return null;
    }

    private void animarEntrada() {
        // Jogador entra deslizando da esquerda
        imgJogador.setTranslateX(-300);
        imgJogador.setOpacity(0);
        Timeline entradaJogador = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgJogador.translateXProperty(), -300),
                new KeyValue(imgJogador.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(600),
                new KeyValue(imgJogador.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(imgJogador.opacityProperty(), 1, Interpolator.EASE_IN))
        );

        // Inimigo entra deslizando da direita
        imgInimigo.setTranslateX(300);
        imgInimigo.setOpacity(0);
        Timeline entradaInimigo = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgInimigo.translateXProperty(), 300),
                new KeyValue(imgInimigo.opacityProperty(), 0)),
            new KeyFrame(Duration.millis(600),
                new KeyValue(imgInimigo.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(imgInimigo.opacityProperty(), 1, Interpolator.EASE_IN))
        );

        // Efeito de glow no inimigo ao entrar
        DropShadow glowInimigo = new DropShadow(20, Color.RED);
        glowInimigo.setSpread(0.3);
        imgInimigo.setEffect(glowInimigo);

        ParallelTransition entrada = new ParallelTransition(entradaJogador, entradaInimigo);
        entrada.setDelay(Duration.millis(100));
        entrada.play();
    }

    // ── ATUALIZAÇÃO DA INTERFACE ─────────────────────────────────────────

    private void atualizarInterface() {
        lblNomeInimigo.setText(inimigo.getNome());
        if (lblNomeInimigoArena != null) lblNomeInimigoArena.setText(inimigo.getNome().toUpperCase());
        lblVidaJogador.setText("❤ " + jogador.getVida() + "/" + jogador.getVidaMaxima());
        lblPEJogador.setText("⚡ " + jogador.getPontosEsforco() + "/" + jogador.getPeMaximo());
        lblVidaInimigo.setText("☠ " + inimigo.getVida());
        lblElementoInimigo.setText(inimigo.getElemento().toString());

        if (jogador.isArmaAmaldicoada()) {
            lblAmaldicao.setText(jogador.getDescricaoAmaldicao());
            lblAmaldicao.setVisible(true);
        } else {
            lblAmaldicao.setVisible(false);
        }

        boolean podeAmaldicoar = jogador.podeAmaldicoarArma();
        btnAmaldicoar.setVisible(podeAmaldicoar);
        btnAmaldicoar.setManaged(podeAmaldicoar);
    }

    // ── NAVEGAÇÃO DO MENU ────────────────────────────────────────────────

    @FXML
    private void mostrarMenuAtaque() {
        SomUtil.tocarConfirmar();
        painelPrincipal.setVisible(false);
        painelPrincipal.setManaged(false);
        painelMenuAtaque.setVisible(true);
        painelMenuAtaque.setManaged(true);

        int custoEspecial = jogador.custoAtaqueEspecial();
        int custoRitual = 0;
        if (jogador.getRitualEquipado() != null) {
            custoRitual = jogador.getCustoPEComAfinidade(
                jogador.getRitualEquipado().getCustoPE(),
                jogador.getRitualEquipado().getElemento()
            );
        }

        btnAtaqueEspecial.setDisable(jogador.getPontosEsforco() < custoEspecial);
        btnRitual.setDisable(jogador.getRitualEquipado() == null || jogador.getPontosEsforco() < custoRitual);

        String dica = "Escolha seu tipo de ataque. ";
        if (custoRitual > 0 && custoRitual < (jogador.getRitualEquipado() != null ? jogador.getRitualEquipado().getCustoPE() : 0)) {
            dica += "✨ Afinidade ativa: custo de ritual reduzido!";
        }
        lblDicaAtaque.setText(dica);
    }

    @FXML
    private void fecharMenuAtaque() {
        SomUtil.tocarVoltar();
        painelMenuAtaque.setVisible(false);
        painelMenuAtaque.setManaged(false);
        painelPrincipal.setVisible(true);
        painelPrincipal.setManaged(true);
        lblDicaAtaque.setText("");
    }

    // ── AÇÕES DE COMBATE ─────────────────────────────────────────────────

    @FXML
    private void atacarComArma() {
        SomUtil.tocarConfirmar();
        desabilitarBotoes();
        int dano = jogador.calcularDanoFisico();
        Elemento elemAtaque = jogador.getElementoAtaqueAtual();
        double mult = ElementoService.calcularMultiplicador(elemAtaque, inimigo.getElemento());
        int danoFinal = (int)(dano * mult);

        exibirEfetividade(mult);
        animarAtaqueJogador(() -> {
            inimigo.setVida(Math.max(0, inimigo.getVida() - danoFinal));
            mostrarDanoFlutuante(imgInimigo, danoFinal, false);
            flashDano(imgInimigo);
            lblEventos.setText("⚔ Você atacou com " + jogador.getArmaEquipada().getNome()
                + " causando " + danoFinal + " de dano!");
            finalizarTurno();
        });
    }

    @FXML
    private void atacarEspecial() {
        int custo = jogador.custoAtaqueEspecial();
        if (jogador.getPontosEsforco() < custo) return;
        SomUtil.tocarConfirmar();
        desabilitarBotoes();
        jogador.setPontosEsforco(jogador.getPontosEsforco() - custo);
        int dano = jogador.calcularDanoEspecial();
        Elemento elemAtaque = jogador.getElementoAtaqueAtual();
        double mult = ElementoService.calcularMultiplicador(elemAtaque, inimigo.getElemento());
        int danoFinal = (int)(dano * mult);

        exibirEfetividade(mult);
        animarAtaqueEspecial(() -> {
            inimigo.setVida(Math.max(0, inimigo.getVida() - danoFinal));
            mostrarDanoFlutuante(imgInimigo, danoFinal, false);
            flashDano(imgInimigo);
            lblEventos.setText("💥 ATAQUE ESPECIAL! Você causou " + danoFinal + " de dano!");
            finalizarTurno();
        });
    }

    @FXML
    private void usarRitual() {
        if (jogador.getRitualEquipado() == null) return;

        int custo = jogador.getCustoPEComAfinidade(
            jogador.getRitualEquipado().getCustoPE(),
            jogador.getRitualEquipado().getElemento()
        );
        if (jogador.getPontosEsforco() < custo) return;
        SomUtil.tocarConfirmar();
        desabilitarBotoes();
        animarRitual(() -> {
            boolean ok = RitualService.usarRitual(jogador, inimigo);
            if (ok) {
                lblEventos.setText("☽ Você conjurou " + jogador.getRitualEquipado().getNome() + "!");
                flashRitual(imgInimigo);
            }
            finalizarTurno();
        });
    }

    @FXML
    private void amaldicoarArma() {
        if (jogador.amaldicoarArma()) {
            lblEventos.setText("🔥 Você amaldiçoou sua arma com o elemento " + jogador.getElemento() + "!");
            atualizarInterface();
            fecharMenuAtaque();
            // Efeito visual de amaldiçoar
            animarAmaldicao();
        }
    }

    // ── ANIMAÇÕES DE ATAQUE ──────────────────────────────────────────────

    /** Animação de ataque físico: jogador avança, bate e recua */
    private void animarAtaqueJogador(Runnable aoImpacto) {
        double avanco = 120;
        Timeline ataque = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgJogador.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(200),
                new KeyValue(imgJogador.translateXProperty(), avanco, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(280),
                new KeyValue(imgJogador.translateXProperty(), avanco - 10, Interpolator.EASE_OUT))
        );
        ataque.setOnFinished(e -> {
            aoImpacto.run();
            // Recua
            Timeline recuo = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(imgJogador.translateXProperty(), avanco - 10)),
                new KeyFrame(Duration.millis(250),
                    new KeyValue(imgJogador.translateXProperty(), 0, Interpolator.EASE_OUT))
            );
            recuo.setOnFinished(ev -> habilitarBotoes());
            recuo.play();
        });
        ataque.play();
    }

    /** Animação de ataque especial: jogador salta e brilha */
    private void animarAtaqueEspecial(Runnable aoImpacto) {
        double avanco = 130;
        Glow glow = new Glow(0.8);
        imgJogador.setEffect(glow);

        Timeline ataque = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgJogador.translateXProperty(), 0),
                new KeyValue(imgJogador.translateYProperty(), 0),
                new KeyValue(imgJogador.scaleXProperty(), 1.0),
                new KeyValue(imgJogador.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(150),
                new KeyValue(imgJogador.translateYProperty(), -20, Interpolator.EASE_OUT),
                new KeyValue(imgJogador.scaleXProperty(), 1.15),
                new KeyValue(imgJogador.scaleYProperty(), 1.15)),
            new KeyFrame(Duration.millis(300),
                new KeyValue(imgJogador.translateXProperty(), avanco, Interpolator.EASE_IN),
                new KeyValue(imgJogador.translateYProperty(), 0, Interpolator.EASE_IN),
                new KeyValue(imgJogador.scaleXProperty(), 1.0),
                new KeyValue(imgJogador.scaleYProperty(), 1.0))
        );
        ataque.setOnFinished(e -> {
            aoImpacto.run();
            imgJogador.setEffect(null);
            Timeline recuo = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(imgJogador.translateXProperty(), avanco)),
                new KeyFrame(Duration.millis(300),
                    new KeyValue(imgJogador.translateXProperty(), 0, Interpolator.EASE_OUT))
            );
            recuo.setOnFinished(ev -> habilitarBotoes());
            recuo.play();
        });
        ataque.play();
    }

    /** Animação de ritual: pulsação e brilho roxo */
    private void animarRitual(Runnable aoImpacto) {
        Glow glow = new Glow(1.0);
        ColorAdjust cor = new ColorAdjust();
        cor.setHue(0.7);
        imgJogador.setEffect(glow);

        Timeline ritual = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgJogador.scaleXProperty(), 1.0),
                new KeyValue(imgJogador.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(200),
                new KeyValue(imgJogador.scaleXProperty(), 1.2),
                new KeyValue(imgJogador.scaleYProperty(), 1.2)),
            new KeyFrame(Duration.millis(400),
                new KeyValue(imgJogador.scaleXProperty(), 0.95),
                new KeyValue(imgJogador.scaleYProperty(), 0.95)),
            new KeyFrame(Duration.millis(550),
                new KeyValue(imgJogador.scaleXProperty(), 1.0),
                new KeyValue(imgJogador.scaleYProperty(), 1.0))
        );
        ritual.setOnFinished(e -> {
            aoImpacto.run();
            imgJogador.setEffect(null);
            habilitarBotoes();
        });
        ritual.play();
    }

    /** Animação de amaldiçoar arma: flash vermelho */
    private void animarAmaldicao() {
        DropShadow fogo = new DropShadow(30, Color.ORANGERED);
        fogo.setSpread(0.5);
        imgJogador.setEffect(fogo);

        Timeline anim = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(imgJogador.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(150), new KeyValue(imgJogador.opacityProperty(), 0.4)),
            new KeyFrame(Duration.millis(300), new KeyValue(imgJogador.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(450), new KeyValue(imgJogador.opacityProperty(), 0.4)),
            new KeyFrame(Duration.millis(600), new KeyValue(imgJogador.opacityProperty(), 1.0))
        );
        anim.setOnFinished(e -> imgJogador.setEffect(null));
        anim.play();
    }

    // ── EFEITOS VISUAIS ──────────────────────────────────────────────────

    /** Flash vermelho no alvo ao receber dano */
    private void flashDano(ImageView alvo) {
        ColorAdjust flash = new ColorAdjust();
        flash.setSaturation(1.0);
        flash.setBrightness(0.8);
        alvo.setEffect(flash);

        // Tremor
        double origX = alvo.getTranslateX();
        Timeline tremor = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(alvo.translateXProperty(), origX)),
            new KeyFrame(Duration.millis(50), new KeyValue(alvo.translateXProperty(), origX + 8)),
            new KeyFrame(Duration.millis(100), new KeyValue(alvo.translateXProperty(), origX - 8)),
            new KeyFrame(Duration.millis(150), new KeyValue(alvo.translateXProperty(), origX + 5)),
            new KeyFrame(Duration.millis(200), new KeyValue(alvo.translateXProperty(), origX - 5)),
            new KeyFrame(Duration.millis(250), new KeyValue(alvo.translateXProperty(), origX))
        );
        tremor.setOnFinished(e -> {
            alvo.setEffect(null);
            // Restaura efeito de glow no inimigo
            if (alvo == imgInimigo) {
                DropShadow glowInimigo = new DropShadow(20, Color.RED);
                glowInimigo.setSpread(0.3);
                imgInimigo.setEffect(glowInimigo);
            }
        });
        tremor.play();
    }

    /** Flash de ritual no alvo */
    private void flashRitual(ImageView alvo) {
        DropShadow brilho = new DropShadow(40, Color.MEDIUMPURPLE);
        brilho.setSpread(0.6);
        alvo.setEffect(brilho);

        Timeline flash = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(alvo.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(100), new KeyValue(alvo.opacityProperty(), 0.3)),
            new KeyFrame(Duration.millis(200), new KeyValue(alvo.opacityProperty(), 1.0)),
            new KeyFrame(Duration.millis(300), new KeyValue(alvo.opacityProperty(), 0.5)),
            new KeyFrame(Duration.millis(400), new KeyValue(alvo.opacityProperty(), 1.0))
        );
        flash.setOnFinished(e -> {
            alvo.setEffect(null);
            if (alvo == imgInimigo) {
                DropShadow glowInimigo = new DropShadow(20, Color.RED);
                glowInimigo.setSpread(0.3);
                imgInimigo.setEffect(glowInimigo);
            }
        });
        flash.play();
    }

    /** Número de dano flutuando sobre o alvo */
    private void mostrarDanoFlutuante(ImageView alvo, int dano, boolean ehJogador) {
        if (rootPane == null) return;

        Label lblDano = new Label((ehJogador ? "-" : "-") + dano);
        lblDano.setStyle(
            "-fx-font-size: 28px; -fx-font-weight: bold; " +
            "-fx-text-fill: " + (ehJogador ? "#ff4444" : "#ffcc00") + "; " +
            "-fx-effect: dropshadow(gaussian, black, 4, 0.8, 0, 0);"
        );
        lblDano.setMouseTransparent(true);

        // Posiciona sobre o alvo
        double offsetX = ehJogador ? -80 : 80;
        lblDano.setTranslateX(alvo.getTranslateX() + offsetX);
        lblDano.setTranslateY(-80);
        StackPane.setAlignment(lblDano, Pos.CENTER);

        rootPane.getChildren().add(lblDano);

        // Animação: sobe e desaparece
        Timeline flutuante = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(lblDano.translateYProperty(), -80),
                new KeyValue(lblDano.opacityProperty(), 1.0),
                new KeyValue(lblDano.scaleXProperty(), 0.5),
                new KeyValue(lblDano.scaleYProperty(), 0.5)),
            new KeyFrame(Duration.millis(150),
                new KeyValue(lblDano.scaleXProperty(), 1.3),
                new KeyValue(lblDano.scaleYProperty(), 1.3)),
            new KeyFrame(Duration.millis(300),
                new KeyValue(lblDano.scaleXProperty(), 1.0),
                new KeyValue(lblDano.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.millis(800),
                new KeyValue(lblDano.translateYProperty(), -160, Interpolator.EASE_OUT),
                new KeyValue(lblDano.opacityProperty(), 0.0))
        );
        flutuante.setOnFinished(e -> rootPane.getChildren().remove(lblDano));
        flutuante.play();
    }

    // ── PROCESSAMENTO DO TURNO ───────────────────────────────────────────

    private void finalizarTurno() {
        atualizarInterface();
        fecharMenuAtaque();

        if (!inimigo.estaVivo()) {
            finalizarCombate(true);
            return;
        }

        // Contra-ataque do inimigo com animação
        PauseTransition pausa = new PauseTransition(Duration.millis(400));
        pausa.setOnFinished(e -> {
            int danoInimigo = inimigo.getDano();
            jogador.setVida(Math.max(0, jogador.getVida() - danoInimigo));

            // Animação do inimigo atacando
            animarAtaqueInimigo(() -> {
                mostrarDanoFlutuante(imgJogador, danoInimigo, true);
                flashDano(imgJogador);
                lblEventos.setText(lblEventos.getText()
                    + "\n💀 O inimigo contra-ataca causando " + danoInimigo + " de dano!");
                atualizarInterface();

                if (!jogador.estaVivo()) {
                    finalizarCombate(false);
                } else {
                    habilitarBotoes();
                }
            });
        });
        pausa.play();
    }

    /** Animação do inimigo avançando para atacar */
    private void animarAtaqueInimigo(Runnable aoImpacto) {
        double avanco = -120;
        Timeline ataque = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgInimigo.translateXProperty(), 0)),
            new KeyFrame(Duration.millis(200),
                new KeyValue(imgInimigo.translateXProperty(), avanco, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(280),
                new KeyValue(imgInimigo.translateXProperty(), avanco + 10, Interpolator.EASE_OUT))
        );
        ataque.setOnFinished(e -> {
            aoImpacto.run();
            Timeline recuo = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(imgInimigo.translateXProperty(), avanco + 10)),
                new KeyFrame(Duration.millis(250),
                    new KeyValue(imgInimigo.translateXProperty(), 0, Interpolator.EASE_OUT))
            );
            recuo.play();
        });
        ataque.play();
    }

    private void desabilitarBotoes() {
        btnAtacar.setDisable(true);
        btnFugir.setDisable(true);
        btnAtaqueArma.setDisable(true);
        btnAtaqueEspecial.setDisable(true);
        btnRitual.setDisable(true);
        btnAmaldicoar.setDisable(true);
    }

    private void habilitarBotoes() {
        btnAtacar.setDisable(false);
        btnFugir.setDisable(false);
        btnAtaqueArma.setDisable(false);
        btnAtaqueEspecial.setDisable(false);
        btnRitual.setDisable(false);
        // btnAmaldicoar é gerenciado separadamente
        atualizarInterface();
    }

    private void exibirEfetividade(double mult) {
        if (mult > 1.0) {
            lblEfetividade.setText("SUPER EFETIVO! (x" + mult + ")");
            lblEfetividade.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        } else if (mult < 1.0 && mult > 0) {
            lblEfetividade.setText("POUCO EFETIVO... (x" + mult + ")");
            lblEfetividade.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        } else {
            lblEfetividade.setText("");
        }
    }

    // ── FINALIZAR COMBATE ────────────────────────────────────────────────

    private void finalizarCombate(boolean vitoria) {
        painelPrincipal.setVisible(false);
        painelPrincipal.setManaged(false);
        painelMenuAtaque.setVisible(false);
        painelMenuAtaque.setManaged(false);

        btnVoltarMissao.setVisible(true);
        btnVoltarMissao.setManaged(true);
        btnVoltarMissao.toFront();

        if (vitoria) {
            // Animação de vitória: inimigo cai
            animarMorteInimigo();

            int xp = inimigo.getXpConcedido();
            jogador.ganharXp(xp);
            jogador.encerrarBatalha();

            int dinheiro = inimigo.isBossFinal()  ? 200
                         : inimigo.isBossMissao() ? 60 + MissaoService.getIndiceMissao(inimigo.getElemento()) * 20
                         : 8 + new java.util.Random().nextInt(13);
            jogador.adicionarDinheiro(dinheiro);

            if (inimigo.isBossMissao()) {
                int idx = MissaoService.getIndiceMissao(inimigo.getElemento());
                GameState.setBossMissaoDerrotado(idx, true);
                if (GameState.getMissaoAtual() != null) {
                    GameState.getMissaoAtual().concluir();
                }
                boolean todasConcluidas = MissaoService.campanhaConcluida();
                if (todasConcluidas) GameState.setBossDesbloqueado(true);
                GameContext.salvarProgressoCampanha();

                String msgExtra = todasConcluidas
                    ? "\n\n🔓 TODAS AS MISSÕES CONCLUÍDAS!\nO BOSS FINAL foi desbloqueado no menu!"
                    : "\n✔ Missão concluída! Volte ao menu para escolher a próxima.";
                lblEventos.setText("🏆 VITÓRIA! " + inimigo.getNome() + " foi derrotado!\n+"
                    + xp + " XP  |  +" + dinheiro + " moedas\n☠ BOSS DA MISSÃO DERROTADO!" + msgExtra);

            } else if (inimigo.isBossFinal()) {
                lblEventos.setText("🏆 VITÓRIA! " + inimigo.getNome() + " foi derrotado!\n+"
                    + xp + " XP  |  +" + dinheiro + " moedas\n💀 BOSS FINAL DERROTADO! Você completou o jogo!");
                btnVoltarMissao.setText("Ver Créditos");
            } else {
                GameState.setCombateVencidoNesteLocal(true);
                GameState.setInvestigouNesteAvanco(false);
                lblEventos.setText("🏆 VITÓRIA! " + inimigo.getNome() + " foi derrotado!\n+"
                    + xp + " XP  |  +" + dinheiro + " moedas\n\n🔍 Agora você pode INVESTIGAR o local.");
            }

        } else {
            // Animação de derrota: jogador cai
            animarMorteJogador();

            GameState.setVeioDeDerrota(true);
            GameState.setVeioDeFuga(true); // Garante que o fluxo de morte seja reconhecido
            int perda = jogador.getDinheiro() / 4;
            jogador.setDinheiro(jogador.getDinheiro() - perda);
            boolean contraBoss = inimigo.isBoss();
            GameState.perderPaginasParcial(contraBoss);
            String msgPerda = contraBoss ? "Você voltou com 4 páginas." : "Você perdeu metade das páginas.";
            lblEventos.setText("☠ DERROTA... Você sucumbiu ao paranormal.\n" + msgPerda + "\nPerdeu " + perda + " moedas.");
            btnVoltarMissao.setText("Voltar ao QG");
        }
    }

    /** Animação de morte do inimigo: cai e desaparece */
    private void animarMorteInimigo() {
        Timeline morte = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgInimigo.rotateProperty(), 0),
                new KeyValue(imgInimigo.opacityProperty(), 1.0),
                new KeyValue(imgInimigo.translateYProperty(), 0)),
            new KeyFrame(Duration.millis(200),
                new KeyValue(imgInimigo.rotateProperty(), 15)),
            new KeyFrame(Duration.millis(600),
                new KeyValue(imgInimigo.rotateProperty(), 90, Interpolator.EASE_IN),
                new KeyValue(imgInimigo.translateYProperty(), 60, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(900),
                new KeyValue(imgInimigo.opacityProperty(), 0.0))
        );
        morte.play();
    }

    /** Animação de morte do jogador: escurece e cai */
    private void animarMorteJogador() {
        ColorAdjust escurecer = new ColorAdjust();
        escurecer.setBrightness(0);
        escurecer.setSaturation(-1);
        imgJogador.setEffect(escurecer);

        Timeline morte = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(imgJogador.rotateProperty(), 0),
                new KeyValue(imgJogador.opacityProperty(), 1.0),
                new KeyValue(imgJogador.translateYProperty(), 0)),
            new KeyFrame(Duration.millis(400),
                new KeyValue(imgJogador.rotateProperty(), -90, Interpolator.EASE_IN),
                new KeyValue(imgJogador.translateYProperty(), 50, Interpolator.EASE_IN)),
            new KeyFrame(Duration.millis(700),
                new KeyValue(imgJogador.opacityProperty(), 0.0))
        );
        morte.play();
    }

    // ── OUTRAS AÇÕES ─────────────────────────────────────────────────────

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("COMBATE");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void fugir(ActionEvent event) {
        SomUtil.tocarVoltar();
        GameState.perderPaginasParcial(false);
        int perda = jogador.getDinheiro() / 10;
        jogador.setDinheiro(jogador.getDinheiro() - perda);

        jogador.encerrarBatalha();
        GameState.setVeioDeFuga(true);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    @FXML
    private void voltarMissao(ActionEvent event) {
        SomUtil.tocarConfirmar();
        if (inimigo != null && inimigo.isBossFinal()) {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/creditos.fxml");
        } else if (GameState.isVeioDeDerrota() || GameState.isVeioDeFuga() || !jogador.estaVivo()) {
            // Derrota: garante as flags e manda para status (tela de penalidade)
            GameState.setVeioDeDerrota(true);
            GameState.setVeioDeFuga(true);
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
        } else if (inimigo != null && inimigo.isBossMissao()) {
            GameState.setMissaoEmAndamento(false);
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }
}
