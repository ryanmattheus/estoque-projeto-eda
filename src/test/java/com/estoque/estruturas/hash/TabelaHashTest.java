package com.estoque.estruturas.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabelaHashTest {

    private TabelaHash<String, Integer> tabela;

    @BeforeEach
    void setUp() {
        tabela = new TabelaHash<>();
    }

    @Test
    void tabelaNovaDeveEstarVazia() {
        assertTrue(tabela.estaVazia());
        assertEquals(0, tabela.tamanho());
    }

    @Test
    void inserirEBuscarDevemFuncionarCorretamente() {
        tabela.inserir("parafuso", 101);
        tabela.inserir("chapa", 102);

        assertEquals(101, tabela.buscar("parafuso").orElseThrow());
        assertEquals(102, tabela.buscar("chapa").orElseThrow());
        assertTrue(tabela.buscar("inexistente").isEmpty());
    }

    @Test
    void inserirChaveExistenteDeveAtualizarValor() {
        tabela.inserir("x", 1);
        tabela.inserir("x", 2);
        assertEquals(1, tabela.tamanho());
        assertEquals(2, tabela.buscar("x").orElseThrow());
    }

    @Test
    void removerDeveEliminarAEntrada() {
        tabela.inserir("a", 1);
        tabela.inserir("b", 2);
        assertTrue(tabela.remover("a"));
        assertTrue(tabela.buscar("a").isEmpty());
        assertEquals(1, tabela.tamanho());
    }

    @Test
    void removerChaveInexistenteDeveRetornarFalse() {
        assertFalse(tabela.remover("naoexiste"));
    }

    @Test
    void deveSuportarMuitasChavesComRedimensionamentoAutomatico() {
        for (int i = 0; i < 100; i++) {
            tabela.inserir("chave" + i, i);
        }
        assertEquals(100, tabela.tamanho());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, tabela.buscar("chave" + i).orElseThrow());
        }
        assertTrue(tabela.fatorDeCarga() <= 0.75,
                "Fator de carga deve se manter controlado apos redimensionamentos");
    }

    @Test
    void valoresDeveRetornarTodosOsElementosInseridos() {
        tabela.inserir("a", 1);
        tabela.inserir("b", 2);
        tabela.inserir("c", 3);
        List<Integer> valores = tabela.valores();
        assertEquals(3, valores.size());
        assertTrue(valores.containsAll(List.of(1, 2, 3)));
    }
}
