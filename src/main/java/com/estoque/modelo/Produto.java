package com.estoque.modelo;

import java.util.Objects;

/**
 * Representa um produto do catalogo de estoque.
 * 
 * O CODIGO: usada como chave na Arvore AVL) e o
 * NOME: usado como chave de busca na Tabela Hash.
 * A QUANTIDADE EM ESTOQUE é o criterio de prioridade usado na Heap (min-heap):
 * quanto menor o estoque, maior a prioridade de reposicao.
 */
public class Produto {

    private final int codigo;
    private String nome;
    private int quantidadeEstoque;
    private int estoqueMinimo;
    private double preco;
    private String fornecedor;

    public Produto(int codigo, String nome, int quantidadeEstoque, int estoqueMinimo,
            double preco, String fornecedor) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
        this.preco = preco;
        this.fornecedor = fornecedor;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    // Indica se o estoque atual esta abaixo (ou igual) do minimo definido
    public boolean precisaReposicao() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Produto))
            return false;
        Produto produto = (Produto) o;
        return codigo == produto.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("Produto{codigo=%d, nome='%s', estoque=%d, minimo=%d, preco=R$%.2f, fornecedor='%s'}",
                codigo, nome, quantidadeEstoque, estoqueMinimo, preco, fornecedor);
    }
}
