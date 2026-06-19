package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Ritual;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TipoHabilidade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Catálogo completo de rituais do Ocultista.
 *
 * Cada elemento tem 3 rituais: FRACO → MÉDIO → FORTE.
 * O desbloqueio segue a cadeia: fraco → médio → forte.
 *
 * Fórmulas de dano (calculadas em tempo de combate no RitualService):
 *   Fraco  : base + poderParanormal
 *   Médio  : base + poderParanormal × 1.5
 *   Forte  : base + poderParanormal × 2.0
 *
 * Os campos dano/cura em Ritual.java armazenam apenas a parte BASE.
 * O multiplicador de Poder Paranormal é armazenado em multiplicadorPP.
 */
public class CatalogoRituaisService {

    public record EntradaRitual(Ritual ritual, TipoHabilidade grau, Elemento elemento,
                                 double multiplicadorPP) {}

    private static final List<EntradaRitual> CATALOGO = new ArrayList<>();

    static {
        // ── SANGUE ──────────────────────────────────────────────────
        CATALOGO.add(new EntradaRitual(
            new Ritual("Golpe Hemorrágico",  5, 25, 0, Elemento.SANGUE, "Um golpe que faz o sangue do inimigo ferver e jorrar."),
            TipoHabilidade.FRACA, Elemento.SANGUE, 1.0));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Explosão de Carne",  10, 45, 0, Elemento.SANGUE, "O corpo do inimigo se rompe em uma explosão visceral."),
            TipoHabilidade.MEDIA, Elemento.SANGUE, 1.5));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Banquete Carmesim", 18, 85, 0, Elemento.SANGUE, "Você drena a essência vital do inimigo em um massacre."),
            TipoHabilidade.FORTE, Elemento.SANGUE, 2.0));

        // ── MORTE ────────────────────────────────────────────────────
        CATALOGO.add(new EntradaRitual(
            new Ritual("Decadenza",               5, 22, 0, Elemento.MORTE, "Acelera o processo de apodrecimento do alvo."),
            TipoHabilidade.FRACA, Elemento.MORTE, 1.0));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Envelhecimento Acelerado", 10, 42, 0, Elemento.MORTE, "O tempo passa em segundos para o corpo do inimigo."),
            TipoHabilidade.MEDIA, Elemento.MORTE, 1.5));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Fim Inevitável",          18, 80, 0, Elemento.MORTE, "Você convoca o vazio para reclamar a existência do alvo."),
            TipoHabilidade.FORTE, Elemento.MORTE, 2.0));

        // ── ENERGIA ──────────────────────────────────────────────────
        CATALOGO.add(new EntradaRitual(
            new Ritual("Descarga Elétrica",   6, 28, 0, Elemento.ENERGIA, "Uma arco voltaico atinge o inimigo com precisão."),
            TipoHabilidade.FRACA, Elemento.ENERGIA, 1.0));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Tempestade Caótica",  12, 52, 0, Elemento.ENERGIA, "Raios imprevisíveis castigam todos ao redor."),
            TipoHabilidade.MEDIA, Elemento.ENERGIA, 1.5));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Ruptura Energética", 20, 95, 0, Elemento.ENERGIA, "Uma explosão de energia pura desintegra a matéria."),
            TipoHabilidade.FORTE, Elemento.ENERGIA, 2.0));

        // ── CONHECIMENTO ─────────────────────────────────────────────
        CATALOGO.add(new EntradaRitual(
            new Ritual("Lâmina Mental",        5, 22, 0, Elemento.CONHECIMENTO, "Uma projeção de puro pensamento corta a mente do alvo."),
            TipoHabilidade.FRACA, Elemento.CONHECIMENTO, 1.0));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Colapso Cognitivo",    10, 44, 0, Elemento.CONHECIMENTO, "O excesso de informação destrói a consciência do inimigo."),
            TipoHabilidade.MEDIA, Elemento.CONHECIMENTO, 1.5));

        CATALOGO.add(new EntradaRitual(
            new Ritual("Revelação Absoluta",  18, 82, 0, Elemento.CONHECIMENTO, "A verdade sobre o paranormal é demais para qualquer mente."),
            TipoHabilidade.FORTE, Elemento.CONHECIMENTO, 2.0));
    }

    // ── Consultas ────────────────────────────────────────────────────

    public static List<EntradaRitual> getTodos() {
        return java.util.Collections.unmodifiableList(CATALOGO);
    }

    public static List<EntradaRitual> getPorElemento(Elemento e) {
        return CATALOGO.stream()
            .filter(r -> r.elemento() == e)
            .collect(Collectors.toList());
    }

    public static EntradaRitual getRitualFraco(Elemento e) {
        return CATALOGO.stream()
            .filter(r -> r.elemento() == e && r.grau() == TipoHabilidade.FRACA)
            .findFirst().orElseThrow();
    }

    public static EntradaRitual getRitualMedio(Elemento e) {
        return CATALOGO.stream()
            .filter(r -> r.elemento() == e && r.grau() == TipoHabilidade.MEDIA)
            .findFirst().orElseThrow();
    }

    public static EntradaRitual getRitualForte(Elemento e) {
        return CATALOGO.stream()
            .filter(r -> r.elemento() == e && r.grau() == TipoHabilidade.FORTE)
            .findFirst().orElseThrow();
    }

    /**
     * Rituais disponíveis para desbloqueio (escolha de nível).
     * Exclui os já aprendidos e respeita a cadeia fraco→médio→forte.
     *
     * @param aprendidos nomes dos rituais que o jogador já possui
     */
    public static List<EntradaRitual> getDisponiveis(List<String> aprendidos) {
        return CATALOGO.stream()
            .filter(e -> !aprendidos.contains(e.ritual().getNome()))
            .filter(e -> preRequisitoCumprido(e, aprendidos))
            .collect(Collectors.toList());
    }

    private static boolean preRequisitoCumprido(EntradaRitual entrada, List<String> aprendidos) {
        return switch (entrada.grau()) {
            case FRACA -> true;
            case MEDIA -> aprendidos.contains(getRitualFraco(entrada.elemento()).ritual().getNome());
            case FORTE -> aprendidos.contains(getRitualMedio(entrada.elemento()).ritual().getNome());
        };
    }

    /**
     * Calcula o dano real de um ritual com base no Poder Paranormal do jogador.
     * Fórmula: (base + PP * multiplicador) * bonusAfinidade * multiplicadorElemental
     */
    public static int calcularDano(EntradaRitual entrada, int poderParanormal,
                                    double bonusAfinidade, double multElemental) {
        double dano = entrada.ritual().getDano()
                    + poderParanormal * entrada.multiplicadorPP();
        return (int)(dano * bonusAfinidade * multElemental);
    }
}
