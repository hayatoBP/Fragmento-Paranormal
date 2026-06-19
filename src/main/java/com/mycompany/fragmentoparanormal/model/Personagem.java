package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.service.CatalogoHabilidadesService;
import com.mycompany.fragmentoparanormal.service.CatalogoRituaisService;
import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.Genero;
import com.mycompany.fragmentoparanormal.util.TipoArvore;
import com.mycompany.fragmentoparanormal.util.TipoHabilidade;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Personagem {

    private String nome;
    private ClassePersonagem classe;
    private Genero genero;
    private Elemento elemento;

    private int nivel;
    private int xpAtual;

    private int vida;
    private int vidaMaxima;
    private int forca;
    private int investigacao;
    private int poderParanormal;
    private int pontosEsforco;
    private int peMaximo;
    private int pontosAtributo;
    private int dinheiro = 0;

    private String imagemBase;
    private Inventario inventario;
    private Arma armaEquipada;
    private Ritual ritualEquipado;
    private ArrayList<Ritual>    rituais;
    private ArrayList<Arma>      armas;
    private ArrayList<Artefato>  artefatosEquipados;
    private ArrayList<Habilidade> habilidades;   // Combatente / Especialista

    // ── Amaldiçoar Arma (Combatente) ─────────────────────────────────
    private boolean armaAmaldicoada = false;

    // ── Bônus de campo ativos (Especialista — duram 1 batalha) ───────
    private double bonusPreparacao  = 1.0;   // multiplicador de dano
    private double reducaoPreparacao = 0.0;  // redução de dano recebido

    // ── Bônus de investigação ativo (Especialista) ────────────────────
    private int nivelBonusInvestigacao = 0;  // 0=nenhum,1=Buscar,2=Reconstru,3=Perfil
    private boolean bonusInvestigacaoUsado = false;

    // ── Flag: pendente escolha de habilidade ao upar ─────────────────
    private boolean escolhaPendente = false;

    /**
     * Níveis em que aparece a tela de escolha de habilidades/rituais.
     * Espaçados de 5 em 5 para que o jogador demore mais para desbloquear habilidades.
     */
    public static final Set<Integer> NIVEIS_ESCOLHA =
        Set.of(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);

    // ── Construtor ────────────────────────────────────────────────────
    public Personagem(String nome, ClassePersonagem classe, Genero genero, Elemento elemento) {
        this.nome      = nome;
        this.classe    = classe;
        this.genero    = genero;
        this.elemento  = elemento;
        nivel          = 1;
        xpAtual        = 0;
        pontosAtributo = 0;
        inventario     = new Inventario();
        rituais        = new ArrayList<>();
        armas          = new ArrayList<>();
        artefatosEquipados = new ArrayList<>(); // máx 2
        habilidades    = new ArrayList<>();
        configurarClasse();
        definirImagemBase();
    }

    // ── Configuração inicial por classe ──────────────────────────────
    private void configurarClasse() {
        switch (classe) {
            case COMBATENTE:
                vidaMaxima = 120; vida = 120;
                forca = 25; investigacao = 10;
                poderParanormal = 5;
                peMaximo = 20; pontosEsforco = 20;
                armaEquipada = (genero == Genero.HOMEM)
                        ? new Arma("Foice", 18) : new Arma("Faca", 12);
                desbloquearHabilidadeInicial();
                break;
            case ESPECIALISTA:
                vidaMaxima = 100; vida = 100;
                forca = 15; investigacao = 25;
                poderParanormal = 10;
                peMaximo = 30; pontosEsforco = 30;
                armaEquipada = new Arma("Pistola 9mm", 14);
                desbloquearHabilidadeInicial();
                break;
            case OCULTISTA:
                vidaMaxima = 80; vida = 80;
                forca = 8; investigacao = 15;
                poderParanormal = 30;
                peMaximo = 50; pontosEsforco = 50;
                armaEquipada = new Arma("Livro Paranormal", 0);
                desbloquearRitualInicial();
                break;
        }
    }

    // ── Desbloqueio inicial (nível 1) ─────────────────────────────────

    /** Ocultista: recebe o ritual FRACO do seu elemento automaticamente. */
    private void desbloquearRitualInicial() {
        CatalogoRituaisService.EntradaRitual entrada =
            CatalogoRituaisService.getRitualFraco(elemento);
        rituais.add(entrada.ritual());
        ritualEquipado = rituais.get(0);
    }

    /**
     * Combatente / Especialista: recebe a habilidade FRACA da árvore elemental
     * do seu elemento automaticamente no nível 1.
     */
    private void desbloquearHabilidadeInicial() {
        List<Habilidade> elementais =
            CatalogoHabilidadesService.getElemental(classe, elemento);
        elementais.stream()
            .filter(h -> h.getTipoHabilidade() == TipoHabilidade.FRACA)
            .findFirst()
            .ifPresent(habilidades::add);
    }

    // ── Resetar para missão ───────────────────────────────────────────
    public void resetarParaMissao() {
        vida = vidaMaxima;
        pontosEsforco = peMaximo;
        armaAmaldicoada = false;
        bonusPreparacao  = 1.0;
        reducaoPreparacao = 0.0;
        bonusInvestigacaoUsado = false;
    }

    /** Chamado ao fim de cada batalha — limpa buffs temporários. */
    public void encerrarBatalha() {
        armaAmaldicoada  = false;
        bonusPreparacao  = 1.0;
        reducaoPreparacao = 0.0;
    }

    public int getVidaAtual() {
        return vida;
    }

    public void setVidaAtual(int vida) {
        this.vida = vida;
    }

    public int getPeAtual() {
        return pontosEsforco;
    }

    public void setPeAtual(int pontosEsforco) {
        this.pontosEsforco = pontosEsforco;
    }

    // ── Sistema de XP ─────────────────────────────────────────────────
    /**
     * Curva de XP revisada para balanceamento:
     * - Níveis iniciais mais fáceis (inimigos dão mais XP agora).
     * - Escala mais suave nos níveis médios.
     * - Cresce mais rápido nos níveis altos para evitar que o jogador fique
     *   excessivamente forte no final do jogo.
     */
    public int xpParaProximoNivel() {
        return switch (nivel) {
            case 1  -> 80;
            case 2  -> 150;
            case 3  -> 240;
            case 4  -> 350;
            case 5  -> 480;
            case 6  -> 640;
            case 7  -> 820;
            case 8  -> 1020;
            case 9  -> 1260;
            case 10 -> 1540;
            default -> 1540 + (nivel - 10) * 450; // Escala mais agressiva nos níveis altos
        };
    }

    public void ganharXp(int xp) {
        xpAtual += xp;
        while (xpAtual >= xpParaProximoNivel()) {
            xpAtual -= xpParaProximoNivel();
            subirNivel();
        }
    }

    private void subirNivel() {
        nivel++;
        // Ganha 1 ponto de atributo a cada nível (reduzido de 2 para 1 para balanceamento)
        pontosAtributo += 1;

        // Bônus fixos de vida e PE por nível conforme especificações
        vidaMaxima += 15;
        peMaximo += 10;

        // Desbloqueios automáticos permanecem (Amaldiçoar Arma no 5, Rituais no 7/18)
        desbloquearAutomatico();

        // Marca escolha pendente apenas nos níveis definidos em NIVEIS_ESCOLHA
        // (a cada 5 níveis) para que o jogador demore mais para desbloquear habilidades
        if (NIVEIS_ESCOLHA.contains(nivel)) {
            escolhaPendente = true;
        }
    }

    /** Desbloqueios automáticos por nível (sem escolha do jogador). */
    private void desbloquearAutomatico() {
        switch (classe) {
            case OCULTISTA   -> desbloquearRitualAutomatico();
            case COMBATENTE  -> desbloquearHabilidadeAutomaticaCombatente();
            case ESPECIALISTA-> desbloquearHabilidadeAutomaticaEspecialista();
        }
    }

    private void desbloquearRitualAutomatico() {
        // Nível 10 → ritual MÉDIO do elemento próprio (atrasado para balanceamento)
        if (nivel == 10) {
            Ritual r = CatalogoRituaisService.getRitualMedio(elemento).ritual();
            if (rituais.stream().noneMatch(x -> x.getNome().equals(r.getNome())))
                rituais.add(r);
        }
        // Nível 22 → ritual FORTE do elemento próprio (atrasado para balanceamento)
        if (nivel == 22) {
            Ritual r = CatalogoRituaisService.getRitualForte(elemento).ritual();
            if (rituais.stream().noneMatch(x -> x.getNome().equals(r.getNome())))
                rituais.add(r);
        }
    }

    private void desbloquearHabilidadeAutomaticaCombatente() {
        // Nível 8 → Amaldiçoar Arma (atrasado para balanceamento)
        if (nivel == 8) {
            Habilidade h = CatalogoHabilidadesService.buscarPorNome("Amaldiçoar Arma");
            if (h != null && habilidades.stream().noneMatch(x -> x.getNome().equals(h.getNome())))
                habilidades.add(h);
        }
        // Nível 10 → habilidade MÉDIA da árvore elemental do elemento próprio
        if (nivel == 10) desbloquearMediaElemental();
        // Nível 22 → habilidade FORTE da árvore elemental do elemento próprio
        if (nivel == 22) desbloquearForteElemental();
    }

    private void desbloquearHabilidadeAutomaticaEspecialista() {
        // Nível 10 → habilidade MÉDIA da árvore elemental do elemento próprio
        if (nivel == 10) desbloquearMediaElemental();
        // Nível 22 → habilidade FORTE da árvore elemental do elemento próprio
        if (nivel == 22) desbloquearForteElemental();
    }

    private void desbloquearMediaElemental() {
        CatalogoHabilidadesService.getElemental(classe, elemento).stream()
            .filter(h -> h.getTipoHabilidade() == TipoHabilidade.MEDIA)
            .findFirst()
            .ifPresent(h -> {
                if (habilidades.stream().noneMatch(x -> x.getNome().equals(h.getNome())))
                    habilidades.add(h);
            });
    }

    private void desbloquearForteElemental() {
        CatalogoHabilidadesService.getElemental(classe, elemento).stream()
            .filter(h -> h.getTipoHabilidade() == TipoHabilidade.FORTE)
            .findFirst()
            .ifPresent(h -> {
                if (habilidades.stream().noneMatch(x -> x.getNome().equals(h.getNome())))
                    habilidades.add(h);
            });
    }

    // ── Aprender habilidade / ritual (escolha do jogador) ────────────
    public void aprenderHabilidade(Habilidade h) {
        if (habilidades.stream().noneMatch(x -> x.getNome().equals(h.getNome())))
            habilidades.add(h);
        escolhaPendente = false;
    }

    public void aprenderRitual(Ritual ritual) {
        if (rituais.stream().noneMatch(x -> x.getNome().equals(ritual.getNome())))
            rituais.add(ritual);
        escolhaPendente = false;
    }

    // ── Afinidade elemental ───────────────────────────────────────────
    /** Bônus de dano quando usa habilidade/ritual do elemento próprio (+20%). */
    public double getBonusAfinidade(Elemento ritualElem) {
        if (ritualElem == elemento) {
            return 1.20;
        }
        return 1.0;
    }

    /** Redução de custo de PE quando usa habilidade/ritual do elemento próprio (-2 PE). */
    public int getCustoPEComAfinidade(int custoBase, Elemento ritualElem) {
        if (ritualElem == elemento) {
            return Math.max(1, custoBase - 2);
        }
        return custoBase;
    }

    // ── Amaldiçoar Arma (Combatente) ─────────────────────────────────
    public boolean podeAmaldicoarArma() {
        return classe == ClassePersonagem.COMBATENTE
            && habilidades.stream().anyMatch(h -> h.getNome().equals("Amaldiçoar Arma"))
            && !armaAmaldicoada;
    }

    public boolean amaldicoarArma() {
        int custo = getCustoPEComAfinidade(4, elemento);
        if (pontosEsforco < custo) return false;
        pontosEsforco -= custo;
        armaAmaldicoada = true;
        return true;
    }

    public boolean isArmaAmaldicoada() { return armaAmaldicoada; }

    public String getDescricaoAmaldicao() {
        if (!armaAmaldicoada) return "";
        return switch (elemento) {
            case SANGUE      -> "🩸 Amaldiçoada por Sangue";
            case MORTE       -> "⚰️ Amaldiçoada por Morte";
            case ENERGIA     -> "⚡ Amaldiçoada por Energia";
            case CONHECIMENTO-> "📜 Amaldiçoada por Conhecimento";
            default          -> "✦ Amaldiçoada";
        };
    }

    // ── Bônus de campo — Preparação (Especialista) ───────────────────
    public void aplicarBonusPreparacao(double multDano, double redDano) {
        this.bonusPreparacao   = multDano;
        this.reducaoPreparacao = redDano;
        pontosEsforco -= 0; // custo já descontado pelo controller
    }

    public double getBonusPreparacao()   { return bonusPreparacao; }
    public double getReducaoPreparacao() { return reducaoPreparacao; }

    // ── Bônus de investigação — Investigação (Especialista) ───────────
    public void ativarBonusInvestigacao(int nivel) {
        this.nivelBonusInvestigacao = nivel;
        this.bonusInvestigacaoUsado = false;
    }

    public int getNivelBonusInvestigacao()   { return nivelBonusInvestigacao; }
    public boolean isBonusInvestigacaoUsado(){ return bonusInvestigacaoUsado; }
    public void marcarBonusInvestigacaoUsado() {
        if (nivelBonusInvestigacao < 3) { // Perfil Completo (nível 3) dura a missão inteira
            bonusInvestigacaoUsado = true;
            nivelBonusInvestigacao = 0;
        }
    }

    // ── Cálculos de dano ─────────────────────────────────────────────
    public int calcularDanoFisico() {
        int dano = forca;
        if (armaEquipada != null) dano += armaEquipada.getBonusDano();
        if (classe == ClassePersonagem.COMBATENTE) dano += 10;
        return (int)(dano * bonusPreparacao);
    }

    public int custoAtaqueEspecial() {
        return switch (classe) {
            case COMBATENTE   -> 8;
            case ESPECIALISTA -> 10;
            case OCULTISTA    -> 12;
        };
    }

    public int calcularDanoEspecial() {
        return (int)(calcularDanoFisico() * 1.75);
    }

    public int calcularDanoRitual() {
        int dano = poderParanormal;
        // Ocultista ganha bônus base maior em rituais (+25 fixo)
        if (classe == ClassePersonagem.OCULTISTA) dano += 25;
        return dano;
    }



    /** Elemento efetivo do ataque físico (considera Amaldiçoar Arma). */
    public Elemento getElementoAtaqueAtual() {
        return armaAmaldicoada ? elemento : null; // null = sem elemento
    }

    // ── Helpers ──────────────────────────────────────────────────────
    public List<String> getNomesHabilidades() {
        return habilidades.stream().map(Habilidade::getNome).toList();
    }

    public List<String> getNomesRituais() {
        return rituais.stream().map(Ritual::getNome).toList();
    }

    public boolean estaVivo()     { return vida > 0; }
    public boolean podeUsarCura() { return classe == ClassePersonagem.OCULTISTA; }

    public void adicionarArma(Arma arma) { armas.add(arma); }

    public ArrayList<Artefato> getArtefatosEquipados() { return artefatosEquipados; }
    /** Equipa um artefato (máx 2). Aplica os bônus passivos ao personagem. */
    public boolean equiparArtefato(Artefato artefato) {
        if (artefatosEquipados.size() >= 2) return false;
        // Evita duplicata pelo nome
        if (artefatosEquipados.stream().anyMatch(a -> a.getNome().equals(artefato.getNome()))) return false;
        artefatosEquipados.add(artefato);
        aplicarBonusArtefato(artefato, +1);
        return true;
    }

    /** Remove um artefato equipado e reverte os bônus. */
    public boolean desequiparArtefato(Artefato artefato) {
        boolean removido = artefatosEquipados.removeIf(a -> a.getNome().equals(artefato.getNome()));
        if (removido) aplicarBonusArtefato(artefato, -1);
        return removido;
    }

    private void aplicarBonusArtefato(Artefato a, int sinal) {
        forca            += sinal * a.getBonusForca();
        investigacao     += sinal * a.getBonusInvestigacao();
        poderParanormal  += sinal * a.getBonusPoderParanormal();
        vidaMaxima       += sinal * a.getBonusVidaMaxima();
        peMaximo         += sinal * a.getBonusPeMaximo();
        // Garante que vida/PE não excedam o máximo após ajuste
        vida             = Math.min(vida, vidaMaxima);
        pontosEsforco    = Math.min(pontosEsforco, peMaximo);
    }

    // ── Getters / setters ─────────────────────────────────────────────
    public String getNome()                          { return nome; }
    public ClassePersonagem getClasse()              { return classe; }
    public Genero getGenero()                        { return genero; }
    public Elemento getElemento()                    { return elemento; }
    private int id; // Adicionar campo id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
    public int getXpAtual() { return xpAtual; }
    public void setXpAtual(int xp) { this.xpAtual = xp; }
    public int getXpParaProximoNivel()               { return xpParaProximoNivel(); }
    public int getVida()                             { return vida; }
    public void setVida(int vida)                    { this.vida = Math.min(vida, vidaMaxima); }
    public int getVidaMaxima()                       { return vidaMaxima; }
    public void setVidaMaxima(int v)                 { this.vidaMaxima = v; }
    public int getForca()                            { return forca; }
    public void setForca(int v)                      { this.forca = v; }
    public int getInvestigacao()                     { return investigacao; }
    public void setInvestigacao(int v)               { this.investigacao = v; }
    public int getPoderParanormal()                  { return poderParanormal; }
    public void setPoderParanormal(int v)            { this.poderParanormal = v; }
    public int getPontosEsforco()                    { return pontosEsforco; }
    public void setPontosEsforco(int v)              { this.pontosEsforco = v; }
    public int getPeMaximo()                         { return peMaximo; }
    public void setPeMaximo(int v)                   { this.peMaximo = v; }
    public int getPontosAtributo()                   { return pontosAtributo; }
    public void setPontosAtributo(int v)             { this.pontosAtributo = v; }
    public int getDinheiro()                         { return dinheiro; }
    public void setDinheiro(int v)                   { this.dinheiro = Math.max(0, v); }
    public void adicionarDinheiro(int v)             { this.dinheiro += v; }
    public boolean gastarDinheiro(int v)             {
        if (dinheiro < v) return false;
        dinheiro -= v;
        return true;
    }
    public Inventario getInventario()                { return inventario; }
    public Arma getArmaEquipada()                    { return armaEquipada; }
    public void setArmaEquipada(Arma v)              { this.armaEquipada = v; }
    public Ritual getRitualEquipado()                { return ritualEquipado; }
    public void setRitualEquipado(Ritual r)          { this.ritualEquipado = r; }
    public ArrayList<Ritual> getRituais()            { return rituais; }
    public ArrayList<Arma> getArmas()                { return armas; }
    public ArrayList<Habilidade> getHabilidades()    { return habilidades; }
    public boolean isEscolhaPendente()               { return escolhaPendente; }
    public void setEscolhaPendente(boolean v)        { this.escolhaPendente = v; }

    private String scene; // Adicionar campo scene
    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }

    public String getImagemAtual() {
        if (armaEquipada == null) return imagemBase;
        return switch (classe) {
            case ESPECIALISTA -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/arthur_arma.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/erin_arma.png";
            case COMBATENTE   -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/dominic_arma.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/carina_arma.png";
            case OCULTISTA    -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/dante_arma.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/agatha_arma.png";
        };
    }

    private void definirImagemBase() {
        imagemBase = switch (classe) {
            case ESPECIALISTA -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/arthur.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/erin.png";
            case COMBATENTE   -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/dominic.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/carina.png";
            case OCULTISTA    -> genero == Genero.HOMEM
                ? "/com/mycompany/fragmentoparanormal/images/personagens/dante.png"
                : "/com/mycompany/fragmentoparanormal/images/personagens/agatha.png";
        };
    }
}
