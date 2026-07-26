package com.estoque.estruturas.avl;

import com.estoque.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Arvore AVL (Arvore Binaria de Busca auto-balanceada) generica.
 *
 * Usada no sistema para indexar o catalogo de produtos pelo CODIGO
 * (chave unica, Comparable), garantindo busca, insercao e remocao em
 * O(log n) mesmo no pior caso, gracas ao balanceamento automatico
 * (fator de balanceamento sempre em {-1, 0, 1}).
 */
public class ArvoreAVL<K extends Comparable<K>, V> {

    private NoAVL<K, V> raiz;
    private int quantidadeElementos;

    public ArvoreAVL() {
        this.raiz = null;
        this.quantidadeElementos = 0;
    }

    // ---------------------------------------------------------------
    // API publica
    // ---------------------------------------------------------------

    /** Insere (ou atualiza, se a chave ja existir) um par chave/valor. Custo O(log n). */
    public void inserir(K chave, V valor) {
        raiz = inserir(raiz, chave, valor);
    }

    /** Busca o valor associado a chave. Custo O(log n). */
    public Optional<V> buscar(K chave) {
        NoAVL<K, V> atual = raiz;
        while (atual != null) {
            int cmp = chave.compareTo(atual.chave);
            if (cmp == 0) {
                Log.log("AVL", "buscar(" + chave + ") -> encontrado");
                return Optional.of(atual.valor);
            }
            atual = (cmp < 0) ? atual.esquerda : atual.direita;
        }
        Log.log("AVL", "buscar(" + chave + ") -> nao encontrado");
        return Optional.empty();
    }

    public boolean contem(K chave) {
        return buscar(chave).isPresent();
    }

    /** Remove o no com a chave informada, se existir. Custo O(log n). */
    public void remover(K chave) {
        raiz = remover(raiz, chave);
    }

    public boolean estaVazia() {
        return raiz == null;
    }

    public int tamanho() {
        return quantidadeElementos;
    }

    /** Percurso em ordem (in-order) -> retorna os valores ordenados pela chave. Custo O(n). */
    public List<V> emOrdem() {
        List<V> resultado = new ArrayList<>();
        emOrdem(raiz, resultado);
        return resultado;
    }

    // ---------------------------------------------------------------
    // Insercao (recursiva) + balanceamento
    // ---------------------------------------------------------------

    private NoAVL<K, V> inserir(NoAVL<K, V> no, K chave, V valor) {
        if (no == null) {
            quantidadeElementos++;
            Log.log("AVL", "inserir(" + chave + ") -> novo no criado");
            return new NoAVL<>(chave, valor);
        }
        int cmp = chave.compareTo(no.chave);
        if (cmp < 0) {
            no.esquerda = inserir(no.esquerda, chave, valor);
        } else if (cmp > 0) {
            no.direita = inserir(no.direita, chave, valor);
        } else {
            // chave ja existe: atualiza o valor (sem alterar a estrutura)
            no.valor = valor;
            Log.log("AVL", "inserir(" + chave + ") -> chave ja existia, valor atualizado");
            return no;
        }
        return balancear(no);
    }

    // ---------------------------------------------------------------
    // Remocao (recursiva) + balanceamento
    // ---------------------------------------------------------------

    private NoAVL<K, V> remover(NoAVL<K, V> no, K chave) {
        if (no == null) {
            Log.log("AVL", "remover(" + chave + ") -> chave nao encontrada");
            return null;
        }
        int cmp = chave.compareTo(no.chave);
        if (cmp < 0) {
            no.esquerda = remover(no.esquerda, chave);
        } else if (cmp > 0) {
            no.direita = remover(no.direita, chave);
        } else {
            // no encontrado
            Log.log("AVL", "remover(" + chave + ") -> no encontrado, removendo");
            if (no.esquerda == null || no.direita == null) {
                NoAVL<K, V> filho = (no.esquerda != null) ? no.esquerda : no.direita;
                no = filho; // pode ficar null (era folha) ou o unico filho
                quantidadeElementos--;
            } else {
                // dois filhos: substitui pelo sucessor (menor da subarvore direita)
                NoAVL<K, V> sucessor = minimo(no.direita);
                no.chave = sucessor.chave;
                no.valor = sucessor.valor;
                // remove fisicamente o no sucessor da subarvore direita
                // (decremento de quantidadeElementos ocorre nessa chamada recursiva)
                no.direita = remover(no.direita, sucessor.chave);
            }
        }
        if (no == null) {
            return null;
        }
        return balancear(no);
    }

    private NoAVL<K, V> minimo(NoAVL<K, V> no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    // ---------------------------------------------------------------
    // Balanceamento AVL (rotacoes)
    // ---------------------------------------------------------------

    private NoAVL<K, V> balancear(NoAVL<K, V> no) {
        atualizarAltura(no);
        int fb = fatorBalanceamento(no);

        // Caso Esquerda-Esquerda
        if (fb > 1 && fatorBalanceamento(no.esquerda) >= 0) {
            Log.log("AVL", "rotacao simples a DIREITA no no " + no.chave);
            return rotacaoDireita(no);
        }
        // Caso Esquerda-Direita
        if (fb > 1 && fatorBalanceamento(no.esquerda) < 0) {
            Log.log("AVL", "rotacao dupla ESQUERDA-DIREITA no no " + no.chave);
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }
        // Caso Direita-Direita
        if (fb < -1 && fatorBalanceamento(no.direita) <= 0) {
            Log.log("AVL", "rotacao simples a ESQUERDA no no " + no.chave);
            return rotacaoEsquerda(no);
        }
        // Caso Direita-Esquerda
        if (fb < -1 && fatorBalanceamento(no.direita) > 0) {
            Log.log("AVL", "rotacao dupla DIREITA-ESQUERDA no no " + no.chave);
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }
        return no;
    }

    private NoAVL<K, V> rotacaoDireita(NoAVL<K, V> y) {
        NoAVL<K, V> x = y.esquerda;
        NoAVL<K, V> t2 = x.direita;

        x.direita = y;
        y.esquerda = t2;

        atualizarAltura(y);
        atualizarAltura(x);
        return x;
    }

    private NoAVL<K, V> rotacaoEsquerda(NoAVL<K, V> x) {
        NoAVL<K, V> y = x.direita;
        NoAVL<K, V> t2 = y.esquerda;

        y.esquerda = x;
        x.direita = t2;

        atualizarAltura(x);
        atualizarAltura(y);
        return y;
    }

    private int altura(NoAVL<K, V> no) {
        return (no == null) ? 0 : no.altura;
    }

    private void atualizarAltura(NoAVL<K, V> no) {
        no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));
    }

    private int fatorBalanceamento(NoAVL<K, V> no) {
        return (no == null) ? 0 : altura(no.esquerda) - altura(no.direita);
    }

    private void emOrdem(NoAVL<K, V> no, List<V> resultado) {
        if (no == null) {
            return;
        }
        emOrdem(no.esquerda, resultado);
        resultado.add(no.valor);
        emOrdem(no.direita, resultado);
    }
}
