package com.estoque.util;

/**
 * Utilitario simples de log usado pelas estruturas de dados para registrar
 * as principais operacoes (insercao, remocao, busca, rotacoes em AVL,
 * colisoes na Tabela Hash, operacoes de heap, push/pop na pilha, etc).
 *
 * Isso facilita a depuracao, ajuda a entender o funcionamento interno das
 * estruturas e enriquece a apresentacao do projeto (log em tempo real de
 * cada operacao critica).
 */
public final class Log {

    /** Liga/desliga os logs globalmente (util para silenciar em testes). */
    private static boolean ativo = true;

    private Log() { }

    public static void ativar() {
        ativo = true;
    }

    public static void desativar() {
        ativo = false;
    }

    public static void log(String modulo, String mensagem) {
        if (ativo) {
            System.out.println("[LOG-" + modulo + "] " + mensagem);
        }
    }
}
