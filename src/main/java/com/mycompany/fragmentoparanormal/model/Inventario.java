package com.mycompany.fragmentoparanormal.model;

import java.util.ArrayList;


public class Inventario {
    private ArrayList<Item> itens;

    public Inventario() {
        itens = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        itens.add(item);
    }

    public ArrayList<Item> getItens() {
        return itens;
    }
}
