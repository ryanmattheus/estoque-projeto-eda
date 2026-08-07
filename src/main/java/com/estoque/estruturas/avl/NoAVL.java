package com.estoque.estruturas.avl;

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
