package com.mycompany.fragmentoparanormal.model;

import com.mycompany.fragmentoparanormal.util.ClassePersonagem;
import com.mycompany.fragmentoparanormal.util.Elemento;
import com.mycompany.fragmentoparanormal.util.Genero;
import java.util.ArrayList;

public class Personagem {

    private String nome;
    private ClassePersonagem classe;
    private Genero genero;
    private Elemento elemento;

    private int nivel;
    private int xpAtual;

    private int vida;
    private int vidaMaxima;       // <<< NOVO — para reset correto
    private int forca;
    private int investigacao;
    private int poderParanormal;
    private int pontosEsforco;
    private int peMaximo;         // <<< NOVO — para reset correto

    private int pontosAtributo;

    private String imagemBase;
    private Inventario inventario;
    private Arma armaEquipada;
    private Ritual ritualEquipado;
    private ArrayList<Ritual> rituais;

    public Personagem(String nome, ClassePersonagem classe, Genero genero, Elemento elemento) {
        this.nome = nome;
        this.classe = classe;
        this.genero = genero;
        this.elemento = elemento;
        nivel = 1;
        xpAtual = 0;
        pontosAtributo = 0;
        inventario = new Inventario();
        rituais = new ArrayList<>();
        configurarClasse();
        definirImagemBase();
    }

    private void configurarClasse() {
        switch (classe) {
            case COMBATENTE:
                vidaMaxima = 120; vida = 120;
                forca = 25; investigacao = 10;
                poderParanormal = 5;
                peMaximo = 20; pontosEsforco = 20;
                // Homem começa com Foice; Mulher começa com Faca
                armaEquipada = (genero == Genero.HOMEM)
                        ? new Arma("Foice",  18)
                        : new Arma("Faca",   12);
                break;
            case ESPECIALISTA:
                vidaMaxima = 100; vida = 100;
                forca = 15; investigacao = 25;
                poderParanormal = 10;
                peMaximo = 30; pontosEsforco = 30;
                // Ambos os gêneros começam com Pistola
                armaEquipada = new Arma("Pistola 9mm", 14);
                break;
            case OCULTISTA:
                vidaMaxima = 80; vida = 80;
                forca = 8; investigacao = 15;
                poderParanormal = 30;
                peMaximo = 50; pontosEsforco = 50;
                // Ambos os gêneros começam com Livro Paranormal (bônus mágico)
                armaEquipada = new Arma("Livro Paranormal", 8);
                aprenderRitual(new Ritual("Descarga Paranormal", 10, 20, 0, Elemento.ENERGIA));
                ritualEquipado = rituais.get(0);
                break;
        }
    }

    /**
     * Restaura vida e PE ao máximo.
     * Chamado ao entrar/reiniciar uma missão (após fuga ou nova tentativa).
     */
    public void resetarParaMissao() {
        vida = vidaMaxima;
        pontosEsforco = peMaximo;
    }

    private void definirImagemBase() {
        switch (classe) {
            case ESPECIALISTA:
                imagemBase = genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/arthur.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/erin.png";
                break;
            case COMBATENTE:
                imagemBase = genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/dominic.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/carina.png";
                break;
            case OCULTISTA:
                imagemBase = genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/dante.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/agatha.png";
                break;
        }
    }

    public String getImagemAtual() {
        if (armaEquipada == null) return imagemBase;
        switch (classe) {
            case ESPECIALISTA:
                return genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/arthur_arma.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/erin_arma.png";
            case COMBATENTE:
                return genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/dominic_arma.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/carina_arma.png";
            case OCULTISTA:
                return genero == Genero.HOMEM
                        ? "/com/mycompany/fragmentoparanormal/images/personagens/dante_arma.png"
                        : "/com/mycompany/fragmentoparanormal/images/personagens/agatha_arma.png";
        }
        return imagemBase;
    }

    public void ganharXp(int xp) {
        xpAtual += xp;
        while (xpAtual >= 100) {
            xpAtual -= 100;
            subirNivel();
        }
    }

    private void subirNivel() {
        nivel++;
        pontosAtributo += 5;
        // Sobe os máximos junto com o nível
        vidaMaxima += 10;
        peMaximo += 5;
        desbloquearRituais();
    }

    private void desbloquearRituais() {
        if (classe != ClassePersonagem.OCULTISTA) return;
        if (nivel == 3) aprenderRitual(new Ritual("Lâminas de Sangue", 20, 40, 0, Elemento.SANGUE));
        if (nivel == 5) aprenderRitual(new Ritual("Cicatrização", 25, 0, 50, Elemento.CONHECIMENTO));
        if (nivel == 7) aprenderRitual(new Ritual("Fim da Existência", 40, 90, 0, Elemento.MORTE));
    }

    public void aprenderRitual(Ritual ritual) { rituais.add(ritual); }

    public int calcularDanoFisico() {
        int dano = forca;
        if (armaEquipada != null) dano += armaEquipada.getBonusDano();
        if (classe == ClassePersonagem.COMBATENTE) dano += 10;
        return dano;
    }

    public int calcularDanoRitual() {
        int dano = poderParanormal;
        if (classe == ClassePersonagem.OCULTISTA) dano += 15;
        return dano;
    }

    public boolean estaVivo() { return vida > 0; }
    public boolean podeUsarCura() { return classe == ClassePersonagem.OCULTISTA; }

    // ---- getters / setters ----
    public String getNome()                          { return nome; }
    public ClassePersonagem getClasse()              { return classe; }
    public Genero getGenero()                        { return genero; }
    public Elemento getElemento()                    { return elemento; }
    public int getNivel()                            { return nivel; }
    public int getXpAtual()                          { return xpAtual; }
    public int getVida()                             { return vida; }
    public void setVida(int vida)                    { this.vida = vida; }
    public int getVidaMaxima()                       { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima)        { this.vidaMaxima = vidaMaxima; }
    public int getForca()                            { return forca; }
    public void setForca(int forca)                  { this.forca = forca; }
    public int getInvestigacao()                     { return investigacao; }
    public void setInvestigacao(int investigacao)    { this.investigacao = investigacao; }
    public int getPoderParanormal()                  { return poderParanormal; }
    public void setPoderParanormal(int v)            { this.poderParanormal = v; }
    public int getPontosEsforco()                    { return pontosEsforco; }
    public void setPontosEsforco(int v)              { this.pontosEsforco = v; }
    public int getPeMaximo()                         { return peMaximo; }
    public void setPeMaximo(int peMaximo)            { this.peMaximo = peMaximo; }
    public int getPontosAtributo()                   { return pontosAtributo; }
    public void setPontosAtributo(int v)             { this.pontosAtributo = v; }
    public Inventario getInventario()                { return inventario; }
    public Arma getArmaEquipada()                    { return armaEquipada; }
    public void setArmaEquipada(Arma armaEquipada)   { this.armaEquipada = armaEquipada; }
    public Ritual getRitualEquipado()                { return ritualEquipado; }
    public void setRitualEquipado(Ritual r)          { this.ritualEquipado = r; }
    public ArrayList<Ritual> getRituais()            { return rituais; }
}
