package com.estoque.util;

// Classe utilitaria para log de eventos do sistema, com possibilidade de ligar/desligar globalmente.
public final class Log {

    private static boolean ativo = true;

    private Log() {
    }

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
