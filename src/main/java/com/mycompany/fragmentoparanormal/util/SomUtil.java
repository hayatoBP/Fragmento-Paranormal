package com.mycompany.fragmentoparanormal.util;

import javafx.scene.media.AudioClip;

/**
 * Utilitário para reprodução de efeitos sonoros do jogo.
 * Gerencia os sons de confirmação (agudo) e voltar (grave).
 */
public class SomUtil {

    private static AudioClip somConfirmar;
    private static AudioClip somVoltar;
    private static boolean somAtivado = true;

    static {
        try {
            var urlConfirmar = SomUtil.class.getResource(
                "/com/mycompany/fragmentoparanormal/sounds/click_confirmar.wav");
            if (urlConfirmar != null) {
                somConfirmar = new AudioClip(urlConfirmar.toExternalForm());
                somConfirmar.setVolume(0.7);
            }

            var urlVoltar = SomUtil.class.getResource(
                "/com/mycompany/fragmentoparanormal/sounds/click_voltar.wav");
            if (urlVoltar != null) {
                somVoltar = new AudioClip(urlVoltar.toExternalForm());
                somVoltar.setVolume(0.7);
            }
        } catch (Exception e) {
            System.err.println("[SomUtil] Erro ao carregar sons: " + e.getMessage());
        }
    }

    /**
     * Toca o som de confirmação (agudo) — usado ao confirmar ações,
     * avançar, atacar, comprar, etc.
     */
    public static void tocarConfirmar() {
        if (somAtivado && somConfirmar != null) {
            somConfirmar.play();
        }
    }

    /**
     * Toca o som de voltar (grave) — usado ao voltar para telas anteriores,
     * cancelar, fechar menus, etc.
     */
    public static void tocarVoltar() {
        if (somAtivado && somVoltar != null) {
            somVoltar.play();
        }
    }

    public static void setSomAtivado(boolean ativado) {
        somAtivado = ativado;
    }

    public static boolean isSomAtivado() {
        return somAtivado;
    }
}
