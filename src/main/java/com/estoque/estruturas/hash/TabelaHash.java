package com.estoque.estruturas.hash;

import com.estoque.estruturas.lista.ListaSimplesmenteEncadeada;
import com.estoque.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Tabela Hash generica com tratamento de colisao por ENCADEAMENTO
 * (chaining), utilizando a Lista Simplesmente Encadeada implementada
 * pelo grupo como estrutura de cada "balde" (bucket).
 *
 * Usada no sistema para busca rapida de produto por NOME (chave String),
 * com custo medio O(1) para busca/insercao/remocao.
 *
 * Fator de carga (load factor) e monitorado; quando ultrapassa 0.75 a
 * tabela e redimensionada (rehash), mantendo o desempenho medio O(1).
 */
public class TabelaHash<K, V> {

    private static final int CAPACIDADE_INICIAL = 16;
    private static final double FATOR_CARGA_MAXIMO = 0.75;

    private ListaSimplesmenteEncadeada<Entrada<K, V>>[] baldes;
    private int quantidadeElementos;

    @SuppressWarnings("unchecked")
    public TabelaHash() {
        this.baldes = new ListaSimplesmenteEncadeada[CAPACIDADE_INICIAL];
        this.quantidadeElementos = 0;
    }

    /** Par chave/valor armazenado em cada no da lista encadeada do balde. */
    private static class Entrada<K, V> {
        final K chave;
        V valor;

        Entrada(K chave, V valor) {
            this.chave = chave;
            this.valor = valor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entrada)) return false;
            Entrada<?, ?> outra = (Entrada<?, ?>) o;
            return Objects.equals(chave, outra.chave);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(chave);
        }

        @Override
        public String toString() {
            return chave + "=" + valor;
        }
    }

    // ---------------------------------------------------------------
    // API publica
    // ---------------------------------------------------------------

    /** Insere ou atualiza um par chave/valor. Custo medio O(1). */
    public void inserir(K chave, V valor) {
        if (((double) (quantidadeElementos + 1)) / baldes.length > FATOR_CARGA_MAXIMO) {
            redimensionar();
        }
        int indice = indiceParaChave(chave);
        ListaSimplesmenteEncadeada<Entrada<K, V>> balde = baldes[indice];
        if (balde == null) {
            balde = new ListaSimplesmenteEncadeada<>();
            baldes[indice] = balde;
        }
        for (Entrada<K, V> entrada : balde) {
            if (Objects.equals(entrada.chave, chave)) {
                entrada.valor = valor; // chave ja existe: atualiza valor
                Log.log("HASH", "inserir(" + chave + ") -> chave ja existia, valor atualizado (balde " + indice + ")");
                return;
            }
        }
        if (!balde.estaVazia()) {
            Log.log("HASH", "COLISAO detectada no balde " + indice + " ao inserir chave '" + chave + "' (encadeando)");
        }
        balde.inserirFim(new Entrada<>(chave, valor));
        quantidadeElementos++;
        Log.log("HASH", "inserir(" + chave + ") -> inserido no balde " + indice + " | total=" + quantidadeElementos);
    }

    /** Busca o valor associado a chave. Custo medio O(1). */
    public Optional<V> buscar(K chave) {
        int indice = indiceParaChave(chave);
        ListaSimplesmenteEncadeada<Entrada<K, V>> balde = baldes[indice];
        if (balde != null) {
            for (Entrada<K, V> entrada : balde) {
                if (Objects.equals(entrada.chave, chave)) {
                    Log.log("HASH", "buscar(" + chave + ") -> encontrado no balde " + indice);
                    return Optional.of(entrada.valor);
                }
            }
        }
        Log.log("HASH", "buscar(" + chave + ") -> nao encontrado (balde " + indice + ")");
        return Optional.empty();
    }

    public boolean contem(K chave) {
        return buscar(chave).isPresent();
    }

    /** Remove a entrada associada a chave. Custo medio O(1). */
    public boolean remover(K chave) {
        int indice = indiceParaChave(chave);
        ListaSimplesmenteEncadeada<Entrada<K, V>> balde = baldes[indice];
        if (balde == null) {
            return false;
        }
        Entrada<K, V> alvo = null;
        for (Entrada<K, V> entrada : balde) {
            if (Objects.equals(entrada.chave, chave)) {
                alvo = entrada;
                break;
            }
        }
        if (alvo == null) {
            return false;
        }
        balde.remover(alvo);
        quantidadeElementos--;
        Log.log("HASH", "remover(" + chave + ") -> removido do balde " + indice + " | total=" + quantidadeElementos);
        return true;
    }

    public boolean estaVazia() {
        return quantidadeElementos == 0;
    }

    public int tamanho() {
        return quantidadeElementos;
    }

    public double fatorDeCarga() {
        return ((double) quantidadeElementos) / baldes.length;
    }

    public List<V> valores() {
        List<V> resultado = new ArrayList<>();
        for (ListaSimplesmenteEncadeada<Entrada<K, V>> balde : baldes) {
            if (balde != null) {
                for (Entrada<K, V> entrada : balde) {
                    resultado.add(entrada.valor);
                }
            }
        }
        return resultado;
    }

    // ---------------------------------------------------------------
    // Funcao hash e redimensionamento
    // ---------------------------------------------------------------

    private int indiceParaChave(K chave) {
        int hash = Objects.hashCode(chave);
        // desloca os bits para reduzir colisoes de hashCodes com padroes proximos
        hash ^= (hash >>> 16);
        return Math.abs(hash) % baldes.length;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        Log.log("HASH", "fator de carga excedido (" + String.format("%.2f", fatorDeCarga())
                + ") -> redimensionando tabela de " + baldes.length + " para " + (baldes.length * 2) + " baldes");
        ListaSimplesmenteEncadeada<Entrada<K, V>>[] antigos = baldes;
        baldes = new ListaSimplesmenteEncadeada[antigos.length * 2];
        quantidadeElementos = 0;
        for (ListaSimplesmenteEncadeada<Entrada<K, V>> balde : antigos) {
            if (balde != null) {
                for (Entrada<K, V> entrada : balde) {
                    inserir(entrada.chave, entrada.valor);
                }
            }
        }
    }
}
