package com.mycompany.fragmentoparanormal.controller;

import com.mycompany.fragmentoparanormal.model.Inimigo;
import com.mycompany.fragmentoparanormal.model.Personagem;
import com.mycompany.fragmentoparanormal.service.CombateService;
import com.mycompany.fragmentoparanormal.service.ElementoService;
import com.mycompany.fragmentoparanormal.service.MissaoService;
import com.mycompany.fragmentoparanormal.service.RitualService;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;
import com.mycompany.fragmentoparanormal.util.TelaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Controller da tela de combate.
 * Gerencia turnos, novo menu de ataque, amaldiçoar arma e vantagens elementais.
 */
public class CombateController {

    private Personagem jogador;
    private Inimigo    inimigo;

    @FXML private Label lblNomeInimigo, lblVidaJogador, lblPEJogador, lblVidaInimigo, lblElementoInimigo, lblAmaldicao, lblEfetividade, lblEventos, lblDicaAtaque;
    @FXML private ImageView imgJogador, imgInimigo;
    @FXML private HBox painelPrincipal, painelMenuAtaque;
    @FXML private Button btnAtacar, btnFugir, btnVoltarMissao, btnAtaqueArma, btnAtaqueEspecial, btnRitual, btnAmaldicoar;

    @FXML
    public void initialize() {
        jogador = GameContext.jogadorAtual;
        inimigo = GameContext.inimigoAtual;

        if (jogador == null || inimigo == null) {
            System.err.println("[Combate] Jogador ou Inimigo nulo!");
            return;
        }

        carregarImagens();
        atualizarInterface();
        
        // Se o inimigo já estiver morto (ao voltar do inventário por exemplo), mostra botão de continuar
        if (!inimigo.estaVivo()) {
            finalizarCombate(true);
        }
    }

    private void carregarImagens() {
        try {
            var streamJ = getClass().getResourceAsStream(jogador.getImagemAtual());
            if (streamJ != null) imgJogador.setImage(new Image(streamJ));

            var streamI = getClass().getResourceAsStream(inimigo.getImagem());
            if (streamI != null) imgInimigo.setImage(new Image(streamI));
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }
    }

    private void atualizarInterface() {
        lblNomeInimigo.setText(inimigo.getNome());
        lblVidaJogador.setText("Vida: " + jogador.getVida() + "/" + jogador.getVidaMaxima());
        lblPEJogador.setText("PE: " + jogador.getPontosEsforco() + "/" + jogador.getPeMaximo());
        lblVidaInimigo.setText("Vida: " + inimigo.getVida());
        lblElementoInimigo.setText("Elemento: " + inimigo.getElemento());

        // Amaldiçoar Arma
        if (jogador.isArmaAmaldicoada()) {
            lblAmaldicao.setText(jogador.getDescricaoAmaldicao());
            lblAmaldicao.setVisible(true);
        } else {
            lblAmaldicao.setVisible(false);
        }

        // Botão Amaldiçoar (só Combatente nível 5+)
        boolean podeAmaldicoar = jogador.podeAmaldicoarArma();
        btnAmaldicoar.setVisible(podeAmaldicoar);
        btnAmaldicoar.setManaged(podeAmaldicoar);
    }

    // ── NAVEGAÇÃO DO MENU ──

    @FXML
    private void mostrarMenuAtaque() {
        painelPrincipal.setVisible(false);
        painelPrincipal.setManaged(false);
        painelMenuAtaque.setVisible(true);
        painelMenuAtaque.setManaged(true);
        
        // Atualiza dicas
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
        painelMenuAtaque.setVisible(false);
        painelMenuAtaque.setManaged(false);
        painelPrincipal.setVisible(true);
        painelPrincipal.setManaged(true);
        lblDicaAtaque.setText("");
    }

    // ── AÇÕES DE COMBATE ──

    @FXML
    private void atacarComArma() {
        processarTurno(() -> {
            int dano = jogador.calcularDanoFisico();
            Elemento elemAtaque = jogador.getElementoAtaqueAtual();
            double mult = ElementoService.calcularMultiplicador(elemAtaque, inimigo.getElemento());
            
            int danoFinal = (int)(dano * mult);
            inimigo.setVida(Math.max(0, inimigo.getVida() - danoFinal));
            
            exibirEfetividade(mult);
            lblEventos.setText("⚔ Você atacou com " + jogador.getArmaEquipada().getNome() + " causando " + danoFinal + " de dano!");
        });
    }

    @FXML
    private void atacarEspecial() {
        int custo = jogador.custoAtaqueEspecial();
        if (jogador.getPontosEsforco() < custo) return;

        processarTurno(() -> {
            jogador.setPontosEsforco(jogador.getPontosEsforco() - custo);
            int dano = jogador.calcularDanoEspecial();
            
            // Ataque especial também pode ser elemental se a arma estiver amaldiçoada
            Elemento elemAtaque = jogador.getElementoAtaqueAtual();
            double mult = ElementoService.calcularMultiplicador(elemAtaque, inimigo.getElemento());
            
            int danoFinal = (int)(dano * mult);
            inimigo.setVida(Math.max(0, inimigo.getVida() - danoFinal));
            
            exibirEfetividade(mult);
            lblEventos.setText("💥 ATAQUE ESPECIAL! Você causou " + danoFinal + " de dano!");
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

        processarTurno(() -> {
            boolean ok = RitualService.usarRitual(jogador, inimigo);
            if (ok) {
                lblEventos.setText("☽ Você conjurou " + jogador.getRitualEquipado().getNome() + "!");
                // O RitualService já trata o dano e o multiplicador elemental
            }
        });
    }

    @FXML
    private void amaldicoarArma() {
        if (jogador.amaldicoarArma()) {
            lblEventos.setText("🔥 Você amaldiçoou sua arma com o elemento " + jogador.getElemento() + "!");
            atualizarInterface();
            fecharMenuAtaque();
        }
    }

    private void processarTurno(Runnable acaoJogador) {
        // 1. Ação do Jogador
        acaoJogador.run();
        atualizarInterface();
        fecharMenuAtaque();

        // 2. Verificar se inimigo morreu
        if (!inimigo.estaVivo()) {
            finalizarCombate(true);
            return;
        }

        // 3. Contra-ataque do Inimigo (simples por enquanto)
        int danoInimigo = inimigo.getDano();
        jogador.setVida(Math.max(0, jogador.getVida() - danoInimigo));
        lblEventos.setText(lblEventos.getText() + "\n💀 O inimigo contra-ataca causando " + danoInimigo + " de dano!");
        
        atualizarInterface();

        // 4. Verificar se jogador morreu
        if (!jogador.estaVivo()) {
            finalizarCombate(false);
        }
    }

    private void exibirEfetividade(double mult) {
        if (mult > 1.0) {
            lblEfetividade.setText("SUPER EFETIVO! (x" + mult + ")");
            lblEfetividade.setStyle("-fx-text-fill: #2ecc71;");
        } else if (mult < 1.0 && mult > 0) {
            lblEfetividade.setText("POUCO EFETIVO... (x" + mult + ")");
            lblEfetividade.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            lblEfetividade.setText("");
        }
    }

    private void finalizarCombate(boolean vitoria) {
        painelPrincipal.setVisible(false);
        painelPrincipal.setManaged(false);
        painelMenuAtaque.setVisible(false);
        painelMenuAtaque.setManaged(false);
        btnVoltarMissao.setVisible(true);
        btnVoltarMissao.setManaged(true);

        if (vitoria) {
            int xp = inimigo.getXpConcedido();
            jogador.ganharXp(xp);
            jogador.encerrarBatalha(); // limpa buffs

            // Recompensa em dinheiro: inimigos normais dão 20-50 moedas; bosses dão mais
            int dinheiro = inimigo.isBossFinal()  ? 500
                         : inimigo.isBossMissao() ? 200
                         : 20 + new java.util.Random().nextInt(31); // 20-50
            jogador.adicionarDinheiro(dinheiro);

            // Registrar boss de missão como derrotado
            if (inimigo.isBossMissao()) {
                int idx = MissaoService.getIndiceMissao(inimigo.getElemento());
                GameState.setBossMissaoDerrotado(idx, true);
            }

            // Mensagem de vitória
            String msgBoss = inimigo.isBossFinal()  ? "\n💀 BOSS FINAL DERROTADO! Você completou o jogo!"
                           : inimigo.isBossMissao() ? "\n☠ BOSS DA MISSÃO DERROTADO!"
                           : "";
            lblEventos.setText("🏆 VITÓRIA! " + inimigo.getNome() + " foi derrotado!"
                + "\n+" + xp + " XP  |  +" + dinheiro + " moedas" + msgBoss);
        } else {
            // Punição Parte 12 (Ajustada): Perde páginas parcial e dinheiro
            boolean contraBoss = inimigo.isBoss();
            GameState.perderPaginasParcial(contraBoss);
            
            int perda = jogador.getDinheiro() / 4;
            jogador.setDinheiro(jogador.getDinheiro() - perda);
            
            String msgPerda = contraBoss ? "Você voltou com 4 páginas." : "Você perdeu metade das páginas.";
            lblEventos.setText("☠ DERROTA... Você sucumbiu ao paranormal.\n" + msgPerda + " Perdeu " + perda + " moedas.");
            btnVoltarMissao.setText("Voltar ao QG");
        }
    }

    @FXML
    private void abrirInventario(ActionEvent event) {
        GameState.setOrigemInventario("COMBATE");
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/inventario.fxml");
    }

    @FXML
    private void fugir(ActionEvent event) {
        // Punição por fuga: perde páginas parcial e um pouco de dinheiro
        GameState.perderPaginasParcial(false);
        int perda = jogador.getDinheiro() / 10;
        jogador.setDinheiro(jogador.getDinheiro() - perda);
        
        jogador.encerrarBatalha();
        GameState.setVeioDeFuga(true);
        TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
    }

    @FXML
    private void voltarMissao(ActionEvent event) {
        if (!jogador.estaVivo()) {
            GameState.setVeioDeDerrota(true);
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/status.fxml");
        } else {
            TelaUtil.trocarTela(event, "/com/mycompany/fragmentoparanormal/view/missao.fxml");
        }
    }
}
