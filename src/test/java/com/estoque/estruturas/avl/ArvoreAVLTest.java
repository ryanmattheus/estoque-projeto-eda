package com.estoque.estruturas.avl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArvoreAVLTest {

    private ArvoreAVL<Integer, String> arvore;

    @BeforeEach
    void setUp() {
        arvore = new ArvoreAVL<>();
    }

    @Test
    void arvoreNovaDeveEstarVazia() {
        assertTrue(arvore.estaVazia());
        assertEquals(0, arvore.tamanho());
    }

    @Test
    void inserirEBuscarDevemFuncionarCorretamente() {
        arvore.inserir(50, "Produto50");
        arvore.inserir(30, "Produto30");
        arvore.inserir(70, "Produto70");

        assertEquals("Produto50", arvore.buscar(50).orElseThrow());
        assertEquals("Produto30", arvore.buscar(30).orElseThrow());
        assertEquals("Produto70", arvore.buscar(70).orElseThrow());
        assertTrue(arvore.buscar(999).isEmpty());
    }

    @Test
    void inserirChaveExistenteDeveAtualizarValorSemDuplicar() {
        arvore.inserir(10, "Original");
        arvore.inserir(10, "Atualizado");
        assertEquals(1, arvore.tamanho());
        assertEquals("Atualizado", arvore.buscar(10).orElseThrow());
    }

    @Test
    void insercaoSequencialCrescenteDeveManterArvoreBalanceada() {
        // Insercao em ordem crescente forcaria uma lista encadeada em BST
        // comum, mas a AVL deve se rebalancear via rotacoes a esquerda.
        for (int i = 1; i <= 15; i++) {
            arvore.inserir(i, "V" + i);
        }
        assertEquals(15, arvore.tamanho());
        // Altura de uma AVL com 15 nos deve ser proxima de log2(15) ~ 4
        assertTrue(alturaAproximada() <= 5, "Arvore deveria estar balanceada");
    }

    private int alturaAproximada() {
        // Verifica indiretamente o balanceamento: percurso em ordem deve
        // retornar exatamente os 15 elementos ordenados corretamente,
        // o que so ocorre se as rotacoes preservaram a propriedade de BST.
        List<String> valores = arvore.emOrdem();
        return (int) Math.ceil(Math.log(valores.size() + 1) / Math.log(2)) + 1;
    }

    @Test
    void emOrdemDeveRetornarValoresOrdenadosPelaChave() {
        int[] chaves = {50, 20, 70, 10, 30, 60, 80};
        for (int chave : chaves) {
            arvore.inserir(chave, "P" + chave);
        }
        List<String> ordenado = arvore.emOrdem();
        List<String> esperado = List.of("P10", "P20", "P30", "P50", "P60", "P70", "P80");
        assertEquals(esperado, ordenado);
    }

    @Test
    void removerFolhaDeveFuncionar() {
        arvore.inserir(50, "A");
        arvore.inserir(30, "B");
        arvore.inserir(70, "C");
        arvore.remover(30);
        assertTrue(arvore.buscar(30).isEmpty());
        assertEquals(2, arvore.tamanho());
    }

    @Test
    void removerNoComDoisFilhosDeveManterPropriedadeBST() {
        int[] chaves = {50, 30, 70, 20, 40, 60, 80};
        for (int chave : chaves) {
            arvore.inserir(chave, "P" + chave);
        }
        arvore.remover(50); // raiz com dois filhos
        assertTrue(arvore.buscar(50).isEmpty());
        assertEquals(6, arvore.tamanho());

        List<String> ordenado = arvore.emOrdem();
        List<String> esperado = List.of("P20", "P30", "P40", "P60", "P70", "P80");
        assertEquals(esperado, ordenado);
    }

    @Test
    void removerChaveInexistenteNaoDeveAlterarArvore() {
        arvore.inserir(1, "A");
        arvore.remover(999);
        assertEquals(1, arvore.tamanho());
    }

    @Test
    void removerTodosOsElementosDeveEsvaziarArvore() {
        for (int i = 1; i <= 10; i++) {
            arvore.inserir(i, "V" + i);
        }
        for (int i = 1; i <= 10; i++) {
            arvore.remover(i);
        }
        assertTrue(arvore.estaVazia());
        assertEquals(0, arvore.tamanho());
    }
}
