package com.estoque.estruturas.pilha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PilhaTest {

    private Pilha<String> pilha;

    @BeforeEach
    void setUp() {
        pilha = new Pilha<>();
    }

    @Test
    void pilhaNovaDeveEstarVazia() {
        assertTrue(pilha.estaVazia());
        assertEquals(0, pilha.tamanho());
    }

    @Test
    void empilharEDesempilharDevemSeguirOrdemLIFO() {
        pilha.empilhar("A");
        pilha.empilhar("B");
        pilha.empilhar("C");

        assertEquals("C", pilha.desempilhar());
        assertEquals("B", pilha.desempilhar());
        assertEquals("A", pilha.desempilhar());
        assertTrue(pilha.estaVazia());
    }

    @Test
    void topoDeveRetornarSemRemover() {
        pilha.empilhar(1 + "");
        pilha.empilhar("2");
        assertEquals("2", pilha.topo());
        assertEquals(2, pilha.tamanho());
    }

    @Test
    void desempilharVaziaDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> pilha.desempilhar());
    }

    @Test
    void topoVaziaDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> pilha.topo());
    }
}
