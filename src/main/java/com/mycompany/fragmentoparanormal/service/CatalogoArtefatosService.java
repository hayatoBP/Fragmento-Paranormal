package com.mycompany.fragmentoparanormal.service;

import com.mycompany.fragmentoparanormal.model.Artefato;
import java.util.List;
import java.util.Random;

/**
 * Catálogo de artefatos encontráveis no jogo.
 * Cada artefato tem bônus passivos em um ou dois atributos.
 * Distribuição: 2~3 artefatos por atributo, 15 artefatos no total.
 */
public class CatalogoArtefatosService {

    private static final Random random = new Random();

    private static final List<Artefato> TODOS = List.of(

        // ── Força (3 artefatos) ──────────────────────────────────────
        new Artefato.Builder()
            .nome("Punho de Ferro")
            .descricao("Uma luva reforçada com metal paranormal. Potencializa golpes físicos.")
            .forca(8).build(),

        new Artefato.Builder()
            .nome("Colar do Predador")
            .descricao("Presas de uma criatura desconhecida. Aumenta a ferocidade nos ataques.")
            .forca(12).build(),

        new Artefato.Builder()
            .nome("Relíquia Marcial")
            .descricao("Artefato militar antigo. Forja a força do usuário além dos limites.")
            .forca(6).vidaMaxima(10).build(),

        // ── Investigação (3 artefatos) ───────────────────────────────
        new Artefato.Builder()
            .nome("Lupa do Além")
            .descricao("Permite enxergar rastros paranormais invisíveis a olho nu.")
            .investigacao(10).build(),

        new Artefato.Builder()
            .nome("Diário Cifrado")
            .descricao("Anotações de um agente perdido. Cada página revela uma técnica.")
            .investigacao(8).build(),

        new Artefato.Builder()
            .nome("Amuleto da Percepção")
            .descricao("Aguça os sentidos. Aumenta a chance de encontrar fragmentos.")
            .investigacao(12).build(),

        // ── Poder Paranormal (3 artefatos) ───────────────────────────
        new Artefato.Builder()
            .nome("Amuleto das Sombras")
            .descricao("Artefato antigo. Conecta o usuário às forças do além.")
            .poderParanormal(10).build(),

        new Artefato.Builder()
            .nome("Cristal do Abismo")
            .descricao("Brilha no escuro. Amplifica o poder elemental do portador.")
            .poderParanormal(8).build(),

        new Artefato.Builder()
            .nome("Olho de Pedra")
            .descricao("Um globo ocular petrificado de criatura paranormal. Pulsa com energia.")
            .poderParanormal(15).build(),

        // ── Vida Máxima (3 artefatos) ────────────────────────────────
        new Artefato.Builder()
            .nome("Fragmento Ossificado")
            .descricao("Osso de criatura de outro plano. Fortalece o corpo do portador.")
            .vidaMaxima(20).build(),

        new Artefato.Builder()
            .nome("Relíquia da Ordem")
            .descricao("Símbolo sagrado da Ordem Paranormal. Protege a vitalidade.")
            .vidaMaxima(25).build(),

        new Artefato.Builder()
            .nome("Coração de Âmbar")
            .descricao("Órgão cristalizado. Aumenta a resistência vital permanentemente.")
            .vidaMaxima(15).peMaximo(5).build(),

        // ── PE Máximo (3 artefatos) ──────────────────────────────────
        new Artefato.Builder()
            .nome("Pedra do Esforço")
            .descricao("Rocha retirada do núcleo de um portal. Expande a reserva de PE.")
            .peMaximo(15).build(),

        new Artefato.Builder()
            .nome("Frasco do Destilado")
            .descricao("Essência de energia paranormal solidificada. Aumenta a resistência ao esforço.")
            .peMaximo(20).build(),

        new Artefato.Builder()
            .nome("Símbolo Arcano")
            .descricao("Gravura de uma ordem extinta. Amplifica a reserva de poder.")
            .peMaximo(12).poderParanormal(5).build(),

        new Artefato.Builder()
            .nome("Livro Paranormal")
            .descricao("Um tomo repleto de segredos proibidos. Aumenta drasticamente o seu Poder Paranormal.")
            .poderParanormal(15).build()
    );

    public static List<Artefato> getTodos() { return TODOS; }

    public static Artefato sortearAleatorio() {
        return TODOS.get(random.nextInt(TODOS.size()));
    }
}
