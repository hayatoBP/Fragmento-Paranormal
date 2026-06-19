package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.util.Elemento;
import java.util.ArrayList;
import java.util.List;

public class MissaoService {

    // Cada missão exige 7 páginas — total de 28 no jogo
    private static final ArrayList<Missao> missoes = new ArrayList<>();

    static {
        Missao missaoSangue = new Missao(
            "Páginas de Sangue", Elemento.SANGUE,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_sangue.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_sangue.txt",
            7, 1
        );
        missaoSangue.setLocais(criarLocaisMissao(Elemento.SANGUE));
        missoes.add(missaoSangue);
        Missao missaoMorte = new Missao(
            "Ecos da Morte", Elemento.MORTE,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_morte.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_morte.txt",
            7, 2
        );
        missaoMorte.setLocais(criarLocaisMissao(Elemento.MORTE));
        missoes.add(missaoMorte);
        Missao missaoEnergia = new Missao(
            "Ruído da Energia", Elemento.ENERGIA,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_energia.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_energia.txt",
            7, 3
        );
        missaoEnergia.setLocais(criarLocaisMissao(Elemento.ENERGIA));
        missoes.add(missaoEnergia);
        Missao missaoConhecimento = new Missao(
            "Segredos do Conhecimento", Elemento.CONHECIMENTO,
            "/com/mycompany/fragmentoparanormal/images/cenarios/cenario_conhecimento.png",
            "/com/mycompany/fragmentoparanormal/dialogos/dialogo_conhecimento.txt",
            7, 4
        );
        missaoConhecimento.setLocais(criarLocaisMissao(Elemento.CONHECIMENTO));
        missoes.add(missaoConhecimento);
    }

    public static ArrayList<Missao> getMissoes() { return missoes; }

    public static Missao getProximaMissao() {
        for (Missao m : missoes) {
            if (!m.isConcluida()) return m;
        }
        return null;
    }

    public static int getIndiceMissao(Elemento elemento) {
        return switch (elemento) {
            case SANGUE       -> 0;
            case MORTE        -> 1;
            case ENERGIA      -> 2;
            case CONHECIMENTO -> 3;
            default           -> -1;
        };
    }

    public static boolean campanhaConcluida() {
        // Verifica no GameState se todas as páginas de todas as missões foram coletadas
        for (int i = 0; i < 4; i++) {
            if (missoes.get(i).getLocais().stream().filter(l -> l.isPaginaEncontrada()).count() < 7) {
                return false;
            }
        }
        return true;
    }

    private static List<LocalMapa> criarLocaisMissao(Elemento elemento) {
        List<LocalMapa> locais = new ArrayList<>();
        String imgBase = "/com/mycompany/fragmentoparanormal/images/cenarios/";

        switch (elemento) {
            case SANGUE:
                locais.add(new LocalMapa(0, "Entrada do Hospital", "Um hospital abandonado, com cheiro de sangue e desespero.", imgBase + "hospital_entrada.png", elemento));
                locais.add(new LocalMapa(1, "Recepção Abandonada", "A recepção, coberta de poeira e com marcas de arranhões.", imgBase + "hospital_recepcao.png", elemento));
                locais.add(new LocalMapa(2, "Corredor dos Leitos", "Leitos vazios e enferrujados, sussurros ecoam.", imgBase + "hospital_corredor.png", elemento));
                locais.add(new LocalMapa(3, "Sala de Cirurgia", "Instrumentos cirúrgicos espalhados, manchas escuras no chão.", imgBase + "hospital_cirurgia.png", elemento));
                locais.add(new LocalMapa(4, "UTI Destruída", "Equipamentos quebrados, a sensação de que algo terrível aconteceu.", imgBase + "hospital_uti.png", elemento));
                locais.add(new LocalMapa(5, "Necrotério", "O frio da morte paira no ar, gavetas abertas.", imgBase + "hospital_necroterio.png", elemento));
                locais.add(new LocalMapa(6, "Ala Isolada", "Um lugar de onde ninguém nunca saiu. Gritos abafados.", imgBase + "hospital_ala_isolada.png", elemento));
                locais.add(new LocalMapa(7, "Câmara de Sangue", "O coração pulsante da anomalia. O boss espera.", imgBase + "boss_sangue.png", elemento, true));
                break;
            case MORTE:
                locais.add(new LocalMapa(0, "Cemitério Esquecido", "Lápides quebradas e névoa densa. Um lugar de descanso eterno... ou não.", imgBase + "cemiterio_entrada.png", elemento));
                locais.add(new LocalMapa(1, "Mausoléu Profanado", "Tumbas abertas, o cheiro de terra e decomposição.", imgBase + "cemiterio_mausoleu.png", elemento));
                locais.add(new LocalMapa(2, "Catacumbas Sombrias", "Corredores estreitos e úmidos, ossos empilhados.", imgBase + "cemiterio_catacumbas.png", elemento));
                locais.add(new LocalMapa(3, "Cripta dos Lamentos", "Sons de choro e sussurros vêm das paredes.", imgBase + "cemiterio_cripta.png", elemento));
                locais.add(new LocalMapa(4, "Jardim dos Espíritos", "Árvores retorcidas e vultos fantasmagóricos.", imgBase + "cemiterio_jardim.png", elemento));
                locais.add(new LocalMapa(5, "Capela Abandonada", "Um altar destruído, velas apagadas. Um ritual foi interrompido.", imgBase + "cemiterio_capela.png", elemento));
                locais.add(new LocalMapa(6, "Fenda Dimensional", "Uma rachadura no tecido da realidade. Onde os mortos não descansam.", imgBase + "cemiterio_fenda.png", elemento));
                locais.add(new LocalMapa(7, "Salão da Ceifadora", "A presença da Morte é esmagadora. O boss aguarda.", imgBase + "boss_morte.png", elemento, true));
                break;
            case ENERGIA:
                locais.add(new LocalMapa(0, "Usina Desativada", "Máquinas enferrujadas e cabos expostos. A energia ainda pulsa.", imgBase + "usina_entrada.png", elemento));
                locais.add(new LocalMapa(1, "Sala de Controle", "Painéis de controle destruídos, faíscas elétricas.", imgBase + "usina_controle.png", elemento));
                locais.add(new LocalMapa(2, "Gerador Principal", "Um gerador gigante, emitindo um zumbido estranho.", imgBase + "usina_gerador.png", elemento));
                locais.add(new LocalMapa(3, "Torre de Resfriamento", "Vapor e condensação. Algo se esconde na névoa.", imgBase + "usina_torre.png", elemento));
                locais.add(new LocalMapa(4, "Subestação Elétrica", "Transformadores explodidos, ar carregado de eletricidade.", imgBase + "usina_subestacao.png", elemento));
                locais.add(new LocalMapa(5, "Laboratório Secreto", "Experimentos abandonados, anotações sobre energia paranormal.", imgBase + "usina_laboratorio.png", elemento));
                locais.add(new LocalMapa(6, "Câmara de Confinamento", "Uma cela de contenção, algo muito poderoso foi mantido aqui.", imgBase + "usina_confinamento.png", elemento));
                locais.add(new LocalMapa(7, "Núcleo Instável", "A fonte de toda a energia. O boss está no centro.", imgBase + "boss_energia.png", elemento, true));
                break;
            case CONHECIMENTO:
                locais.add(new LocalMapa(0, "Biblioteca Antiga", "Prateleiras empoeiradas e livros esquecidos. Conhecimento proibido.", imgBase + "biblioteca_entrada.png", elemento));
                locais.add(new LocalMapa(1, "Arquivo Secreto", "Documentos confidenciais e registros ocultos.", imgBase + "biblioteca_arquivo.png", elemento));
                locais.add(new LocalMapa(2, "Sala de Leitura Proibida", "Livros acorrentados, sussurros de mentes antigas.", imgBase + "biblioteca_leitura.png", elemento));
                locais.add(new LocalMapa(3, "Observatório Astral", "Telescópios apontados para o vazio. Estrelas que não deveriam existir.", imgBase + "biblioteca_observatorio.png", elemento));
                locais.add(new LocalMapa(4, "Câmara dos Enigmas", "Quebra-cabeças e ilusões. A mente é o maior inimigo.", imgBase + "biblioteca_enigmas.png", elemento));
                locais.add(new LocalMapa(5, "Salão dos Sábios", "Estátuas de pensadores antigos, com olhos que parecem seguir você.", imgBase + "biblioteca_sabios.png", elemento));
                locais.add(new LocalMapa(6, "Vazio Cognitivo", "Um lugar onde a realidade se dobra. A sanidade é testada.", imgBase + "biblioteca_vazio.png", elemento));
                locais.add(new LocalMapa(7, "Mente Coletiva", "A consciência de todos os segredos. O boss te espera.", imgBase + "boss_conhecimento.png", elemento, true));
                break;
        }
        return locais;
    }
}
