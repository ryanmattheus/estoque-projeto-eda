package com.estoque.app;

import com.estoque.modelo.PedidoCompra;
import com.estoque.modelo.Produto;
import com.estoque.servico.SistemaEstoque;
import com.estoque.util.Log;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Aplicacao de console que demonstra o Sistema de Controle de Estoque com
 * Reposicao Automatica, integrando: Fila (2 pilhas), Heap (min-heap),
 * Arvore AVL e Tabela Hash (chaining).
 */
public class Main {

    private static final Scanner leitor = new Scanner(System.in);
    private static final SistemaEstoque sistema = new SistemaEstoque();

    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));
        System.out.println("=================================================================");
        System.out.println(" SISTEMA DE CONTROLE DE ESTOQUE COM REPOSICAO AUTOMATICA");
        System.out.println(" EDA/LEDA 2026.1 - Fila(2 Pilhas) | Heap | Arvore AVL | Tabela Hash");
        System.out.println("=================================================================");

        popularDadosDemonstracao();

        boolean sair = false;
        while (!sair) {
            exibirMenu();
            String opcao = leitor.nextLine().trim();
            try {
                switch (opcao) {
                    case "1" -> cadastrarProduto();
                    case "2" -> buscarPorCodigo();
                    case "3" -> buscarPorNome();
                    case "4" -> listarCatalogoOrdenado();
                    case "5" -> registrarVenda();
                    case "6" -> registrarEntrada();
                    case "7" -> listarProdutosCriticos();
                    case "8" -> processarProximoPedido();
                    case "9" -> processarTodosOsPedidos();
                    case "10" -> removerProduto();
                    case "11" -> ordenarPorEstoqueHeapsort();
                    case "12" -> alternarLogs();
                    case "0" -> sair = true;
                    default -> System.out.println(">> Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println(">> Erro: " + e.getMessage());
            }
        }
        System.out.println("Encerrando o sistema. Ate logo!");
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("--------------------- MENU ---------------------");
        System.out.println(" 1  - Cadastrar produto");
        System.out.println(" 2  - Buscar produto por codigo");
        System.out.println(" 3  - Buscar produto por nome");
        System.out.println(" 4  - Listar catalogo ordenado por codigo"); //avl em ordem
        System.out.println(" 5  - Registrar venda: ");//saida de estoque
        System.out.println(" 6  - Registrar entrada manual de estoque");
        System.out.println(" 7  - Listar produtos mais criticos ");//Heap
        System.out.println(" 8  - Processar proximo pedido da fila: "); //Fila = 2 Pilhas
        System.out.println(" 9  - Processar TODOS os pedidos pendentes");
        System.out.println(" 10 - Remover produto");
        System.out.println(" 11 - Ordenar todos os produtos por estoque (heapsort)");
        System.out.println(" 12 - Ligar/desligar logs das estruturas");
        System.out.println(" 0  - Sair");
        System.out.println("--------------------------------------------------");
        System.out.print("Escolha uma opcao: ");
    }

    private static void popularDadosDemonstracao() {
        System.out.println("\n>> Carregando catalogo inicial de demonstracao...\n");
        sistema.cadastrarProduto(101, "Parafuso Sextavado M8", 500, 100, 0.35, "Fornecedor A");
        sistema.cadastrarProduto(102, "Chapa de Aco 2mm", 40, 20, 89.90, "Fornecedor B");
        sistema.cadastrarProduto(103, "Tinta Industrial 18L", 15, 10, 210.00, "Fornecedor C");
        sistema.cadastrarProduto(104, "Rolamento 6202", 8, 15, 12.50, "Fornecedor A"); // ja abaixo do minimo
        sistema.cadastrarProduto(105, "Correia Dentada", 60, 25, 45.00, "Fornecedor D");
        sistema.cadastrarProduto(106, "Luva de Protecao", 3, 30, 6.90, "Fornecedor B"); // criticamente baixo
        System.out.println("\n>> Catalogo inicial carregado (" + sistema.totalDeProdutos() + " produtos).");
        System.out.println(">> Pedidos automaticos pendentes gerados: " + sistema.quantidadePedidosPendentes());
    }

    private static void cadastrarProduto() {
        System.out.print("Codigo (numero inteiro unico): ");
        int codigo = Integer.parseInt(leitor.nextLine().trim());
        System.out.print("Nome: ");
        String nome = leitor.nextLine().trim();
        System.out.print("Quantidade em estoque: ");
        int quantidade = Integer.parseInt(leitor.nextLine().trim());
        System.out.print("Estoque minimo: ");
        int minimo = Integer.parseInt(leitor.nextLine().trim());
        System.out.print("Preco unitario: ");
        double preco = Double.parseDouble(leitor.nextLine().trim().replace(",", "."));
        System.out.print("Fornecedor: ");
        String fornecedor = leitor.nextLine().trim();

        Produto produto = sistema.cadastrarProduto(codigo, nome, quantidade, minimo, preco, fornecedor);
        System.out.println(">> Produto cadastrado: " + produto);
    }

    private static void buscarPorCodigo() {
        System.out.print("Codigo do produto: ");
        int codigo = Integer.parseInt(leitor.nextLine().trim());
        sistema.buscarPorCodigo(codigo)
                .ifPresentOrElse(
                        p -> System.out.println(">> Encontrado: " + p),
                        () -> System.out.println(">> Produto nao encontrado."));
    }

    private static void buscarPorNome() {
        System.out.print("Nome (ou parte do nome exata cadastrada): ");
        String nome = leitor.nextLine().trim();
        sistema.buscarPorNome(nome)
                .ifPresentOrElse(
                        p -> System.out.println(">> Encontrado: " + p),
                        () -> System.out.println(">> Produto nao encontrado."));
    }

    private static void listarCatalogoOrdenado() {
        List<Produto> catalogo = sistema.listarCatalogoOrdenadoPorCodigo();
        System.out.println(">> Catalogo (ordenado por codigo via percurso em-ordem da AVL):");
        catalogo.forEach(p -> System.out.println("   " + p));
    }

    private static void registrarVenda() {
        System.out.print("Codigo do produto vendido: ");
        int codigo = Integer.parseInt(leitor.nextLine().trim());
        System.out.print("Quantidade vendida: ");
        int quantidade = Integer.parseInt(leitor.nextLine().trim());
        sistema.registrarVenda(codigo, quantidade);
        System.out.println(">> Venda registrada.");
    }

    private static void registrarEntrada() {
        System.out.print("Codigo do produto: ");
        int codigo = Integer.parseInt(leitor.nextLine().trim());
        System.out.print("Quantidade recebida: ");
        int quantidade = Integer.parseInt(leitor.nextLine().trim());
        sistema.registrarEntrada(codigo, quantidade);
        System.out.println(">> Entrada registrada.");
    }

    private static void listarProdutosCriticos() { // forma de mostrar apenas os criticos(os que estao abaixo do minimo:
           List<Produto> criticos = sistema.listarProdutosMaisCriticos(sistema.totalDeProdutos())
            .stream()
            .filter(Produto::precisaReposicao)
            .toList();

      if (criticos.isEmpty()) {
          System.out.println(">> Nenhum produto em estado critico no momento.");
          return;
      }

      System.out.println(">> Produtos em estado critico (estoque <= minimo), do mais urgente ao menos urgente:");
      criticos.forEach(p -> System.out.println("   " + p));
  }

    private static void processarProximoPedido() {
        if (!sistema.existePedidoPendente()) {
            System.out.println(">> Nao ha pedidos pendentes na fila.");
            return;
        }
        PedidoCompra pedido = sistema.processarProximoPedido();
        System.out.println(">> Pedido processado: " + pedido);
    }

    private static void processarTodosOsPedidos() {
        if (!sistema.existePedidoPendente()) {
            System.out.println(">> Nao ha pedidos pendentes na fila.");
            return;
        }
        int total = 0;
        while (sistema.existePedidoPendente()) {
            sistema.processarProximoPedido();
            total++;
        }
        System.out.println(">> " + total + " pedido(s) processado(s).");
    }

    private static void removerProduto() {
        System.out.print("Codigo do produto a remover: ");
        int codigo = Integer.parseInt(leitor.nextLine().trim());
        boolean removido = sistema.removerProduto(codigo);
        System.out.println(removido ? ">> Produto removido." : ">> Produto nao encontrado.");
    }

    private static void ordenarPorEstoqueHeapsort() {
        List<Produto> ordenados = sistema.ordenarProdutosPorEstoque();
        System.out.println(">> Produtos ordenados por estoque crescente (heapsort, nao altera a heap original):");
        ordenados.forEach(p -> System.out.println("   " + p));
    }

    private static boolean logsAtivos = true;

    private static void alternarLogs() {
        logsAtivos = !logsAtivos;
        if (logsAtivos) {
            Log.ativar();
            System.out.println(">> Logs das estruturas LIGADOS.");
        } else {
            Log.desativar();
            System.out.println(">> Logs das estruturas DESLIGADOS.");
        }
    }
}
