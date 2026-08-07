package com.estoque.servico;

import com.estoque.estruturas.avl.ArvoreAVL;
import com.estoque.estruturas.fila.Fila;
import com.estoque.estruturas.hash.TabelaHash;
import com.estoque.estruturas.heap.MinHeap;
import com.estoque.modelo.PedidoCompra;
import com.estoque.modelo.Produto;
import com.estoque.util.Log;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

/**
 * Sistema de Controle de Estoque com Reposicao Automatica.
 * Integra as quatro estruturas de dados: fila, heap, arvore AVL e tabela hash
 * cada uma responsavel por uma funcionalidade especifica
 */
public class SistemaEstoque {

    private final ArvoreAVL<Integer, Produto> catalogoPorCodigo;
    private final TabelaHash<String, Produto> indicePorNome;
    private final MinHeap<Produto> heapReposicao;
    private final Fila<PedidoCompra> filaPedidos;
    private final Set<Integer> codigosComReposicaoAutomaticaPendente;

    public SistemaEstoque() {
        this.catalogoPorCodigo = new ArvoreAVL<>();
        this.indicePorNome = new TabelaHash<>();
        this.heapReposicao = new MinHeap<>(Comparator.comparingInt(Produto::getQuantidadeEstoque));
        this.filaPedidos = new Fila<>();
        this.codigosComReposicaoAutomaticaPendente = new HashSet<>();
    }

    // Cadastro / consulta / remocao de produtos

    // Cadastra um novo produto nas tres estruturas de indexacao (AVL, Hash, Heap)
    public Produto cadastrarProduto(int codigo, String nome, int quantidadeEstoque,
            int estoqueMinimo, double preco, String fornecedor) {
        if (catalogoPorCodigo.contem(codigo)) {
            throw new IllegalArgumentException("Ja existe um produto com o codigo " + codigo);
        }
        Produto produto = new Produto(codigo, nome, quantidadeEstoque, estoqueMinimo, preco, fornecedor);

        catalogoPorCodigo.inserir(codigo, produto);
        indicePorNome.inserir(chaveNome(nome), produto);
        heapReposicao.inserir(produto);

        Log.log("SISTEMA", "Produto cadastrado: " + produto);

        if (produto.precisaReposicao()) {
            gerarPedidoReposicaoAutomatica(produto);
        }
        return produto;
    }

    // Busca um produto pelo codigo. Custo O(log n) via AVL
    public Optional<Produto> buscarPorCodigo(int codigo) {
        return catalogoPorCodigo.buscar(codigo);
    }

    // Busca um produto pelo nome. Custo O(1) medio via Tabela Hash
    public Optional<Produto> buscarPorNome(String nome) {
        return indicePorNome.buscar(chaveNome(nome));
    }

    // Remove um produto de todas as estruturas
    public boolean removerProduto(int codigo) {
        Optional<Produto> produtoOpt = buscarPorCodigo(codigo);
        if (produtoOpt.isEmpty()) {
            return false;
        }
        Produto produto = produtoOpt.get();
        catalogoPorCodigo.remover(codigo);
        indicePorNome.remover(chaveNome(produto.getNome()));
        heapReposicao.remover(produto);
        Log.log("SISTEMA", "Produto removido: " + produto);
        return true;
    }

    // Retorna o catalogo completo ordenado por codigo (percurso em ordem da AVL).
    public List<Produto> listarCatalogoOrdenadoPorCodigo() {
        return catalogoPorCodigo.emOrdem();
    }

    // Movimentacao de estoque

    // Registra uma venda de um produto, reduzindo o estoque e atualizando a
    // prioridade na heap
    public void registrarVenda(int codigo, int quantidadeVendida) {
        Produto produto = buscarPorCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado: codigo " + codigo));
        if (quantidadeVendida > produto.getQuantidadeEstoque()) {
            throw new IllegalArgumentException("Estoque insuficiente para a venda solicitada.");
        }
        atualizarEstoque(produto, produto.getQuantidadeEstoque() - quantidadeVendida);
    }

    // Registra entrada manual de estoque
    public void registrarEntrada(int codigo, int quantidadeRecebida) {
        Produto produto = buscarPorCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado: codigo " + codigo));
        atualizarEstoque(produto, produto.getQuantidadeEstoque() + quantidadeRecebida);
    }

    private void atualizarEstoque(Produto produto, int novaQuantidade) {
        int anterior = produto.getQuantidadeEstoque();
        produto.setQuantidadeEstoque(novaQuantidade);
        heapReposicao.atualizar(produto); // reflete a mudanca de prioridade na heap
        Log.log("SISTEMA", "Estoque de '" + produto.getNome() + "' alterado: " + anterior + " -> " + novaQuantidade);

        if (produto.precisaReposicao()) {
            gerarPedidoReposicaoAutomatica(produto);
        }
    }

    // Pedidos de compra (Fila)

    // Gera automaticamente um pedido de reposicao e o coloca na fila (FIFO)
    private void gerarPedidoReposicaoAutomatica(Produto produto) {
        if (codigosComReposicaoAutomaticaPendente.contains(produto.getCodigo())) {
            Log.log("SISTEMA",
                    "Reposicao automatica ja pendente para '" + produto.getNome() + "' -> ignorando duplicata");
            return;
        }
        int quantidadeSugerida = Math.max(produto.getEstoqueMinimo() * 2 - produto.getQuantidadeEstoque(),
                produto.getEstoqueMinimo());
        PedidoCompra pedido = new PedidoCompra(produto, quantidadeSugerida, true);
        filaPedidos.enfileirar(pedido);
        codigosComReposicaoAutomaticaPendente.add(produto.getCodigo());
        Log.log("SISTEMA", "Reposicao automatica disparada -> " + pedido);
    }

    // Permite tambem solicitar manualmente um pedido de compra
    public PedidoCompra solicitarPedidoManual(int codigo, int quantidade) {
        Produto produto = buscarPorCodigo(codigo)
                .orElseThrow(() -> new NoSuchElementException("Produto nao encontrado: codigo " + codigo));
        PedidoCompra pedido = new PedidoCompra(produto, quantidade, false);
        filaPedidos.enfileirar(pedido);
        Log.log("SISTEMA", "Pedido manual criado -> " + pedido);
        return pedido;
    }

    // Processa o proximo pedido da fila (o mais antigo), dando entrada no estoque
    // do produto correspondente. Retorna o pedido processado
    public PedidoCompra processarProximoPedido() {
        PedidoCompra pedido = filaPedidos.desenfileirar();
        Produto produto = pedido.getProduto();
        int novaQuantidade = produto.getQuantidadeEstoque() + pedido.getQuantidadeSolicitada();
        produto.setQuantidadeEstoque(novaQuantidade);
        heapReposicao.atualizar(produto);
        pedido.marcarProcessado();
        if (pedido.isAutomatico()) {
            codigosComReposicaoAutomaticaPendente.remove(produto.getCodigo());
        }
        Log.log("SISTEMA", "Pedido processado -> " + pedido + " | novo estoque=" + novaQuantidade);
        return pedido;
    }

    public boolean existePedidoPendente() {
        return !filaPedidos.estaVazia();
    }

    public int quantidadePedidosPendentes() {
        return filaPedidos.tamanho();
    }

    // Relatorios baseados na Heap

    // Produto com a prioridade mais alta de reposicao (menor estoque), sem remover
    // da heap
    public Optional<Produto> produtoMaisCritico() {
        if (heapReposicao.estaVazia()) {
            return Optional.empty();
        }
        return Optional.of(heapReposicao.espiarMinimo());
    }

    // Lista os k produtos mais criticos (menor estoque), sem alterar a heap
    public List<Produto> listarProdutosMaisCriticos(int k) {
        return heapReposicao.obterMenores(k);
    }

    // Retorna todos os produtos ordenados por estoque crescente, via heapsort
    public List<Produto> ordenarProdutosPorEstoque() {
        return heapReposicao.heapsort();
    }

    public int totalDeProdutos() {
        return catalogoPorCodigo.tamanho();
    }

    private String chaveNome(String nome) {
        return nome.trim().toLowerCase();
    }
}
