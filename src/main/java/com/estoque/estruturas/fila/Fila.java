package com.estoque.estruturas.fila;

import com.estoque.estruturas.pilha.Pilha;
import com.estoque.util.Log;

import java.util.NoSuchElementException;

public class Fila<T> {

    private final Pilha<T> pilhaEntrada;
    private final Pilha<T> pilhaSaida;

    public Fila() {
        this.pilhaEntrada = new Pilha<>();
        this.pilhaSaida = new Pilha<>();
    }

    // Enfileira (enqueue) um elemento no final da fila. Custo O(1)
    public void enfileirar(T valor) {
        pilhaEntrada.empilhar(valor);
        Log.log("FILA", "enfileirar(" + valor + ") -> tamanho=" + tamanho());
    }

    // Remove e retorna o elemento no inicio da fila (o mais antigo). Custo O(1)
    // amortizado

    public T desenfileirar() {
        transferirSeNecessario();
        if (pilhaSaida.estaVazia()) {
            throw new NoSuchElementException("Fila vazia: nao e possivel desenfileirar.");
        }
        T valor = pilhaSaida.desempilhar();
        Log.log("FILA", "desenfileirar() -> " + valor + " | tamanho=" + tamanho());
        return valor;
    }

    // Observa o elemento no inicio da fila sem remove-lo. Custo O(1) amortizado
    public T espiar() {
        transferirSeNecessario();
        if (pilhaSaida.estaVazia()) {
            throw new NoSuchElementException("Fila vazia: nao ha elemento no inicio.");
        }
        return pilhaSaida.topo();
    }

    public boolean estaVazia() {
        return pilhaEntrada.estaVazia() && pilhaSaida.estaVazia();
    }

    public int tamanho() {
        return pilhaEntrada.tamanho() + pilhaSaida.tamanho();
    }

    // Move todos os elementos de pilhaEntrada para pilhaSaida quando esta esta
    // vazia
    private void transferirSeNecessario() {
        if (pilhaSaida.estaVazia()) {
            Log.log("FILA", "pilhaSaida vazia -> transferindo elementos de pilhaEntrada");
            while (!pilhaEntrada.estaVazia()) {
                pilhaSaida.empilhar(pilhaEntrada.desempilhar());
            }
        }
    }

    @Override
    public String toString() {
        return "Fila{entrada=" + pilhaEntrada + ", saida=" + pilhaSaida + "}";
    }
}
