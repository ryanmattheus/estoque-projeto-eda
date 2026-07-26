package com.estoque.servico;

import com.estoque.modelo.PedidoCompra;
import com.estoque.modelo.Produto;
import com.estoque.util.Log;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SistemaEstoqueTest {

    private SistemaEstoque sistema;

    @BeforeAll
    static void desativarLogsDurranteOsTestes() {
        Log.desativar();
    }

    @AfterAll
    static void reativarLogs() {
        Log.ativar();
    }

    @BeforeEach
    void setUp() {
        sistema = new SistemaEstoque();
    }

    @Test
    void cadastrarProdutoDeveIndexarNasTresEstruturas() {
        sistema.cadastrarProduto(1, "Parafuso", 100, 20, 0.5, "Fornecedor A");

        assertTrue(sistema.buscarPorCodigo(1).isPresent());
        assertTrue(sistema.buscarPorNome("Parafuso").isPresent());
        assertEquals(1, sistema.totalDeProdutos());
    }

    @Test
    void buscarPorNomeDeveSerCaseInsensitive() {
        sistema.cadastrarProduto(1, "Chapa de Aco", 50, 10, 20.0, "Fornecedor B");
        assertTrue(sistema.buscarPorNome("chapa de aco").isPresent());
        assertTrue(sistema.buscarPorNome("CHAPA DE ACO").isPresent());
    }

    @Test
    void cadastrarProdutoComCodigoDuplicadoDeveLancarExcecao() {
        sistema.cadastrarProduto(1, "A", 10, 5, 1.0, "F");
        assertThrows(IllegalArgumentException.class,
                () -> sistema.cadastrarProduto(1, "B", 20, 5, 2.0, "F"));
    }

    @Test
    void cadastrarProdutoAbaixoDoMinimoDeveGerarPedidoAutomatico() {
        sistema.cadastrarProduto(1, "Rolamento", 2, 10, 5.0, "Fornecedor C");
        assertTrue(sistema.existePedidoPendente());
        assertEquals(1, sistema.quantidadePedidosPendentes());
    }

    @Test
    void registrarVendaQueDerrubaEstoqueAbaixoDoMinimoDeveGerarReposicaoAutomatica() {
        sistema.cadastrarProduto(1, "Luva", 30, 10, 6.9, "Fornecedor D");
        assertFalse(sistema.existePedidoPendente());

        sistema.registrarVenda(1, 25); // estoque cai para 5, abaixo do minimo (10)
        assertTrue(sistema.existePedidoPendente());
        assertEquals(5, sistema.buscarPorCodigo(1).orElseThrow().getQuantidadeEstoque());
    }

    @Test
    void registrarVendaComQuantidadeMaiorQueEstoqueDeveLancarExcecao() {
        sistema.cadastrarProduto(1, "Correia", 5, 2, 10.0, "F");
        assertThrows(IllegalArgumentException.class, () -> sistema.registrarVenda(1, 10));
    }

    @Test
    void processarProximoPedidoDeveSeguirOrdemFIFOEAumentarEstoque() {
        sistema.cadastrarProduto(1, "P1", 2, 10, 1.0, "F"); // gera pedido automatico #1
        sistema.cadastrarProduto(2, "P2", 1, 10, 1.0, "F"); // gera pedido automatico #2

        assertEquals(2, sistema.quantidadePedidosPendentes());

        PedidoCompra primeiro = sistema.processarProximoPedido();
        assertEquals(1, primeiro.getProduto().getCodigo()); // FIFO: o primeiro cadastrado sai primeiro

        PedidoCompra segundo = sistema.processarProximoPedido();
        assertEquals(2, segundo.getProduto().getCodigo());

        assertFalse(sistema.existePedidoPendente());
    }

    @Test
    void produtoMaisCriticoDeveSerOComMenorEstoque() {
        sistema.cadastrarProduto(1, "A", 50, 5, 1.0, "F");
        sistema.cadastrarProduto(2, "B", 5, 1, 1.0, "F");
        sistema.cadastrarProduto(3, "C", 20, 5, 1.0, "F");

        Produto maisCritico = sistema.produtoMaisCritico().orElseThrow();
        assertEquals(2, maisCritico.getCodigo());
    }

    @Test
    void listarProdutosMaisCriticosDeveRetornarOrdenadoPorEstoqueCrescente() {
        sistema.cadastrarProduto(1, "A", 50, 5, 1.0, "F");
        sistema.cadastrarProduto(2, "B", 5, 1, 1.0, "F");
        sistema.cadastrarProduto(3, "C", 20, 5, 1.0, "F");

        List<Produto> top2 = sistema.listarProdutosMaisCriticos(2);
        assertEquals(2, top2.size());
        assertEquals(2, top2.get(0).getCodigo());
        assertEquals(3, top2.get(1).getCodigo());
    }

    @Test
    void removerProdutoDeveRemoverDeTodasAsEstruturas() {
        sistema.cadastrarProduto(1, "Produto Unico", 10, 2, 5.0, "F");
        assertTrue(sistema.removerProduto(1));
        assertTrue(sistema.buscarPorCodigo(1).isEmpty());
        assertTrue(sistema.buscarPorNome("Produto Unico").isEmpty());
        assertEquals(0, sistema.totalDeProdutos());
    }

    @Test
    void removerProdutoInexistenteDeveRetornarFalse() {
        assertFalse(sistema.removerProduto(999));
    }

    @Test
    void listarCatalogoOrdenadoPorCodigoDeveRefletirOrdemDaAVL() {
        sistema.cadastrarProduto(30, "C", 10, 1, 1.0, "F");
        sistema.cadastrarProduto(10, "A", 10, 1, 1.0, "F");
        sistema.cadastrarProduto(20, "B", 10, 1, 1.0, "F");

        List<Produto> catalogo = sistema.listarCatalogoOrdenadoPorCodigo();
        assertEquals(List.of(10, 20, 30), catalogo.stream().map(Produto::getCodigo).toList());
    }

    @Test
    void solicitarPedidoManualParaProdutoInexistenteDeveLancarExcecao() {
        assertThrows(NoSuchElementException.class, () -> sistema.solicitarPedidoManual(999, 10));
    }

    @Test
    void naoDeveGerarPedidosAutomaticosDuplicadosEnquantoUmEstivaPendente() {
        sistema.cadastrarProduto(1, "Critico", 2, 10, 1.0, "F"); // gera 1 pedido automatico
        sistema.registrarEntrada(1, 1); // estoque=3, ainda abaixo do minimo, mas ja ha pedido pendente
        assertEquals(1, sistema.quantidadePedidosPendentes());
    }
}
