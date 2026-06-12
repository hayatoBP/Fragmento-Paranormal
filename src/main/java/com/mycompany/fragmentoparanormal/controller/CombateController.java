package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.dao.JogadorDAO;
import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.ElementoService;
import com.mycompany.fragmentoparanormal.service.RitualService;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CombateController {

    private Personagem jogador;
    private Inimigo    inimigo;
    private boolean    combateEncerrado = false;

    @FXML private Label     lblNomeInimigo;
    @FXML private Label     lblVidaJogador;
    @FXML private Label     lblPEJogador;
    @FXML private Label     lblVidaInimigo;
    @FXML private Label     lblElementoInimigo;
    @FXML private Label     lblEfetividade;
    @FXML private Label     lblEventos;
    @FXML private Label     lblDicaAtaque;
    @FXML private Label     lblAmaldicao;   // indicador "🔥 Amaldiçoada por X"
    @FXML private ImageView imgJogador;
    @FXML private ImageView imgInimigo;

    // Painel principal (Atacar / Inventário / Fugir)
    @FXML private HBox   painelPrincipal;
    @FXML private Button btnAtacar;
    @FXML private Button btnFugir;
    @FXML private Button btnVoltarMissao;

    // Submenu de ataque
    @FXML private HBox   painelMenuAtaque;
    @FXML private Button btnAtaqueArma;
    @FXML private Button btnAtaqueEspecial;
    @FXML private Button btnRitual;
    @FXML private Button btnAmaldicoar;   // Amaldiçoar Arma (Combatente nível 5+)

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        inimigo = GameContext.inimigoAtual;
        btnVoltarMissao.setVisible(false);
        lblEfetividade.setText("");
        lblDicaAtaque.setText("");
        carregarImagens();

        if (GameContext.vidaInimigoSalva >= 0 && inimigo != null) {
            inimigo.setVida(GameContext.vidaInimigoSalva);
            GameContext.vidaInimigoSalva = -1;
        }

        atualizarTela();

        if (inimigo != null && inimigo.isBoss()) {
            lblNomeInimigo.setText("⚠ BOSS FINAL: " + inimigo.getNome());
            lblEventos.setText("O Quarto Anfitrião emerge das sombras...\n\"Bem-vindo ao ritual, agente.\"");
        }
    }

    private void carregarImagens() {
        try {
            if (jogador != null) {
                var s = getClass().getResourceAsStream(jogador.getImagemAtual());
                if (s != null) imgJogador.setImage(new Image(s));
            }
            if (inimigo != null) {
                var s = getClass().getResourceAsStream(inimigo.getImagem());
                if (s != null) imgInimigo.setImage(new Image(s));
            }
        } catch (Exception e) {
            System.err.println("Erro imagens: " + e.getMessage());
        }
    }

    // ── MENU DE ATAQUE ───────────────────────────────────────────────

    /** Botão "Atacar" → abre submenu com as 3 opções. */
    @FXML
    private void mostrarMenuAtaque() {
        if (combateEncerrado) return;
        painelMenuAtaque.setVisible(true);
        painelMenuAtaque.setManaged(true);

        // Dica contextual
        int peEspecial = jogador.custoAtaqueEspecial();
        int danoEspecial = jogador.calcularDanoEspecial();
        int danoArma = jogador.calcularDanoFisico();
        boolean temPeEspecial = jogador.getPontosEsforco() >= peEspecial;
        boolean temRitual = jogador.getRitualEquipado() != null
                && jogador.getPontosEsforco() >= jogador.getRitualEquipado().getCustoPE();

        btnAtaqueEspecial.setDisable(!temPeEspecial);
        btnRitual.setDisable(!temRitual);

        String dicaAmaldicao = jogador.isArmaAmaldicoada()
            ? "  |  🔥 Arma: " + jogador.getDescricaoAmaldicao()
            : (jogador.podeAmaldicoarArma() ? "  |  🔥 Pode Amaldiçoar (4 PE)" : "");

        lblDicaAtaque.setText(
            "🗡 Arma: ~" + danoArma + " dano" +
            (jogador.isArmaAmaldicoada() ? " [" + jogador.getElemento() + "]" : " [sem elemento]") +
            "  |  💥 Especial: ~" + danoEspecial + " dano  (custo " + peEspecial + " PE)" +
            (temPeEspecial ? "" : "  ✕ sem PE") + "  |  " +
            "☽ Ritual: " + (jogador.getRitualEquipado() != null
                ? jogador.getRitualEquipado().getNome() + " (" + jogador.getRitualEquipado().getCustoPE() + " PE)"
                : "nenhum equipado") +
            dicaAmaldicao
        );
    }

    @FXML
    private void fecharMenuAtaque() {
        painelMenuAtaque.setVisible(false);
        painelMenuAtaque.setManaged(false);
        lblDicaAtaque.setText("");
    }

    /** Amaldiçoar Arma — disponível apenas para Combatente a partir do nível 5. */
    @FXML
    private void amaldicoarArma() {
        if (combateEncerrado || jogador == null) return;
        fecharMenuAtaque();

        if (!jogador.podeAmaldicoarArma()) {
            lblEventos.setText("✕ Amaldiçoar Arma não disponível.");
            return;
        }

        boolean sucesso = jogador.amaldicoarArma();
        if (!sucesso) {
            int custo = jogador.getCustoPEComAfinidade(4, jogador.getElemento());
            lblEventos.setText("✕ PE insuficiente para Amaldiçoar Arma! (precisa " + custo + " PE)");
            return;
        }

        lblEventos.setText("🔥 Arma amaldiçoada por " + jogador.getElemento() + "!
"
            + "Ataques normais agora usam o elemento " + jogador.getElemento() + " até o fim da batalha.");
        atualizarTela();
    }

    // ── AÇÕES DE COMBATE ─────────────────────────────────────────────

    /** Ataque físico normal com arma.
     *  Sem bônus elemental por padrão.
     *  Com Amaldiçoar Arma ativa: usa o elemento do jogador. */
    @FXML
    private void atacarComArma(ActionEvent event) {
        if (combateEncerrado || jogador == null || inimigo == null) return;
        fecharMenuAtaque();

        int danoBase = jogador.calcularDanoFisico();

        // Bônus elemental só se a arma estiver amaldiçoada
        double mult;
        String prefixo;
        if (jogador.isArmaAmaldicoada()) {
            mult    = ElementoService.calcularMultiplicador(jogador.getElemento(), inimigo.getElemento());
            prefixo = "⚔ Ataque amaldiçoado [" + jogador.getElemento() + "]: ";
        } else {
            mult    = 1.0;  // sem bônus elemental
            prefixo = "⚔ Ataque com arma: ";
        }

        int danoFinal = (int)(danoBase * mult);
        inimigo.setVida(inimigo.getVida() - danoFinal);

        String ef = descreveEfetividade(mult);
        lblEfetividade.setText(ef);

        if (verificarMorteInimigo(event, "Você atacou com a arma! +" + inimigo.getXpConcedido() + " XP.")) return;

        int danoInimigo = aplicarDanoInimigo();
        lblEventos.setText(prefixo + danoFinal + " de dano" + ef
                + ".\nO inimigo contra-atacou por " + danoInimigo + ".");

        if (!jogador.estaVivo()) { encerrarDerrota(); return; }
        atualizarTela();
    }

    /**
     * Ataque especial: usa a arma equipada × multiplicador da habilidade, consome PE.
     * Sem bônus elemental — o dano vem da arma, não do elemento.
     * Habilidades elementais (árvore ELEMENTAL) recebem bônus da afinidade do jogador.
     */
    @FXML
    private void atacarEspecial(ActionEvent event) {
        if (combateEncerrado || jogador == null || inimigo == null) return;

        int custo = jogador.custoAtaqueEspecial();
        if (jogador.getPontosEsforco() < custo) {
            lblEventos.setText("⚡ Sem PE suficiente para o ataque especial! (precisa " + custo + " PE)");
            fecharMenuAtaque();
            return;
        }
        fecharMenuAtaque();

        jogador.setPontosEsforco(jogador.getPontosEsforco() - custo);

        int danoBase  = jogador.calcularDanoEspecial();
        // Ataque especial: sem bônus elemental por padrão
        int danoFinal = danoBase;
        String ef = "";
        lblEfetividade.setText(ef);

        inimigo.setVida(inimigo.getVida() - danoFinal);

        if (verificarMorteInimigo(event, "Ataque especial devastador! +" + inimigo.getXpConcedido() + " XP.")) return;

        int danoInimigo = aplicarDanoInimigo();
        lblEventos.setText("💥 Ataque especial: " + danoFinal + " de dano (custou " + custo + " PE)."
                + "\nO inimigo contra-atacou por " + danoInimigo + ".");

        if (!jogador.estaVivo()) { encerrarDerrota(); return; }
        atualizarTela();
    }

    /** Ritual elemental. */
    @FXML
    private void usarRitual(ActionEvent event) {
        if (combateEncerrado || jogador == null || inimigo == null) return;
        fecharMenuAtaque();

        boolean conseguiu = RitualService.usarRitual(jogador, inimigo);
        if (!conseguiu) {
            lblEventos.setText("☽ Ritual falhou! PE insuficiente ou nenhum ritual equipado.");
            lblEfetividade.setText("");
            return;
        }

        double mult = jogador.getRitualEquipado() != null
                ? ElementoService.calcularMultiplicador(jogador.getRitualEquipado().getElemento(), inimigo.getElemento())
                : 1.0;
        String ef = descreveEfetividade(mult);
        lblEfetividade.setText(ef);

        if (verificarMorteInimigo(event, "Ritual devastador! +" + inimigo.getXpConcedido() + " XP.")) return;

        int danoInimigo = aplicarDanoInimigo();
        lblEventos.setText("☽ Ritual usado" + ef + ".\nO inimigo contra-atacou por " + danoInimigo + ".");

        if (!jogador.estaVivo()) { encerrarDerrota(); return; }
        atualizarTela();
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("COMBATE");
        if (inimigo != null) GameContext.vidaInimigoSalva = inimigo.getVida();
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void fugir(ActionEvent event) {
        GameState.setVeioDeFuga(true);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    @FXML
    private void voltarMissao(ActionEvent event) {
        if (inimigo != null && inimigo.isBoss()) {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/menuMissoes.fxml");
        } else {
            GameState.setOrigemInventario("MISSAO");
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────

    /**
     * Verifica se o inimigo morreu após um ataque.
     * Se sim, concede XP, salva e encerra o combate.
     * Retorna true se o combate acabou.
     */
    private boolean verificarMorteInimigo(ActionEvent event, String mensagemVitoria) {
        if (!inimigo.estaVivo()) {
            jogador.ganharXp(inimigo.getXpConcedido());
            // Dinheiro: fraco=15, forte=30, boss=100
            int dinheiro = inimigo.isBoss() ? 100
                         : inimigo.getTipo() == com.mycompany.fragmentoparanormal.util.TipoInimigo.FORTE ? 30 : 15;
            jogador.adicionarDinheiro(dinheiro);
            jogador.encerrarBatalha();
            salvarJogador();
            if (inimigo.isBoss()) {
                CreditosController.contexto = "FIM";
                TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/creditos.fxml");
                return true;
            }
            // Se subiu de nível durante o combate, abre tela de escolha
            if (jogador.isEscolhaPendente()) {
                EscolhaHabilidadeController.telaOrigem = "COMBATE";
                TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/escolhaHabilidade.fxml");
                return true;
            }
            encerrarCombate(mensagemVitoria);
            return true;
        }
        return false;
    }

    private int aplicarDanoInimigo() {
        double multInimigo = ElementoService.calcularMultiplicador(inimigo.getElemento(), jogador.getElemento());
        int dano = (int)(inimigo.getDano() * multInimigo);
        jogador.setVida(jogador.getVida() - dano);
        return dano;
    }

    private void encerrarCombate(String mensagem) {
        combateEncerrado = true;
        lblEventos.setText(mensagem);
        atualizarTela();
        btnAtacar.setDisable(true);
        btnFugir.setDisable(true);
        btnVoltarMissao.setVisible(true);
        fecharMenuAtaque();
    }

    private void encerrarDerrota() {
        combateEncerrado = true;
        lblEventos.setText("💀 Você foi derrotado...");
        lblEfetividade.setText("");
        atualizarTela();
        btnAtacar.setDisable(true);
        btnFugir.setDisable(true);
        GameState.setVeioDeFuga(true);
        GameState.setVeioDeDerrota(true);
        GameState.setMissaoEmAndamento(false);
        TelaUtil.trocarTelaPorNode(lblEventos, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    private void salvarJogador() {
        if (jogador == null) return;
        try { JogadorDAO.salvar(jogador); }
        catch (Exception e) { System.err.println("[CombateController] Erro ao salvar: " + e.getMessage()); }
    }

    private String descreveEfetividade(double mult) {
        if (mult >= 2.0) return " ⚠ TERROR ABSOLUTO!";
        if (mult >= 1.5) return " ✦ Super efetivo!";
        if (mult <= 0.5) return " ▼ Não muito efetivo...";
        return "";
    }

    private void atualizarTela() {
        if (jogador == null || inimigo == null) return;
        lblVidaJogador.setText("Vida: " + Math.max(0, jogador.getVida()) + "/" + jogador.getVidaMaxima());
        lblPEJogador.setText("PE: " + jogador.getPontosEsforco() + "/" + jogador.getPeMaximo());
        lblVidaInimigo.setText("Vida: " + Math.max(0, inimigo.getVida()));
        lblElementoInimigo.setText("Elemento: " + inimigo.getElemento());
        if (lblNomeInimigo != null && !inimigo.isBoss())
            lblNomeInimigo.setText(inimigo.getNome());
        // Indicador de Amaldiçoar Arma
        if (lblAmaldicao != null) {
            String desc = jogador.getDescricaoAmaldicao();
            lblAmaldicao.setText(desc);
            lblAmaldicao.setVisible(!desc.isEmpty());
        }
        // Mostra botão Amaldiçoar só se disponível e arma não amaldiçoada ainda
        if (btnAmaldicoar != null) {
            btnAmaldicoar.setVisible(jogador.podeAmaldicoarArma());
            btnAmaldicoar.setManaged(jogador.podeAmaldicoarArma());
        }
    }
}
