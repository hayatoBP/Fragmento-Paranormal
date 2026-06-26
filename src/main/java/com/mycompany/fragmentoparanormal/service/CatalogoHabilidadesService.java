package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Habilidade;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.TipoArvore;
import com.mycompany.fragmentoparanormal.util.TipoHabilidade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Catálogo centralizado de todas as habilidades do jogo.
 *
 * Combatente  → árvores: ATAQUE, DEFESA, MOBILIDADE, ELEMENTAL (+ Amaldiçoar Arma especial)
 * Especialista → árvores: PRECISAO, INVESTIGACAO (campo), PREPARACAO (campo), ELEMENTAL
 *
 * Níveis de escolha (11 intervalos até nível 60):
 *   6, 12, 18, 24, 30, 36, 42, 48, 54, 57, 60
 *
 * Ocultista usa Ritual.java — não está aqui.
 */
public class CatalogoHabilidadesService {

    private static final List<Habilidade> TODAS = new ArrayList<>();

    static {
        buildCombatente();
        buildEspecialista();
    }

    // ================================================================
    // COMBATENTE
    // ================================================================
    private static void buildCombatente() {
        final ClassePersonagem C = ClassePersonagem.COMBATENTE;

        // ── Árvore de Ataque ────────────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Ataque Brutal").custoPE(3)
            .descricao("Um golpe direto, sem cerimônias.")
            .efeito("Causa 140% do dano da arma.")
            .multiplicador(1.40)
            .arvore(TipoArvore.ATAQUE).tipo(TipoHabilidade.FRACA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Investida Devastadora").custoPE(8)
            .descricao("Você avança com força total contra o inimigo.")
            .efeito("Causa 200% do dano da arma.")
            .multiplicador(2.00)
            .arvore(TipoArvore.ATAQUE).tipo(TipoHabilidade.MEDIA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Golpe Executor").custoPE(15)
            .descricao("Um golpe definitivo, calculado para destruir.")
            .efeito("Causa 300% do dano da arma.")
            .multiplicador(3.00)
            .arvore(TipoArvore.ATAQUE).tipo(TipoHabilidade.FORTE).classe(C).build());

        // ── Árvore de Defesa ────────────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Guarda Firme").custoPE(2)
            .descricao("Você assume uma postura defensiva por um instante.")
            .efeito("Reduz em 25% o próximo dano recebido.")
            .reducaoDano(0.25)
            .arvore(TipoArvore.DEFESA).tipo(TipoHabilidade.FRACA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Postura Inabalável").custoPE(5)
            .descricao("Seu corpo se torna um escudo.")
            .efeito("Reduz em 50% o próximo dano recebido.")
            .reducaoDano(0.50)
            .arvore(TipoArvore.DEFESA).tipo(TipoHabilidade.MEDIA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Muralha Humana").custoPE(10)
            .descricao("Quase nada consegue atravessar sua defesa.")
            .efeito("Reduz em 75% o próximo dano recebido.")
            .reducaoDano(0.75)
            .arvore(TipoArvore.DEFESA).tipo(TipoHabilidade.FORTE).classe(C).build());

        // ── Árvore de Mobilidade ────────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Passo Rápido").custoPE(2)
            .descricao("Você se move mais rápido que o inimigo espera.")
            .efeito("Próximo ataque causa 120% do dano.")
            .multiplicador(1.20)
            .arvore(TipoArvore.MOBILIDADE).tipo(TipoHabilidade.FRACA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Reflexos Aprimorados").custoPE(5)
            .descricao("Dois golpes rápidos, quase simultâneos.")
            .efeito("Executa dois ataques de 80% do dano.")
            .multiplicador(0.80) // aplicado 2x no controller
            .arvore(TipoArvore.MOBILIDADE).tipo(TipoHabilidade.MEDIA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Surto de Adrenalina").custoPE(10)
            .descricao("Um surto de velocidade sobrehumana.")
            .efeito("Executa dois ataques de 120% do dano.")
            .multiplicador(1.20) // aplicado 2x no controller
            .arvore(TipoArvore.MOBILIDADE).tipo(TipoHabilidade.FORTE).classe(C).build());

        // ── Árvore Elemental — SANGUE ────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Frenesi").custoPE(2)
            .descricao("O sangue alimenta a fúria.")
            .efeito("Causa 130% do dano da arma (elemental Sangue).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.SANGUE).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Banho de Sangue").custoPE(5)
            .descricao("Você se entrega completamente ao caos.")
            .efeito("Causa 180% do dano da arma (elemental Sangue).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.SANGUE).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Carnificina").custoPE(10)
            .descricao("Puro terror canalizado em força bruta.")
            .efeito("Causa 260% do dano da arma (elemental Sangue).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.SANGUE).classe(C).build());

        // ── Árvore Elemental — MORTE ─────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Marca da Decadência").custoPE(2)
            .descricao("Cada golpe carrega o peso da morte.")
            .efeito("Causa 130% do dano da arma (elemental Morte).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.MORTE).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Decadência Marcial").custoPE(5)
            .descricao("Seus golpes drenam a vitalidade do alvo.")
            .efeito("Causa 180% do dano da arma (elemental Morte).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.MORTE).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Ceifador").custoPE(10)
            .descricao("Você se torna instrumento da morte.")
            .efeito("Causa 260% do dano da arma (elemental Morte).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.MORTE).classe(C).build());

        // ── Árvore Elemental — ENERGIA ───────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Sobrecarga").custoPE(2)
            .descricao("Seu corpo acumula energia cinética.")
            .efeito("Causa 130% do dano da arma (elemental Energia).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.ENERGIA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Descarga Cinética").custoPE(5)
            .descricao("A energia liberada num único golpe explosivo.")
            .efeito("Causa 180% do dano da arma (elemental Energia).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.ENERGIA).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Tempestade de Golpes").custoPE(10)
            .descricao("Uma sequência imparável de ataques carregados.")
            .efeito("Causa 260% do dano da arma (elemental Energia).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.ENERGIA).classe(C).build());

        // ── Árvore Elemental — CONHECIMENTO ──────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Análise de Combate").custoPE(2)
            .descricao("Você estuda os padrões do inimigo antes de atacar.")
            .efeito("Causa 130% do dano da arma (elemental Conhecimento).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.CONHECIMENTO).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Ponto Fraco").custoPE(5)
            .descricao("Você identifica e explora a vulnerabilidade do alvo.")
            .efeito("Causa 180% do dano da arma (elemental Conhecimento).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.CONHECIMENTO).classe(C).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Execução Precisa").custoPE(10)
            .descricao("Golpe calculado para máxima destruição.")
            .efeito("Causa 260% do dano da arma (elemental Conhecimento).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.CONHECIMENTO).classe(C).build());

        // ── Amaldiçoar Arma (habilidade especial automática nível 5) ─
        TODAS.add(new Habilidade.Builder()
            .nome("Amaldiçoar Arma").custoPE(4)
            .descricao("Canaliza o elemento do portador na arma equipada.")
            .efeito("Arma passa a usar o elemento do Combatente até o fim da batalha.")
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA) // tipo especial — tratado separado
            .classe(C).build());
    }

    // ================================================================
    // ESPECIALISTA
    // ================================================================
    private static void buildEspecialista() {
        final ClassePersonagem E = ClassePersonagem.ESPECIALISTA;

        // ── Árvore de Precisão (combate) ────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Tiro Preciso").custoPE(4)
            .descricao("Um disparo cuidadosamente mira no ponto certo.")
            .efeito("Causa 150% do dano da arma.")
            .multiplicador(1.50)
            .arvore(TipoArvore.PRECISAO).tipo(TipoHabilidade.FRACA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Ponto Vital").custoPE(10)
            .descricao("Você identifica e atinge um ponto vital do inimigo.")
            .efeito("Causa 220% do dano da arma.")
            .multiplicador(2.20)
            .arvore(TipoArvore.PRECISAO).tipo(TipoHabilidade.MEDIA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Disparo Perfeito").custoPE(18)
            .descricao("Um único tiro impossível de desviar.")
            .efeito("Causa 320% do dano da arma.")
            .multiplicador(3.20)
            .arvore(TipoArvore.PRECISAO).tipo(TipoHabilidade.FORTE).classe(E).build());

        // ── Árvore de Investigação (campo) ───────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Buscar Evidências").custoPE(3)
            .descricao("Você varre a área em busca de pistas escondidas.")
            .efeito("Aumenta a chance de encontrar páginas e pistas na próxima investigação.")
            .campo()
            .arvore(TipoArvore.INVESTIGACAO).tipo(TipoHabilidade.FRACA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Reconstrução da Cena").custoPE(6)
            .descricao("Você reconstrói mentalmente os eventos do local.")
            .efeito("Grande aumento nas chances de encontrar algo útil na próxima investigação.")
            .campo()
            .arvore(TipoArvore.INVESTIGACAO).tipo(TipoHabilidade.MEDIA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Perfil Completo").custoPE(10)
            .descricao("Análise total do ambiente e seus segredos.")
            .efeito("Durante toda a missão, melhora significativamente o desempenho em investigações.")
            .campo()
            .arvore(TipoArvore.INVESTIGACAO).tipo(TipoHabilidade.FORTE).classe(E).build());

        // ── Árvore de Preparação (campo) ─────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Estudar Alvo").custoPE(3)
            .descricao("Você analisa o inimigo antes do confronto.")
            .efeito("+10% de dano na próxima batalha.")
            .campo()
            .multiplicador(1.10)
            .arvore(TipoArvore.PREPARACAO).tipo(TipoHabilidade.FRACA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Plano de Ação").custoPE(6)
            .descricao("Você elabora uma estratégia de combate detalhada.")
            .efeito("+20% de dano na próxima batalha.")
            .campo()
            .multiplicador(1.20)
            .arvore(TipoArvore.PREPARACAO).tipo(TipoHabilidade.MEDIA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Operação Planejada").custoPE(10)
            .descricao("Cada movimento calculado com precisão militar.")
            .efeito("+30% de dano e -10% de dano recebido na próxima batalha.")
            .campo()
            .multiplicador(1.30).reducaoDano(0.10)
            .arvore(TipoArvore.PREPARACAO).tipo(TipoHabilidade.FORTE).classe(E).build());

        // ── Árvore Elemental — SANGUE ────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Instinto Predatório").custoPE(2)
            .descricao("Seu instinto aguçado guia cada golpe.")
            .efeito("Causa 130% do dano da arma (elemental Sangue).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.SANGUE).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Caçador Implacável").custoPE(5)
            .descricao("Você persegue o alvo até o fim.")
            .efeito("Causa 180% do dano da arma (elemental Sangue).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.SANGUE).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Predador Supremo").custoPE(10)
            .descricao("Ninguém escapa de você.")
            .efeito("Causa 260% do dano da arma (elemental Sangue).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.SANGUE).classe(E).build());

        // ── Árvore Elemental — MORTE ─────────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Estudo da Decadência").custoPE(2)
            .descricao("Você entende a morte melhor que qualquer um.")
            .efeito("Causa 130% do dano da arma (elemental Morte).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.MORTE).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Conhecimento Proibido").custoPE(5)
            .descricao("Você usa o que não deveria saber.")
            .efeito("Causa 180% do dano da arma (elemental Morte).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.MORTE).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Herdeiro do Fim").custoPE(10)
            .descricao("A morte é apenas mais uma ferramenta em suas mãos.")
            .efeito("Causa 260% do dano da arma (elemental Morte).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.MORTE).classe(E).build());

        // ── Árvore Elemental — ENERGIA ───────────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Leitura de Padrões").custoPE(2)
            .descricao("Você decifra os padrões energéticos do ambiente.")
            .efeito("Causa 130% do dano da arma (elemental Energia).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.ENERGIA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Sincronização Energética").custoPE(5)
            .descricao("Você se sincroniza com a energia do alvo para atingi-lo.")
            .efeito("Causa 180% do dano da arma (elemental Energia).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.ENERGIA).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Mestre do Caos").custoPE(10)
            .descricao("Você domina o caos energético que outros temem.")
            .efeito("Causa 260% do dano da arma (elemental Energia).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.ENERGIA).classe(E).build());

        // ── Árvore Elemental — CONHECIMENTO ──────────────────────────
        TODAS.add(new Habilidade.Builder()
            .nome("Dedução Avançada").custoPE(2)
            .descricao("Você usa o conhecimento como arma.")
            .efeito("Causa 130% do dano da arma (elemental Conhecimento).")
            .multiplicador(1.30)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FRACA)
            .elemento(Elemento.CONHECIMENTO).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Análise Profunda").custoPE(5)
            .descricao("Uma análise devastadora transforma informação em dano.")
            .efeito("Causa 180% do dano da arma (elemental Conhecimento).")
            .multiplicador(1.80)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.MEDIA)
            .elemento(Elemento.CONHECIMENTO).classe(E).build());

        TODAS.add(new Habilidade.Builder()
            .nome("Verdade Absoluta").custoPE(10)
            .descricao("A verdade como força devastadora.")
            .efeito("Causa 260% do dano da arma (elemental Conhecimento).")
            .multiplicador(2.60)
            .arvore(TipoArvore.ELEMENTAL).tipo(TipoHabilidade.FORTE)
            .elemento(Elemento.CONHECIMENTO).classe(E).build());
    }

    // ================================================================
    // CONSULTAS
    // ================================================================

    /** Todas as habilidades de uma classe. */
    public static List<Habilidade> getHabilidades(ClassePersonagem classe) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == classe)
            .collect(Collectors.toList());
    }

    /** Habilidades de uma classe em uma árvore específica. */
    public static List<Habilidade> getArvore(ClassePersonagem classe, TipoArvore arvore) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == classe && h.getArvore() == arvore)
            .collect(Collectors.toList());
    }

    /** Habilidades elementais de uma classe para um elemento específico. */
    public static List<Habilidade> getElemental(ClassePersonagem classe, Elemento elemento) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == classe
                      && h.getArvore() == TipoArvore.ELEMENTAL
                      && h.getElementoArvore() == elemento)
            .collect(Collectors.toList());
    }

    /**
     * Retorna habilidades que o jogador pode desbloquear agora,
     * respeitando a cadeia fraca → média → forte.
     *
     * @param classe       classe do jogador
     * @param aprendidas   nomes das habilidades que o jogador já possui
     */
    public static List<Habilidade> getDisponiveis(ClassePersonagem classe,
                                                   List<String> aprendidas) {
        return getDisponiveis(classe, aprendidas, null);
    }

    /**
     * Retorna habilidades disponíveis filtrando habilidades elementais
     * pelo elemento do jogador (Combatente e Especialista).
     */
    public static List<Habilidade> getDisponiveis(ClassePersonagem classe,
                                                   List<String> aprendidas,
                                                   Elemento elementoJogador) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == classe)
            .filter(h -> !aprendidas.contains(h.getNome()))
            .filter(h -> preRequisitoCumprido(h, aprendidas))
            .filter(h -> {
                // Para Combatente e Especialista: habilidades elementais só do próprio elemento
                if (elementoJogador != null
                        && h.getArvore() == TipoArvore.ELEMENTAL
                        && h.getElementoArvore() != null
                        && h.getElementoArvore() != elementoJogador) {
                    return false;
                }
                return true;
            })
            .collect(Collectors.toList());
    }

    /** Verifica se o pré-requisito de uma habilidade está cumprido. */
    private static boolean preRequisitoCumprido(Habilidade h, List<String> aprendidas) {
        if (h.getTipoHabilidade() == null) return true; // habilidade especial (Amaldiçoar)
        
        // Habilidades iniciais (FRACAS) não têm pré-requisito
        if (h.getTipoHabilidade() == com.mycompany.fragmentoparanormal.util.TipoHabilidade.FRACA) return true;
        
        // Para árvores normais, o elemento é null. Para a árvore ELEMENTAL, o elemento deve coincidir.
        Elemento elementoReq = (h.getArvore() == TipoArvore.ELEMENTAL) ? h.getElementoArvore() : null;
        
        return switch (h.getTipoHabilidade()) {
            case MEDIA -> aprendidas.stream().anyMatch(nome ->
                encontrarFraca(h.getClasseDona(), h.getArvore(), elementoReq)
                    .stream().anyMatch(f -> f.getNome().equals(nome)));
            case FORTE -> aprendidas.stream().anyMatch(nome ->
                encontrarMedia(h.getClasseDona(), h.getArvore(), elementoReq)
                    .stream().anyMatch(m -> m.getNome().equals(nome)));
            default -> true;
        };
    }

    private static List<Habilidade> encontrarFraca(ClassePersonagem c, TipoArvore a, Elemento e) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == c && h.getArvore() == a
                      && h.getTipoHabilidade() == TipoHabilidade.FRACA
                      && (e == null || e == h.getElementoArvore()))
            .collect(Collectors.toList());
    }

    private static List<Habilidade> encontrarMedia(ClassePersonagem c, TipoArvore a, Elemento e) {
        return TODAS.stream()
            .filter(h -> h.getClasseDona() == c && h.getArvore() == a
                      && h.getTipoHabilidade() == TipoHabilidade.MEDIA
                      && (e == null || e == h.getElementoArvore()))
            .collect(Collectors.toList());
    }

    /** Busca uma habilidade pelo nome exato. */
    public static Habilidade buscarPorNome(String nome) {
        return TODAS.stream()
            .filter(h -> h.getNome().equals(nome))
            .findFirst().orElse(null);
    }
}
