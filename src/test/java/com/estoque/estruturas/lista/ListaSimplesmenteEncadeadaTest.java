package com.estoque.estruturas.lista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ListaSimplesmenteEncadeadaTest {

    private ListaSimplesmenteEncadeada<Integer> lista;

    @BeforeEach
    void setUp() {
        lista = new ListaSimplesmenteEncadeada<>();
    }

    @Test
    void listaNovaDeveEstarVazia() {
        assertTrue(lista.estaVazia());
        assertEquals(0, lista.tamanho());
    }

    @Test
    void inserirInicioDeveColocarElementoNaFrente() {
        lista.inserirInicio(1);
        lista.inserirInicio(2);
        lista.inserirInicio(3);
        assertEquals(3, lista.verInicio());
        assertEquals(3, lista.tamanho());
    }

    @Test
    void inserirFimDeveManterOrdemDeInsercao() {
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.inserirFim(3);
        assertEquals(1, lista.verInicio());
        assertEquals(3, lista.tamanho());
        assertEquals("[1 -> 2 -> 3]", lista.toString());
    }

    @Test
    void removerInicioDeveRetornarERemoverPrimeiroElemento() {
        lista.inserirFim(10);
        lista.inserirFim(20);
        assertEquals(10, lista.removerInicio());
        assertEquals(20, lista.verInicio());
        assertEquals(1, lista.tamanho());
    }

    @Test
    void removerInicioEmListaVaziaDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> lista.removerInicio());
    }

    @Test
    void removerValorEspecificoDeveFuncionarNoMeioENasPontas() {
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.inserirFim(3);
        lista.inserirFim(4);

        assertTrue(lista.remover(3)); // meio
        assertEquals("[1 -> 2 -> 4]", lista.toString());

        assertTrue(lista.remover(1)); // inicio
        assertEquals("[2 -> 4]", lista.toString());

        assertTrue(lista.remover(4)); // fim
        assertEquals("[2]", lista.toString());

        assertFalse(lista.remover(99)); // inexistente
    }

    @Test
    void contemDeveIndicarPresencaCorretamente() {
        lista.inserirFim(5);
        lista.inserirFim(7);
        assertTrue(lista.contem(5));
        assertTrue(lista.contem(7));
        assertFalse(lista.contem(100));
    }

    @Test
    void iteratorDevePercorrerTodosOsElementosNaOrdemCorreta() {
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.inserirFim(3);

        int soma = 0;
        for (int valor : lista) {
            soma += valor;
        }
        assertEquals(6, soma);
    }

    @Test
    void removerTodosElementosDeveEsvaziarAListaCorretamente() {
        lista.inserirFim(1);
        lista.inserirFim(2);
        lista.removerInicio();
        lista.removerInicio();
        assertTrue(lista.estaVazia());
        // insercao apos esvaziar deve funcionar (fim/inicio resetados corretamente)
        lista.inserirFim(99);
        assertEquals(99, lista.verInicio());
        assertEquals(1, lista.tamanho());
    }
}
