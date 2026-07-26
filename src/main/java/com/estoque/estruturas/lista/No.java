package com.estoque.estruturas.lista;

/**
 * No (nodo) de uma Lista Simplesmente Encadeada.
 * Armazena um valor generico e a referencia para o proximo no da lista.
 */
public class No<T> {

    private T valor;
    private No<T> proximo;

    public No(T valor) {
        this.valor = valor;
        this.proximo = null;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

    public No<T> getProximo() {
        return proximo;
    }

    public void setProximo(No<T> proximo) {
        this.proximo = proximo;
    }
}
