package com.estoque.estruturas.avl;

/**
 * No de uma Arvore AVL (Arvore Binaria de Busca balanceada).
 * Armazena um par chave/valor (ex.: codigo do produto -> Produto).
 */
public class NoAVL<K extends Comparable<K>, V> {

    K chave;
    V valor;
    NoAVL<K, V> esquerda;
    NoAVL<K, V> direita;
    int altura;

    NoAVL(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
        this.esquerda = null;
        this.direita = null;
        this.altura = 1; // folha recem criada tem altura 1
    }

    public K getChave() {
        return chave;
    }

    public V getValor() {
        return valor;
    }
}
