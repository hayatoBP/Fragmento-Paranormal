package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.dao.CampanhaDAO;
import com.mycompany.fragmentoparanormal.model.LocalMapa;
import com.mycompany.fragmentoparanormal.model.Missao;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MissaoService {

    /** Lista em memória — criada uma única vez por sessão de jogo. */
    private static List<Missao> missoesSessao = null;

    /**
     * Retorna a lista de missões da sessão atual.
     * Se ainda não foram carregadas, cria e aplica o progresso salvo no banco.
     */
    public static List<Missao> carregarMissoes() {
        if (missoesSessao != null) return missoesSessao;

        missoesSessao = new ArrayList<>();
        missoesSessao.add(new Missao("O Hospital de Sangue",         Elemento.SANGUE,       "/com/mycompany/fragmentoparanormal/images/cenarios/sangue/1.png",       "dialogo_sangue.txt",       7, 0));
        missoesSessao.add(new Missao("O Cemitério da Morte",          Elemento.MORTE,        "/com/mycompany/fragmentoparanormal/images/cenarios/morte/1.png",        "dialogo_morte.txt",        7, 1));
        missoesSessao.add(new Missao("A Usina de Energia",            Elemento.ENERGIA,      "/com/mycompany/fragmentoparanormal/images/cenarios/energia/1.png",      "dialogo_energia.txt",      7, 2));
        missoesSessao.add(new Missao("A Biblioteca do Conhecimento",  Elemento.CONHECIMENTO, "/com/mycompany/fragmentoparanormal/images/cenarios/conhecimento/1.png", "dialogo_conhecimento.txt", 7, 3));

        for (Missao m : missoesSessao) {
            m.setLocais(gerarLocaisParaMissao(m.getElemento()));
        }
        return missoesSessao;
    }

    /**
     * Aplica o progresso salvo no banco para um jogador específico.
     * Deve ser chamado uma vez ao carregar o jogador existente.
     */
    public static void aplicarProgressoSalvo(int jogadorId) {
        List<Missao> missoes = carregarMissoes();
        Map<Elemento, CampanhaDAO.CampanhaData> salvo = CampanhaDAO.carregarCampanhas(jogadorId);

        for (Missao m : missoes) {
            CampanhaDAO.CampanhaData data = salvo.get(m.getElemento());
            if (data == null) continue;

            if (data.concluida()) m.concluir();
            m.setLocalAtual(data.localAtual());

            // Aplicar quais páginas/locais já foram encontrados
            if (data.paginasLocaisEncontradas() != null && !data.paginasLocaisEncontradas().isBlank()) {
                m.setPaginasLocaisEncontradasFromString(data.paginasLocaisEncontradas());
            }

            // Liberar locais conforme o progresso (todos até localAtual)
            for (int i = 0; i <= data.localAtual() && i < m.getLocais().size(); i++) {
                m.getLocais().get(i).setLiberado(true);
            }

            // Boss desbloqueado globalmente
            if (data.bossDesbloqueado()) {
                GameState.setBossDesbloqueado(true);
            }
        }
    }

    /** Reseta a lista em memória (usar ao iniciar novo jogo). */
    public static void resetarSessao() {
        missoesSessao = null;
    }

    public static boolean campanhaConcluida() {
        if (missoesSessao == null) return false;
        return missoesSessao.stream().allMatch(Missao::isConcluida);
    }

    /** Alias para acesso direto à lista de missões em memória. */
    public static List<Missao> getMissoes() {
        return carregarMissoes();
    }

    public static int getIndiceMissao(Elemento elemento) {
        return switch (elemento) {
            case SANGUE       -> 0;
            case MORTE        -> 1;
            case ENERGIA      -> 2;
            case CONHECIMENTO -> 3;
            default           -> 0;
        };
    }

    private static List<LocalMapa> gerarLocaisParaMissao(Elemento elemento) {
        List<LocalMapa> locais = new ArrayList<>();
        String imgBase = "/com/mycompany/fragmentoparanormal/images/cenarios/" + elemento.toString().toLowerCase() + "/";

        switch (elemento) {
            case SANGUE:
                locais.add(new LocalMapa(0, "Recepção Sangrenta",    "Paredes manchadas e silêncio absoluto.",                    imgBase + "1.png", elemento));
                locais.add(new LocalMapa(1, "Corredor das Sombras",  "Luzes piscando, o cheiro de ferro é forte.",                imgBase + "2.png", elemento));
                locais.add(new LocalMapa(2, "Quarto 404",            "Algo não quer que você saia daqui.",                        imgBase + "3.png", elemento));
                locais.add(new LocalMapa(3, "Laboratório Macabro",   "Experimentos que deram muito errado.",                      imgBase + "4.png", elemento));
                locais.add(new LocalMapa(4, "UTI do Horror",         "Máquinas mantendo vivo o que deveria estar morto.",         imgBase + "5.png", elemento));
                locais.add(new LocalMapa(5, "Necrotério Frio",       "Onde os corpos não descansam.",                             imgBase + "6.png", elemento));
                locais.add(new LocalMapa(6, "Câmara de Sangue",      "O coração da anomalia. O Boss espera.",                     imgBase + "boss.png", elemento, true));
                break;
            case MORTE:
                locais.add(new LocalMapa(0, "Entrada do Cemitério",  "Lápides quebradas e névoa densa.",                         imgBase + "1.png", elemento));
                locais.add(new LocalMapa(1, "Mausoléu Profanado",    "O cheiro de terra e decomposição.",                        imgBase + "2.png", elemento));
                locais.add(new LocalMapa(2, "Catacumbas Úmidas",     "Corredores estreitos cheios de ossos.",                    imgBase + "3.png", elemento));
                locais.add(new LocalMapa(3, "Cripta dos Lamentos",   "Sussurros vêm das paredes.",                               imgBase + "4.png", elemento));
                locais.add(new LocalMapa(4, "Jardim dos Espíritos",  "Árvores retorcidas e vultos.",                             imgBase + "5.png", elemento));
                locais.add(new LocalMapa(5, "Capela Abandonada",     "Um ritual interrompido.",                                  imgBase + "6.png", elemento));
                locais.add(new LocalMapa(6, "Salão da Ceifadora",    "A presença da Morte é esmagadora. O Boss aguarda.",        imgBase + "boss.png", elemento, true));
                break;
            case ENERGIA:
                locais.add(new LocalMapa(0, "Pátio da Usina",            "Cabos expostos e faíscas elétricas.",                  imgBase + "1.png", elemento));
                locais.add(new LocalMapa(1, "Sala de Controle",          "Painéis destruídos, zumbido constante.",               imgBase + "2.png", elemento));
                locais.add(new LocalMapa(2, "Gerador Principal",         "Uma máquina gigante emitindo luz azul.",               imgBase + "3.png", elemento));
                locais.add(new LocalMapa(3, "Torre de Resfriamento",     "Vapor denso escondendo perigos.",                      imgBase + "4.png", elemento));
                locais.add(new LocalMapa(4, "Subestação Crítica",        "O ar está carregado de eletricidade.",                 imgBase + "5.png", elemento));
                locais.add(new LocalMapa(5, "Laboratório de Confinamento","Algo poderoso foi mantido aqui.",                     imgBase + "6.png", elemento));
                locais.add(new LocalMapa(6, "Núcleo Instável",           "A fonte de toda a energia. O Boss está no centro.",    imgBase + "boss.png", elemento, true));
                break;
            case CONHECIMENTO:
                locais.add(new LocalMapa(0, "Átrio da Biblioteca",    "Prateleiras infinitas e poeira.",                          imgBase + "1.png", elemento));
                locais.add(new LocalMapa(1, "Arquivo Proibido",       "Documentos que não deveriam existir.",                     imgBase + "2.png", elemento));
                locais.add(new LocalMapa(2, "Sala de Leitura Oculta", "Sussurros de mentes antigas.",                             imgBase + "3.png", elemento));
                locais.add(new LocalMapa(3, "Observatório do Vazio",  "Estrelas que não pertencem a este céu.",                   imgBase + "4.png", elemento));
                locais.add(new LocalMapa(4, "Câmara dos Enigmas",     "A realidade se dobra aos seus pés.",                       imgBase + "5.png", elemento));
                locais.add(new LocalMapa(5, "Salão dos Sábios",       "Estátuas que parecem julgar você.",                        imgBase + "6.png", elemento));
                locais.add(new LocalMapa(6, "Mente Coletiva",         "A consciência de todos os segredos. O Boss te espera.",    imgBase + "boss.png", elemento, true));
                break;
        }
        return locais;
    }
}
