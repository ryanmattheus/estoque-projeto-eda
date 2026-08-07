package com.estoque.estruturas.pilha;

import com.estoque.estruturas.lista.ListaSimplesmenteEncadeada;
import com.estoque.util.Log;

import java.util.NoSuchElementException;

public class Pilha<T> {

    private final ListaSimplesmenteEncadeada<T> lista;

    public Pilha() {
        this.lista = new ListaSimplesmenteEncadeada<>();
    }

    // Empilha (push) um elemento no topo. Custo O(1)
    public void empilhar(T valor) {
        lista.inserirInicio(valor);
        Log.log("PILHA", "push(" + valor + ") -> tamanho=" + lista.tamanho());
    }

    // Desempilha (pop) o elemento do topo. Custo O(1)
    public T desempilhar() {
        if (estaVazia()) {
            throw new NoSuchElementException("Pilha vazia: nao e possivel desempilhar.");
        }
        T valor = lista.removerInicio();
        Log.log("PILHA", "pop() -> " + valor + " | tamanho=" + lista.tamanho());
        return valor;
    }

    // Retorna o elemento do topo sem remove-lo. Custo O(1)
    public T topo() {
        if (estaVazia()) {
            throw new NoSuchElementException("Pilha vazia: nao ha topo.");
        }
        return lista.verInicio();
    }

    public boolean estaVazia() {
        return lista.estaVazia();
    }

    public int tamanho() {
        return lista.tamanho();
    }

    @Override
    public String toString() {
        return "Pilha(topo->fundo) " + lista.toString();
    }
}
