package com.estoque.estruturas.lista;

import com.estoque.util.Log;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ListaSimplesmenteEncadeada<T> implements Iterable<T> {

    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public ListaSimplesmenteEncadeada() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    // Insere no inicio da lista. Custo O(1).
    public void inserirInicio(T valor) {
        No<T> novo = new No<>(valor);
        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            novo.setProximo(inicio);
            inicio = novo;
        }
        tamanho++;
        Log.log("LISTA", "inserirInicio(" + valor + ") -> tamanho=" + tamanho);
    }

    // Insere no final da lista. Custo O(1) (mantemos ponteiro para o fim)
    public void inserirFim(T valor) {
        No<T> novo = new No<>(valor);
        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.setProximo(novo);
            fim = novo;
        }
        tamanho++;
        Log.log("LISTA", "inserirFim(" + valor + ") -> tamanho=" + tamanho);
    }

    // Remove e retorna o elemento do inicio da lista. Custo O(1)
    public T removerInicio() {
        if (estaVazia()) {
            throw new NoSuchElementException("Lista vazia: nao ha elemento para remover.");
        }
        No<T> removido = inicio;
        inicio = inicio.getProximo();
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        Log.log("LISTA", "removerInicio() -> " + removido.getValor() + " | tamanho=" + tamanho);
        return removido.getValor();
    }

    // Remove a primeira ocorrencia do valor informado (comparado via equals). Custo
    // O(n) - implementacao iterativa.
    // return true se removeu, false se nao encontrado.
    public boolean remover(T valor) {
        if (estaVazia()) {
            return false;
        }
        if (Objects.equals(inicio.getValor(), valor)) {
            removerInicio();
            return true;
        }
        No<T> anterior = inicio;
        No<T> atual = inicio.getProximo();
        while (atual != null) {
            if (Objects.equals(atual.getValor(), valor)) {
                anterior.setProximo(atual.getProximo());
                if (atual == fim) {
                    fim = anterior;
                }
                tamanho--;
                Log.log("LISTA", "remover(" + valor + ") -> encontrado e removido | tamanho=" + tamanho);
                return true;
            }
            anterior = atual;
            atual = atual.getProximo();
        }
        Log.log("LISTA", "remover(" + valor + ") -> nao encontrado");
        return false;
    }

    // Busca (contains) um valor na lista. Custo O(n) - iterativo
    public boolean contem(T valor) {
        No<T> atual = inicio;
        while (atual != null) {
            if (Objects.equals(atual.getValor(), valor)) {
                return true;
            }
            atual = atual.getProximo();
        }
        return false;
    }

    // Retorna o valor no inicio da lista sem remover
    public T verInicio() {
        if (estaVazia()) {
            throw new NoSuchElementException("Lista vazia.");
        }
        return inicio.getValor();
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public int tamanho() {
        return tamanho;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private No<T> atual = inicio;

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T valor = atual.getValor();
                atual = atual.getProximo();
                return valor;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        No<T> atual = inicio;
        while (atual != null) {
            sb.append(atual.getValor());
            if (atual.getProximo() != null) {
                sb.append(" -> ");
            }
            atual = atual.getProximo();
        }
        sb.append("]");
        return sb.toString();
    }
}
