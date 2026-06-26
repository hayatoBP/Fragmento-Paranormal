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
    private static boolean investigouNesteAvanco     = false;
    private static boolean combateVencidoNesteLocal  = false;
    private static boolean missaoEmAndamento          = false;
    private static String  origemInventario           = "MISSAO";




    /** Páginas encontradas em ordem de descoberta — disponíveis para releitura. */
    private static final List<PaginaDiario> paginasEncontradas = new ArrayList<>();

    // ── Textos completos das 28 páginas (4 missões × 7) ──────────────
    private static final String[][] TEXTOS = {
        { // 0 — Sangue (Hospital Santa Agnes)
            """
PRONTUÁRIO MÉDICO 14-B

DOCUMENTO ANTIGO
Paciente apresenta comportamento agressivo incomum. Os sedativos não produzem efeito. Durante a madrugada tentou atacar dois enfermeiros e afirmou ouvir \"algo respirando dentro das paredes\". Solicito transferência imediata para observação especializada.

ANOTAÇÃO RECENTE
Os primeiros registros confirmam o aumento da influência paranormal. O local é mais promissor do que imaginávamos.""",
            """
REGISTRO DE OCORRÊNCIA

DOCUMENTO ANTIGO
Diversos pacientes relataram ouvir gritos vindos da ala cirúrgica desativada. A equipe realizou uma busca completa. Nenhuma pessoa foi encontrada. Os gritos continuaram durante toda a madrugada.

ANOTAÇÃO RECENTE
A concentração permanece estável mesmo após todos esses anos. Não será necessário buscar outra instalação.""",
            """
RELATÓRIO INTERNO

DOCUMENTO ANTIGO
O paciente da Sala 09 sofreu alterações físicas severas. Sua força aumentou drasticamente. Dois seguranças ficaram feridos tentando contê-lo. O diretor proibiu que o caso fosse divulgado.

ANOTAÇÃO RECENTE
As transformações observadas coincidem com os resultados esperados. Precisamos de mais amostras.""",
            """
FOLHA RASGADA

DOCUMENTO ANTIGO
...não importa quantas portas sejam trancadas. Continua aparecendo em lugares diferentes. Não sei mais se estamos lidando com um paciente.

ANOTAÇÃO RECENTE
O espécime original ainda parece influenciar o ambiente. Interessante.""",
            """
MEMORANDO DE EMERGÊNCIA

DOCUMENTO ANTIGO
Todas as atividades médicas foram suspensas. Os funcionários devem abandonar o prédio imediatamente. A situação está fora de controle.

ANOTAÇÃO RECENTE
O incidente explica o estado atual do hospital. Felizmente isso não interfere na pesquisa.""",
            """
RELATÓRIO DE PESQUISA

DOCUMENTO ANTIGO
[DOCUMENTO DANIFICADO]
Grande parte do texto foi destruída.

ANOTAÇÃO RECENTE
Primeira fase concluída. Os resultados superaram as expectativas. O potencial deste local é extraordinário. Talvez tenhamos encontrado o ambiente ideal.""",
            """
ANOTAÇÃO FINAL

DOCUMENTO ANTIGO
Se alguém encontrar isto...
Não volte para este hospital.
Não importa o que esteja procurando.
Não entre na ala subterrânea.

ANOTAÇÃO RECENTE
O confinamento falhou. Perdemos contato com a equipe enviada ao subsolo. O experimento tornou-se imprevisível. Estamos abandonando a instalação."""
        },
        { // 1 — Morte (Cemitério Esquecido)
            """
BOLETIM DE OCORRÊNCIA Nº 214/2016

DOCUMENTO ANTIGO
Data: 14 de setembro de 2016. Às 06h42, três adolescentes foram encontrados sem vida próximos ao antigo mausoléu da família Alencar. Não foram observados sinais de violência compatíveis com homicídio convencional. Apesar de possuírem entre 16 e 17 anos, apresentavam características semelhantes às de pessoas muito idosas. A pele estava profundamente enrugada, os cabelos completamente brancos e os tecidos musculares extremamente deteriorados.

ANOTAÇÃO ESCRITA À MÃO
Os registros oficiais foram mais úteis do que esperávamos. O evento deixou marcas permanentes. Ainda conseguimos sentir a mesma presença.""",
            """
DIÁRIO DO COVEIRO

DOCUMENTO ANTIGO
Faz quatro noites que escuto passos entre os túmulos depois que todos vão embora. Não vejo ninguém. Só escuto. Hoje encontrei flores recém-colocadas em um jazigo abandonado há décadas. Não havia pegadas na terra. Acho que vou pedir demissão.

ANOTAÇÃO RECENTE
Funcionários comuns sempre percebem os sintomas primeiro. A exposição prolongada aumenta significativamente a sensibilidade.""",
            """
RELATÓRIO DE CAMPO

DOCUMENTO RECENTE
A segunda instalação foi montada sem incidentes. A quantidade de energia residual supera nossas estimativas iniciais. Diferentemente do hospital, aqui o fenômeno permanece estável. Talvez finalmente tenhamos encontrado um ambiente adequado para os próximos testes.""",
            """
DOCUMENTO PARCIAL

DOCUMENTO RECENTE
Os resultados obtidos na Instalação Um foram suficientes para validar a hipótese. Cada manifestação possui propriedades próprias. Separadamente são interessantes. Em conjunto... ainda não temos dados suficientes.""",
            """
MEMORANDO INTERNO

DOCUMENTO RECENTE
Ordem do Conselho: Nenhum membro da equipe deve discutir o verdadeiro objetivo do projeto fora das reuniões autorizadas. Ainda existem pesquisadores que questionam nossos métodos. Quando os resultados aparecerem, entenderão que todos os sacrifícios foram necessários.""",
            """
PÁGINA RASURADA

DOCUMENTO RECENTE
A equipe insiste em chamar os espécimes de \"monstros\". Discordo. Não estamos criando monstros. Estamos observando a evolução natural do paranormal.""",
            """
ÚLTIMO REGISTRO

DOCUMENTO RECENTE
O experimento voltou a apresentar comportamento imprevisível. Perdemos quatro pesquisadores nas últimas quarenta e oito horas. Encerramos as atividades nesta instalação. A próxima fase já está preparada."""
        },
        { // 2 — Energia (Parque de Diversões)
            """
RELATÓRIO DOS BOMBEIROS

DOCUMENTO ANTIGO
O incêndio começou às 19h17 após uma explosão na central elétrica. Diversas testemunhas afirmam que os brinquedos permaneceram funcionando mesmo sem fornecimento de energia. Algumas equipes se recusaram a retornar ao parque após o combate ao incêndio.

ANOTAÇÃO RECENTE
Excelente. O caos permanece impregnado no ambiente.""",
            """
BILHETE ENCONTRADO

DOCUMENTO ANTIGO
Pai,
Se você encontrar este papel... Estou escondido dentro da casa dos espelhos. As luzes não param de piscar. Tem alguém me chamando pelo meu nome.

ANOTAÇÃO RECENTE
Casos como este ajudam a compreender os efeitos da exposição prolongada.""",
            """
RELATÓRIO TÉCNICO

DOCUMENTO RECENTE
Instalação Três operacional. A energia produz comportamento completamente diferente das pesquisas anteriores. O ambiente muda constantemente. Instrumentos apresentam leituras impossíveis.""",
            """
MEMORANDO

DOCUMENTO RECENTE
O Projeto Gênese entrou oficialmente na Fase II. A coleta de dados segue conforme o cronograma. Em breve teremos material suficiente para iniciar a integração.""",
            """
DIÁRIO DE PESQUISA

DOCUMENTO RECENTE
Quanto mais estudamos o Outro Lado... Mais evidente se torna que ele não é caótico. Existe um padrão. Ainda não conseguimos enxergá-lo por completo.""",
            """
RELATÓRIO RESTRITO

DOCUMENTO RECENTE
Algumas equipes questionam por que escolhemos locais abandonados. A resposta é simples. Lugares onde ocorreram tragédias preservam melhor a influência paranormal.""",
            """
ORDEM DE TRANSFERÊNCIA

DOCUMENTO RECENTE
Encerrar imediatamente todas as pesquisas nesta instalação. Preparar transporte do espécime. O Conselho autorizou a próxima etapa."""
        },
        { // 3 — Conhecimento (Mansão)
            """
DIÁRIO DE UM PESQUISADOR

DOCUMENTO ANTIGO
Passamos anos estudando o paranormal. No início queríamos respostas. Agora só queremos continuar perguntando. Às vezes penso que a mansão responde antes mesmo de terminarmos as perguntas.

ANOTAÇÃO RECENTE
Os antigos proprietários compreenderam parte da verdade. Infelizmente enlouqueceram antes de chegar ao fim.""",
            """
REGISTRO DA SOCIEDADE

DOCUMENTO ANTIGO
Conhecimento não possui limite. O limite pertence apenas à mente humana.

ANOTAÇÃO RECENTE
Talvez eles estivessem certos.""",
            """
RELATÓRIO DE PESQUISA

DOCUMENTO RECENTE
Sangue. Morte. Energia. Todos apresentaram resultados superiores às previsões iniciais. Resta concluir a quarta etapa.""",
            """
DOCUMENTO CONFIDENCIAL

DOCUMENTO RECENTE
Pela primeira vez conseguimos observar interação entre os quatro conjuntos de dados. A hipótese principal permanece válida.""",
            """
ATA DE REUNIÃO

DOCUMENTO RECENTE
O Conselho aprovou por unanimidade a continuidade do Projeto Gênese. Não existem mais dúvidas. Estamos próximos de provar nossa teoria.""",
            """
REGISTRO PESSOAL

DOCUMENTO RECENTE
Alguns pesquisadores acreditam que estamos indo longe demais. Discordo. Toda descoberta importante exigiu coragem. A história lembrará nossos nomes.""",
            """
ÚLTIMA PÁGINA

DOCUMENTO RECENTE
Todos os componentes foram obtidos. O protótipo está pronto para ser transferido. Se nossos cálculos estiverem corretos...

O próximo registro desta pesquisa marcará o início de uma nova era para a humanidade."""
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
    public static boolean isInvestigouNesteAvanco()         { return investigouNesteAvanco; }
    public static void    setInvestigouNesteAvanco(boolean v){ investigouNesteAvanco = v; }
    public static boolean isCombateVencidoNesteLocal()      { return combateVencidoNesteLocal; }
    public static void    setCombateVencidoNesteLocal(boolean v){ combateVencidoNesteLocal = v; }
    public static boolean isMissaoEmAndamento()             { return missaoEmAndamento; }
    public static void    setMissaoEmAndamento(boolean v)   { missaoEmAndamento = v; }
    public static String  getOrigemInventario()             { return origemInventario; }
    public static void    setOrigemInventario(String o)     { origemInventario = o; }



    /**
     * Perde metade das páginas coletadas na missão atual.
     * Se for contra um Boss, o jogador volta com exatamente 4 páginas.
     */
    public static void perderPaginasParcial(boolean contraBoss) {
        if (missaoAtual == null) return;
        // getOrdem() retorna 0-3 diretamente (índice correto)
        int idx = missaoAtual.getOrdem();
        if (idx >= 0 && idx < TOTAL_MISSOES) {
            int totalAtual = (int) missaoAtual.getLocais().stream()
                    .filter(l -> l.isPaginaEncontrada()).count();
            int novasPaginas;

            if (contraBoss) {
                // Morreu pro boss → volta com até 4 páginas
                novasPaginas = Math.min(4, totalAtual);
            } else {
                // Morreu/fugiu comum → perde metade
                novasPaginas = totalAtual / 2;
            }

            // Remove todas as páginas da missão atual e readiciona apenas as que sobraram
            paginasEncontradas.removeIf(p -> p.titulo().contains(missaoAtual.getNome()));
            for (int i = 0; i < missaoAtual.getLocais().size(); i++) {
                missaoAtual.getLocais().get(i).setPaginaEncontrada(false);
            }
            for (int i = 0; i < novasPaginas && i < missaoAtual.getLocais().size(); i++) {
                missaoAtual.getLocais().get(i).setPaginaEncontrada(true);
                registrarPagina(idx, i + 1);
            }

            // Volta o localAtual para onde tem progresso
            missaoAtual.setLocalAtual(Math.min(novasPaginas, missaoAtual.getLocais().size() - 1));
        }

        // Reseta estado de combate
        combateVencidoNesteLocal = false;
        investigouNesteAvanco    = false;

        // Recupera vida/PE ao voltar ao QG
        if (com.mycompany.fragmentoparanormal.controller.GameContext.jogadorAtual != null) {
            com.mycompany.fragmentoparanormal.controller.GameContext.jogadorAtual.resetarParaMissao();
        }
    }

    public static void resetar() {
        missaoAtual = null; veioDeFuga = false; veioDeDerrota = false;
        bossDesbloqueado = false; investigouNesteAvanco = false;
        combateVencidoNesteLocal = false;
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