package com.estoque.estruturas.heap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    private MinHeap<Integer> heap;

    @BeforeEach
    void setUp() {
        heap = new MinHeap<Integer>(Comparator.naturalOrder());
    }

    @Test
    void heapNovaDeveEstarVazia() {
        assertTrue(heap.estaVazia());
        assertEquals(0, heap.tamanho());
    }

    @Test
    void inserirEExtrairMinimoDevemRetornarOrdemCrescente() {
        int[] valores = {5, 3, 8, 1, 9, 2};
        for (int v : valores) {
            heap.inserir(v);
        }
        int anterior = Integer.MIN_VALUE;
        while (!heap.estaVazia()) {
            int atual = heap.extrairMinimo();
            assertTrue(atual >= anterior, "A extracao deve ocorrer em ordem crescente");
            anterior = atual;
        }
    }

    @Test
    void espiarMinimoDeveRetornarMenorSemRemover() {
        heap.inserir(10);
        heap.inserir(4);
        heap.inserir(7);
        assertEquals(4, heap.espiarMinimo());
        assertEquals(3, heap.tamanho());
    }

    @Test
    void extrairMinimoEmHeapVaziaDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> heap.extrairMinimo());
    }

    @Test
    void heapsortDeveRetornarListaOrdenadaSemAlterarHeapOriginal() {
        int[] valores = {9, 1, 5, 3, 7};
        for (int v : valores) {
            heap.inserir(v);
        }
        List<Integer> ordenado = heap.heapsort();
        assertEquals(List.of(1, 3, 5, 7, 9), ordenado);
        assertEquals(5, heap.tamanho()); // heap original preservada
    }

    @Test
    void obterMenoresDeveRetornarTopKSemAlterarHeap() {
        int[] valores = {9, 1, 5, 3, 7};
        for (int v : valores) {
            heap.inserir(v);
        }
        List<Integer> top3 = heap.obterMenores(3);
        assertEquals(List.of(1, 3, 5), top3);
        assertEquals(5, heap.tamanho());
    }

    @Test
    void atualizarDeveReordenarQuandoPrioridadeMuda() {
        // Simula objetos mutaveis via wrapper simples com equals por identidade
        MinHeap<int[]> heapArrays = new MinHeap<>(Comparator.comparingInt(a -> a[0]));
        int[] itemA = {10};
        int[] itemB = {20};
        int[] itemC = {30};
        heapArrays.inserir(itemA);
        heapArrays.inserir(itemB);
        heapArrays.inserir(itemC);

        assertSame(itemA, heapArrays.espiarMinimo());

        itemC[0] = 1; // agora itemC deveria ser o minimo
        heapArrays.atualizar(itemC);

        assertSame(itemC, heapArrays.espiarMinimo());
    }

    @Test
    void removerItemEspecificoDeveFuncionar() {
        heap.inserir(5);
        heap.inserir(3);
        heap.inserir(8);
        heap.inserir(1);

        assertTrue(heap.remover(8));
        assertEquals(3, heap.tamanho());

        List<Integer> restante = heap.heapsort();
        assertEquals(List.of(1, 3, 5), restante);
    }

    @Test
    void removerItemInexistenteDeveRetornarFalse() {
        heap.inserir(1);
        assertFalse(heap.remover(999));
    }
}
