package com.estoque.estruturas.fila;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class FilaTest {

    private Fila<Integer> fila;

    @BeforeEach
    void setUp() {
        fila = new Fila<>();
    }

    @Test
    void filaNovaDeveEstarVazia() {
        assertTrue(fila.estaVazia());
        assertEquals(0, fila.tamanho());
    }

    @Test
    void enfileirarEDesenfileirarDevemSeguirOrdemFIFO() {
        fila.enfileirar(1);
        fila.enfileirar(2);
        fila.enfileirar(3);

        assertEquals(1, fila.desenfileirar());
        assertEquals(2, fila.desenfileirar());
        assertEquals(3, fila.desenfileirar());
        assertTrue(fila.estaVazia());
    }

    @Test
    void espiarDeveRetornarPrimeiroSemRemover() {
        fila.enfileirar(10);
        fila.enfileirar(20);
        assertEquals(10, fila.espiar());
        assertEquals(2, fila.tamanho());
    }

    @Test
    void intercalarEnfileirarEDesenfileirarDeveManterOrdemCorreta() {
        fila.enfileirar(1);
        fila.enfileirar(2);
        assertEquals(1, fila.desenfileirar());
        fila.enfileirar(3);
        fila.enfileirar(4);
        assertEquals(2, fila.desenfileirar());
        assertEquals(3, fila.desenfileirar());
        assertEquals(4, fila.desenfileirar());
        assertTrue(fila.estaVazia());
    }

    @Test
    void desenfileirarVaziaDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> fila.desenfileirar());
    }

    @Test
    void tamanhoDeveConsiderarAmbasAsPilhasInternas() {
        fila.enfileirar(1);
        fila.enfileirar(2);
        fila.desenfileirar(); // move tudo para pilhaSaida, depois remove o "1"
        fila.enfileirar(3);   // vai para pilhaEntrada novamente
        assertEquals(2, fila.tamanho()); // restam [2] na saida e [3] na entrada
    }
}
