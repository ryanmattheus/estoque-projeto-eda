package com.estoque.estruturas.heap;

import com.estoque.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Heap Binaria (Min-Heap) generica, implementada com array (ArrayList).
 *
 * Usada no sistema para manter os produtos priorizados pelo NIVEL DE
 * ESTOQUE: produtos com estoque mais baixo "sobem" (ficam mais proximos
 * da raiz), pois um Comparator adequado e usado (menor quantidade =
 * maior prioridade de reposicao).
 *
 * Alem das operacoes classicas de heap (inserir / extrairMinimo / espiar),
 * esta implementacao mantem um mapa auxiliar (item -> indice no array)
 * que permite a operacao atualizar(item), usada quando o estoque de um
 * produto muda e sua prioridade na heap precisa ser recalculada
 * (sift-up ou sift-down, conforme o caso), sem precisar reconstruir a
 * heap inteira.
 *
 * Complexidade:
 *  - inserir........... O(log n)
 *  - extrairMinimo...... O(log n)
 *  - espiarMinimo....... O(1)
 *  - atualizar.......... O(log n)
 *  - remover(item)...... O(log n)
 */
public class MinHeap<T> {

    private final List<T> array;
    private final Comparator<T> comparador;
    private final Map<T, Integer> indices; // item -> posicao no array (requer equals/hashCode consistentes)

    public MinHeap(Comparator<T> comparador) {
        this.array = new ArrayList<>();
        this.comparador = comparador;
        this.indices = new HashMap<>();
    }

    // ---------------------------------------------------------------
    // API publica
    // ---------------------------------------------------------------

    public void inserir(T item) {
        array.add(item);
        int i = array.size() - 1;
        indices.put(item, i);
        Log.log("HEAP", "inserir(" + item + ") na posicao " + i + " -> sift-up");
        subir(i);
    }

    /** Remove e retorna o elemento de maior prioridade (menor valor). */
    public T extrairMinimo() {
        if (estaVazia()) {
            throw new NoSuchElementException("Heap vazia: nao ha elemento para extrair.");
        }
        T minimo = array.get(0);
        T ultimo = array.remove(array.size() - 1);
        indices.remove(minimo);
        if (!array.isEmpty()) {
            array.set(0, ultimo);
            indices.put(ultimo, 0);
            Log.log("HEAP", "extrairMinimo() -> " + minimo + " | novo topo provisorio=" + ultimo + " -> sift-down");
            descer(0);
        } else {
            Log.log("HEAP", "extrairMinimo() -> " + minimo + " | heap ficou vazia");
        }
        return minimo;
    }

    public T espiarMinimo() {
        if (estaVazia()) {
            throw new NoSuchElementException("Heap vazia: nao ha elemento no topo.");
        }
        return array.get(0);
    }

    /**
     * Remove um item especifico da heap (nao precisa ser o minimo).
     * Util quando um produto e removido do estoque.
     */
    public boolean remover(T item) {
        Integer i = indices.get(item);
        if (i == null) {
            return false;
        }
        int ultimoIndice = array.size() - 1;
        T ultimo = array.remove(ultimoIndice);
        indices.remove(item);
        if (i != ultimoIndice) {
            array.set(i, ultimo);
            indices.put(ultimo, i);
            // pode precisar subir ou descer dependendo da comparacao;
            // reconsulta o indice entre as duas chamadas, pois subir()
            // pode ter movido o elemento para outra posicao
            subir(i);
            descer(indices.get(ultimo));
        }
        Log.log("HEAP", "remover(" + item + ") -> removido da heap");
        return true;
    }

    /**
     * Reposiciona um item cuja "chave de prioridade" mudou externamente
     * (ex.: quantidade em estoque foi alterada). Deve ser chamado logo
     * apos a mutacao do campo usado no Comparator.
     */
    public void atualizar(T item) {
        Integer i = indices.get(item);
        if (i == null) {
            Log.log("HEAP", "atualizar(" + item + ") -> item nao esta na heap");
            return;
        }
        Log.log("HEAP", "atualizar(" + item + ") -> reordenando (sift-up/sift-down)");
        subir(i);
        descer(indices.get(item));
    }

    public boolean estaVazia() {
        return array.isEmpty();
    }

    public int tamanho() {
        return array.size();
    }

    /**
     * Retorna, sem alterar a heap, os k elementos de maior prioridade
     * (menor valor) ordenados. Custo O(n log n) - usa uma copia.
     */
    public List<T> obterMenores(int k) {
        List<T> copia = new ArrayList<>(array);
        copia.sort(comparador);
        int limite = Math.min(k, copia.size());
        return copia.subList(0, limite);
    }

    /** Heapsort: esvazia uma copia da heap e retorna os elementos em ordem crescente de prioridade. */
    public List<T> heapsort() {
        MinHeap<T> copia = new MinHeap<>(comparador);
        for (T item : array) {
            copia.inserir(item);
        }
        List<T> resultado = new ArrayList<>();
        while (!copia.estaVazia()) {
            resultado.add(copia.extrairMinimo());
        }
        return resultado;
    }

    // ---------------------------------------------------------------
    // Operacoes internas de heap
    // ---------------------------------------------------------------

    private void subir(int i) {
        while (i > 0) {
            int pai = (i - 1) / 2;
            if (comparador.compare(array.get(i), array.get(pai)) < 0) {
                trocar(i, pai);
                i = pai;
            } else {
                break;
            }
        }
    }

    private void descer(int i) {
        int n = array.size();
        while (true) {
            int esquerda = 2 * i + 1;
            int direita = 2 * i + 2;
            int menor = i;

            if (esquerda < n && comparador.compare(array.get(esquerda), array.get(menor)) < 0) {
                menor = esquerda;
            }
            if (direita < n && comparador.compare(array.get(direita), array.get(menor)) < 0) {
                menor = direita;
            }
            if (menor == i) {
                break;
            }
            trocar(i, menor);
            i = menor;
        }
    }

    private void trocar(int i, int j) {
        T temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp);
        indices.put(array.get(i), i);
        indices.put(array.get(j), j);
    }
}
