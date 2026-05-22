package com.mycompany.fragmentoparanormal.util;

import com.mycompany.fragmentoparanormal.model.Missao;

public class GameState {

    private static Missao missaoAtual;
    private static boolean veioDeFuga = false;
    private static boolean bossDesbloqueado = false;

    // Controla se o jogador já investigou neste avanço (persiste ao abrir inventário)
    private static boolean investigouNesteAvanco = false;

    // Indica se a missão já foi iniciada (impede reset de PE/vida ao voltar do inventário)
    private static boolean missaoEmAndamento = false;

    /**
     * Tela de origem quando o inventário foi aberto.
     * "MISSAO" ou "COMBATE" — usado para retornar ao local correto.
     */
    private static String origemInventario = "MISSAO";

    // 4 missões × 7 páginas cada = 28 total
    // índice: 0=Sangue, 1=Morte, 2=Energia, 3=Conhecimento
    private static final int[] paginasPorMissao = new int[4];
    private static final int PAGINAS_POR_MISSAO = 7;
    private static final int TOTAL_MISSOES = 4;

    public static Missao getMissaoAtual()              { return missaoAtual; }
    public static void setMissaoAtual(Missao m)        { missaoAtual = m; }

    public static boolean isVeioDeFuga()               { return veioDeFuga; }
    public static void setVeioDeFuga(boolean v)        { veioDeFuga = v; }

    public static boolean isBossDesbloqueado()         { return bossDesbloqueado; }
    public static void setBossDesbloqueado(boolean v)  { bossDesbloqueado = v; }

    public static int getPaginasMissao(int indice)     { return paginasPorMissao[indice]; }

    public static void incrementarPaginaMissao(int indice) {
        if (indice >= 0 && indice < TOTAL_MISSOES
                && paginasPorMissao[indice] < PAGINAS_POR_MISSAO) {
            paginasPorMissao[indice]++;
        }
    }

    /** Total de páginas coletadas em TODAS as missões (máx. 28). */
    public static int totalPaginasColetadas() {
        int total = 0;
        for (int p : paginasPorMissao) total += p;
        return total;
    }

    /** Páginas coletadas na missão de índice i como texto "x/7". */
    public static String progressoMissao(int indice) {
        return paginasPorMissao[indice] + "/" + PAGINAS_POR_MISSAO;
    }

    public static int getPaginasPorMissao()    { return PAGINAS_POR_MISSAO; }
    public static int getTotalPaginasJogo()    { return PAGINAS_POR_MISSAO * TOTAL_MISSOES; }

    public static boolean isInvestigouNesteAvanco()           { return investigouNesteAvanco; }
    public static void setInvestigouNesteAvanco(boolean v)    { investigouNesteAvanco = v; }

    public static boolean isMissaoEmAndamento()               { return missaoEmAndamento; }
    public static void setMissaoEmAndamento(boolean v)        { missaoEmAndamento = v; }

    public static String getOrigemInventario()                { return origemInventario; }
    public static void setOrigemInventario(String origem)     { origemInventario = origem; }
}
