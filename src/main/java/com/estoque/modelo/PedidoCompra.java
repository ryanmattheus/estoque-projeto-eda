package com.estoque.modelo;

/**
 * Representa um pedido de compra (reposicao) de um produto.
 * Os pedidos sao processados EM ORDEM na Fila do sistema (FIFO),
 */
public class PedidoCompra {

    private static int proximoId = 1;

    private final int id;
    private final Produto produto;
    private final int quantidadeSolicitada;
    private final boolean automatico;
    private StatusPedido status;

    public PedidoCompra(Produto produto, int quantidadeSolicitada, boolean automatico) {
        this.id = proximoId++;
        this.produto = produto;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.automatico = automatico;
        this.status = StatusPedido.PENDENTE;
    }

    public int getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public boolean isAutomatico() {
        return automatico;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void marcarProcessado() {
        this.status = StatusPedido.PROCESSADO;
    }

    @Override
    public String toString() {
        return String.format("Pedido#%d{produto=%s (cod.%d), qtd=%d, origem=%s, status=%s}",
                id, produto.getNome(), produto.getCodigo(), quantidadeSolicitada,
                automatico ? "AUTOMATICO" : "MANUAL", status);
    }
}
