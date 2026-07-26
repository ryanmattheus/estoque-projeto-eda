package com.estoque.estruturas.fila;

import com.estoque.estruturas.pilha.Pilha;
import com.estoque.util.Log;

import java.util.NoSuchElementException;

/**
 * Fila (Queue) generica FIFO, implementada EXCLUSIVAMENTE a partir de
 * duas instancias de Pilha (requisito do projeto). Cada Pilha, por sua
 * vez, e implementada com uma Lista Simplesmente Encadeada.
 *
 * Estrategia classica "fila com duas pilhas":
 *  - pilhaEntrada: recebe os elementos na chegada (enfileirar = push).
 *  - pilhaSaida: fornece os elementos na ordem correta de saida.
 *
 * Quando pilhaSaida esta vazia e e necessario desenfileirar/espiar,
 * todos os elementos de pilhaEntrada sao transferidos (pop/push) para
 * pilhaSaida, o que inverte a ordem e restaura o comportamento FIFO.
 *
 * Complexidade amortizada: O(1) por operacao (cada elemento e movido de
 * uma pilha para a outra no maximo uma vez).
 *
 * Usada no sistema para representar os pedidos de compra a serem
 * processados em ordem de chegada (fila de reposicao de estoque).
 */
public class Fila<T> {

    private final Pilha<T> pilhaEntrada;
    private final Pilha<T> pilhaSaida;

    public Fila() {
        this.pilhaEntrada = new Pilha<>();
        this.pilhaSaida = new Pilha<>();
    }

    /** Enfileira (enqueue) um elemento no final da fila. Custo O(1). */
    public void enfileirar(T valor) {
        pilhaEntrada.empilhar(valor);
        Log.log("FILA", "enfileirar(" + valor + ") -> tamanho=" + tamanho());
    }

    /**
     * Remove e retorna o elemento no inicio da fila (o mais antigo).
     * Custo O(1) amortizado.
     */
    public T desenfileirar() {
        transferirSeNecessario();
        if (pilhaSaida.estaVazia()) {
            throw new NoSuchElementException("Fila vazia: nao e possivel desenfileirar.");
        }
        T valor = pilhaSaida.desempilhar();
        Log.log("FILA", "desenfileirar() -> " + valor + " | tamanho=" + tamanho());
        return valor;
    }

    /** Observa o elemento no inicio da fila sem remove-lo. Custo O(1) amortizado. */
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

    /** Move todos os elementos de pilhaEntrada para pilhaSaida quando esta esta vazia. */
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
