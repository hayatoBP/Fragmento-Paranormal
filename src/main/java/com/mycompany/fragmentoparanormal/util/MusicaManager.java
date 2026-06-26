package com.mycompany.fragmentoparanormal.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Gerenciador central de música do jogo Fragmento Paranormal.
 *
 * Regras de reprodução:
 *  - TelaInicial       → MusicaMenuInicial
 *  - Ivete / Loja      → MusicaIvete
 *  - Tela de Missão    → MusicaDefault{Elemento}
 *  - Combate normal    → MusicaDefault{Elemento}
 *  - Combate boss missão → MusicaBoss{Elemento}
 *  - Combate boss final  → MusicaBossFinal
 *  - Créditos          → MusicaCreditos
 *  - Todas as demais   → MusicaResto (menuMissoes, status, loja, inventario, etc.)
 *
 * A música toca em loop contínuo e transições entre telas não reiniciam
 * a mesma faixa — apenas trocam quando necessário.
 */
public class MusicaManager {

    private static MediaPlayer playerAtual = null;
    private static String faixaAtual = null;
    private static double volume = 0.5;
    private static boolean ativado = true;

    private static final String BASE = "/com/mycompany/fragmentoparanormal/sounds/";

    // ── Constantes de faixas ─────────────────────────────────────────────
    public static final String MENU_INICIAL      = "MusicaMenuInicial.mp3";
    public static final String IVETE             = "MusicaIvete.mp3";
    public static final String RESTO             = "MusicaResto.mp3";
    public static final String CREDITOS          = "MusicaCreditos.mp3";
    public static final String BOSS_FINAL        = "MusicaBossFinal.mp3";
    public static final String BOSS_SANGUE       = "MusicaBossSangue.mp3";
    public static final String BOSS_MORTE        = "MusicaBossMorte.mp3";
    public static final String BOSS_ENERGIA      = "MusicaBossEnergia.mp3";
    public static final String BOSS_CONHECIMENTO = "MusicaBossConhecimento.mp3";
    public static final String DEFAULT_SANGUE       = "MusicaDefaultSangue.mp3";
    public static final String DEFAULT_MORTE        = "MusicaDefaultMorte.mp3";
    public static final String DEFAULT_ENERGIA      = "MusicaDefaultEnergia.mp3";
    public static final String DEFAULT_CONHECIMENTO = "MusicaDefaultConhecimento.mp3";

    /**
     * Toca a faixa especificada. Se já estiver tocando a mesma faixa, não faz nada.
     * @param nomeFaixa nome do arquivo (ex: MusicaResto.mp3)
     */
    public static void tocar(String nomeFaixa) {
        if (!ativado) return;
        if (nomeFaixa == null) return;
        if (nomeFaixa.equals(faixaAtual) && playerAtual != null
                && playerAtual.getStatus() == MediaPlayer.Status.PLAYING) {
            return; // já está tocando a mesma faixa
        }
        pararAtual();
        try {
            var url = MusicaManager.class.getResource(BASE + nomeFaixa);
            if (url == null) {
                System.err.println("[Musica] Arquivo não encontrado: " + nomeFaixa);
                return;
            }
            Media media = new Media(url.toExternalForm());
            playerAtual = new MediaPlayer(media);
            playerAtual.setVolume(volume);
            playerAtual.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
            playerAtual.play();
            faixaAtual = nomeFaixa;
            System.out.println("[Musica] Tocando: " + nomeFaixa);
        } catch (Exception e) {
            System.err.println("[Musica] Erro ao tocar " + nomeFaixa + ": " + e.getMessage());
        }
    }

    /** Para a música atual imediatamente. */
    public static void pararAtual() {
        if (playerAtual != null) {
            try {
                playerAtual.stop();
                playerAtual.dispose();
            } catch (Exception ignored) {}
            playerAtual = null;
        }
        faixaAtual = null;
    }

    /** Toca a MusicaResto (usada na maioria das telas que não são exceção). */
    public static void tocarResto() {
        tocar(RESTO);
    }

    /** Toca a música do menu inicial. */
    public static void tocarMenuInicial() {
        tocar(MENU_INICIAL);
    }

    /** Toca a música da Ivete/Loja. */
    public static void tocarIvete() {
        tocar(IVETE);
    }

    /** Toca a música de créditos. */
    public static void tocarCreditos() {
        tocar(CREDITOS);
    }

    /** Toca a música de boss final. */
    public static void tocarBossFinal() {
        tocar(BOSS_FINAL);
    }

    /**
     * Toca a música de missão padrão para o elemento dado.
     * @param elemento nome do elemento em maiúsculas (ex: "SANGUE")
     */
    public static void tocarMissao(String elemento) {
        if (elemento == null) { tocarResto(); return; }
        String faixa = switch (elemento.toUpperCase()) {
            case "SANGUE"       -> DEFAULT_SANGUE;
            case "MORTE"        -> DEFAULT_MORTE;
            case "ENERGIA"      -> DEFAULT_ENERGIA;
            case "CONHECIMENTO" -> DEFAULT_CONHECIMENTO;
            default             -> RESTO;
        };
        tocar(faixa);
    }

    /**
     * Toca a música de boss de missão para o elemento dado.
     * @param elemento nome do elemento em maiúsculas
     */
    public static void tocarBossMissao(String elemento) {
        if (elemento == null) { tocarResto(); return; }
        String faixa = switch (elemento.toUpperCase()) {
            case "SANGUE"       -> BOSS_SANGUE;
            case "MORTE"        -> BOSS_MORTE;
            case "ENERGIA"      -> BOSS_ENERGIA;
            case "CONHECIMENTO" -> BOSS_CONHECIMENTO;
            default             -> RESTO;
        };
        tocar(faixa);
    }

    public static void setVolume(double v) {
        volume = Math.max(0.0, Math.min(1.0, v));
        if (playerAtual != null) playerAtual.setVolume(volume);
    }

    public static double getVolume() { return volume; }

    public static void setAtivado(boolean a) {
        ativado = a;
        if (!a) pararAtual();
    }

    public static boolean isAtivado() { return ativado; }

    public static String getFaixaAtual() { return faixaAtual; }
}
