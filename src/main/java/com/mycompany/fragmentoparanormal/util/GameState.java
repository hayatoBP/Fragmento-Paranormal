package com.mycompany.fragmentoparanormal.util;

import com.mycompany.fragmentoparanormal.model.Missao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameState {

    private static Missao  missaoAtual;
    private static boolean veioDeFuga        = false;
    private static boolean veioDeDerrota     = false;
    private static boolean bossDesbloqueado  = false;
    // Controla se o boss de cada missão (0–3) já foi derrotado nesta sessão
    private static final int   TOTAL_MISSOES     = 4;
    private static final boolean[] bossMissaoDerrotado = new boolean[TOTAL_MISSOES];
    private static boolean investigouNesteAvanco = false;
    private static boolean missaoEmAndamento = false;
    private static String  origemInventario  = "MISSAO";




    /** Páginas encontradas em ordem de descoberta — disponíveis para releitura. */
    private static final List<PaginaDiario> paginasEncontradas = new ArrayList<>();

    // ── Textos completos das 28 páginas (4 missões × 7) ──────────────
    private static final String[][] TEXTOS = {
        { // 0 — Sangue
            "\"O hospital estava vazio quando cheguei. Mas as marcas nas paredes contavam outra história — alguém sangrou aqui por horas antes de desaparecer.\"\n— Diário, Página 1 [Sangue]",
            "\"Encontrei um prontuário médico rasgado. O paciente respondia por 'Sujeito Alfa'. Último registro: 'estado irreversível'. Nenhuma data.\"\n— Diário, Página 2 [Sangue]",
            "\"As criaturas daqui se alimentam de memória — não de carne. Cada ferida que causam apaga um fragmento de quem você é.\"\n— Diário, Página 3 [Sangue]",
            "\"Lívia disse que a medicina e o paranormal não eram diferentes. Ambos tentavam consertar o que não deveria estar quebrado. Ela estava certa — da pior forma.\"\n— Diário, Página 4 [Sangue]",
            "\"O elemento Sangue não representa violência. Representa vínculo. Quem o invoca se conecta a tudo que já viveu — e a tudo que perdeu.\"\n— Diário, Página 5 [Sangue]",
            "\"Vi meu próprio reflexo numa poça de sangue seco. Ele piscou antes de mim.\"\n— Diário, Página 6 [Sangue]",
            "\"Saí do hospital com mais dúvidas do que respostas. Mas levei algo comigo que não estava lá quando entrei. Algo que ainda não consigo nomear.\"\n— Diário, Página 7 [Sangue]"
        },
        { // 1 — Morte
            "\"O cemitério tem lápides sem nome. Centenas delas. Alguém as colocou aqui deliberadamente — para honrar os que não podiam ser lembrados.\"\n— Diário, Página 1 [Morte]",
            "\"Corvin era coveiro antes de se tornar agente. Dizia que a morte não era um fim — era uma mudança de endereço. Sinto falta do cinismo dele.\"\n— Diário, Página 2 [Morte]",
            "\"As criaturas aqui não atacam por instinto. Elas esperam. Observam. Escolhem o momento em que você baixa a guarda.\"\n— Diário, Página 3 [Morte]",
            "\"Encontrei flores frescas sobre uma das lápides sem nome. Alguém ainda vem aqui. Alguém ainda lembra.\"\n— Diário, Página 4 [Morte]",
            "\"O elemento Morte não destrói. Ele preserva o que foi, exatamente como era no último momento.\"\n— Diário, Página 5 [Morte]",
            "\"Ouvi passos no necrotério. Eram meus próprios — ecoando de dez minutos atrás.\"\n— Diário, Página 6 [Morte]",
            "\"Deixei o cemitério ao amanhecer. As lápides sem nome tinham nomes agora. Todos iguais. Todos o meu.\"\n— Diário, Página 7 [Morte]"
        },
        { // 2 — Energia
            "\"O laboratório ainda tem eletricidade — mas nenhuma fonte de energia conhecida está conectada.\"\n— Diário, Página 1 [Energia]",
            "\"Mara acreditava que o paranormal era física mal compreendida. Seus experimentos a consumiram.\"\n— Diário, Página 2 [Energia]",
            "\"As distorções aqui dobram o tempo localmente. Entrei às 14h. A vela já está completamente consumida.\"\n— Diário, Página 3 [Energia]",
            "\"Os inimigos de energia não causam dano físico. Eles drenam — PE, força de vontade, certeza.\"\n— Diário, Página 4 [Energia]",
            "\"Encontrei as anotações de Mara. A última termina no meio de uma frase.\"\n— Diário, Página 5 [Energia]",
            "\"As luzes piscaram em código Morse. Traduzi: 'VOCÊ JÁ ESTEVE AQUI ANTES'.\"\n— Diário, Página 6 [Energia]",
            "\"Saí do laboratório carregando uma carga estática que não dissipa. Toda tela que toco mostra o rosto de Mara.\"\n— Diário, Página 7 [Energia]"
        },
        { // 3 — Conhecimento
            "\"A biblioteca parece normal à primeira vista. Mas todos os livros têm o mesmo número de páginas. Exatamente 312. Todos.\"\n— Diário, Página 1 [Conhecimento]",
            "\"Os cultos que operaram aqui adoravam informação. Acreditavam que saber o suficiente tornava qualquer coisa possível.\"\n— Diário, Página 2 [Conhecimento]",
            "\"Encontrei documentos proibidos selados com cera negra. O símbolo era o mesmo dos quatro círculos conectados.\"\n— Diário, Página 3 [Conhecimento]",
            "\"O elemento Conhecimento não dá poder diretamente. Ele mostra onde o poder já existe — e como tomá-lo.\"\n— Diário, Página 4 [Conhecimento]",
            "\"Li o suficiente para entender o que o autor do diário estava tentando fazer.\"\n— Diário, Página 5 [Conhecimento]",
            "\"A última seção da biblioteca está trancada por dentro. Empurrei a porta. Ela abriu. Não havia ninguém.\"\n— Diário, Página 6 [Conhecimento]",
            "\"Encontrei o espelho. Vi o rosto do autor. Era alguém da Ordem — alguém que me enviou para cá.\n\nEle sabia. Ele sempre soube.\"\n— Diário, Página 7 [Conhecimento] ⚠ ÚLTIMA PÁGINA"
        }
    };

    // ── Páginas do diário ─────────────────────────────────────────────
    public static void registrarPagina(int missaoIdx, int numeroPagina) {
        // O número da página agora é a ordem do local + 1
        String titulo = missaoAtual.getNome() + " — " + missaoAtual.getLocais().get(numeroPagina - 1).getNome();
        String texto  = TEXTOS[missaoIdx][numeroPagina - 1];
        boolean jaExiste = paginasEncontradas.stream().anyMatch(p -> p.titulo().equals(titulo));
        if (!jaExiste) paginasEncontradas.add(new PaginaDiario(titulo, texto));
    }

    public static List<PaginaDiario> getPaginasEncontradas() {
        return Collections.unmodifiableList(paginasEncontradas);
    }

    public static String getTextoPagina(int missaoIdx, int numeroPagina) {
        return TEXTOS[missaoIdx][numeroPagina - 1];
    }

    private static String nomeMissao(int idx) {
        return switch (idx) {
            case 0 -> "Sangue"; case 1 -> "Morte";
            case 2 -> "Energia"; default -> "Conhecimento";
        };
    }

    // ── Getters / setters padrão ──────────────────────────────────────
    public static Missao getMissaoAtual()                  { return missaoAtual; }
    public static void   setMissaoAtual(Missao m)          { missaoAtual = m; }
    public static boolean isVeioDeFuga()                   { return veioDeFuga; }
    public static void    setVeioDeFuga(boolean v)         { veioDeFuga = v; }
    public static boolean isVeioDeDerrota()                { return veioDeDerrota; }
    public static void    setVeioDeDerrota(boolean v)      { veioDeDerrota = v; }
    public static boolean isBossDesbloqueado()             { return bossDesbloqueado; }
    public static void    setBossDesbloqueado(boolean v)   { bossDesbloqueado = v; }
    public static boolean isInvestigouNesteAvanco()        { return investigouNesteAvanco; }
    public static void    setInvestigouNesteAvanco(boolean v){ investigouNesteAvanco = v; }
    public static boolean isMissaoEmAndamento()            { return missaoEmAndamento; }
    public static void    setMissaoEmAndamento(boolean v)  { missaoEmAndamento = v; }
    public static String  getOrigemInventario()            { return origemInventario; }
    public static void    setOrigemInventario(String o)    { origemInventario = o; }



    /**
     * Perde metade das páginas coletadas na missão atual.
     * Se for contra um Boss, o jogador volta com exatamente 4 páginas.
     */
    public static void perderPaginasParcial(boolean contraBoss) {
        if (missaoAtual == null) return;
            int idx = missaoAtual.getOrdem() - 1;
            if (idx >= 0 && idx < TOTAL_MISSOES) {
                int totalAtual = (int) missaoAtual.getLocais().stream().filter(l -> l.isPaginaEncontrada()).count();
            int novasPaginas;

            if (contraBoss) {
                // Se morreu pro boss, volta com 4 páginas
                novasPaginas = 4;
            } else {
                // Se morreu/fugiu comum, perde metade (arredondado pra cima na perda)
                // Ex: 1->0, 2->1, 3->1, 4->2, 5->2, 6->3, 7->3
                novasPaginas = totalAtual / 2;
            }

            // Remove todas as páginas da missão atual e readiciona apenas as que sobraram
            paginasEncontradas.removeIf(p -> p.titulo().contains(missaoAtual.getNome()));
            for (int i = 0; i < missaoAtual.getLocais().size(); i++) {
                missaoAtual.getLocais().get(i).setPaginaEncontrada(false);
            }
            for (int i = 0; i < novasPaginas; i++) {
                missaoAtual.getLocais().get(i).setPaginaEncontrada(true);
                registrarPagina(missaoAtual.getOrdem() - 1, i + 1); // +1 para ordem e número da página
            }
        }
        
        // Recupera vida/PE ao voltar ao QG
        if (com.mycompany.fragmentoparanormal.controller.GameContext.jogadorAtual != null) {
            com.mycompany.fragmentoparanormal.controller.GameContext.jogadorAtual.resetarParaMissao();
        }
    }

    public static void resetar() {
        missaoAtual = null; veioDeFuga = false; veioDeDerrota = false;
        bossDesbloqueado = false; investigouNesteAvanco = false;
        missaoEmAndamento = false; origemInventario = "MISSAO";

        Arrays.fill(bossMissaoDerrotado, false);
        paginasEncontradas.clear();
    }

    // ── Boss de missão ────────────────────────────────────────────────
    public static boolean isBossMissaoDerrotado(int i) {
        return i >= 0 && i < 4 && bossMissaoDerrotado[i];
    }
    public static void setBossMissaoDerrotado(int i, boolean v) {
        if (i >= 0 && i < 4) bossMissaoDerrotado[i] = v;
    }

    public static int getTotalPaginasJogo() {
        return TEXTOS[0].length * TOTAL_MISSOES;
    }

    public record PaginaDiario(String titulo, String texto) {}
}
