package arcade.jogos;

import java.util.Scanner;

public class JogoDaVelha {

    // Exceção personalizada para qualquer entrada inválida do usuário
    static class EntradaInvalidaException extends Exception {
        public EntradaInvalidaException(String mensagem) {
            super(mensagem);
        }
    }

    private final char[][] tabuleiro = new char[3][3];
    private final Scanner scanner = new Scanner(System.in);

    public JogoDaVelha() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabuleiro[i][j] = ' ';
            }
        }
    }

    // Lê o símbolo do jogador e lança exceção se não for X ou O
    private char lerSimbolo(String mensagem) throws EntradaInvalidaException {
        System.out.print(mensagem);
        String entrada = scanner.nextLine().trim().toUpperCase();

        if (entrada.length() != 1 || (entrada.charAt(0) != 'X' && entrada.charAt(0) != 'O')) {
            throw new EntradaInvalidaException("Entrada inválida! Digite apenas X ou O.");
        }
        return entrada.charAt(0);
    }

    // Repete a leitura até o usuário digitar X ou O
    private char escolherSimbolo() {
        while (true) {
            try {
                return lerSimbolo("Jogador 1, escolha seu símbolo (X ou O): ");
            } catch (EntradaInvalidaException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Lê a jogada (linha e coluna de 1 a 3) e valida
    private int[] lerJogada(char jogador) throws EntradaInvalidaException {
        System.out.print("Jogador " + jogador + ", digite linha e coluna (1-3), ex: 2 3: ");
        String[] partes = scanner.nextLine().trim().split("\\s+");

        if (partes.length != 2) {
            throw new EntradaInvalidaException("Digite dois números separados por espaço.");
        }

        int linha, coluna;
        try {
            linha = Integer.parseInt(partes[0]) - 1;
            coluna = Integer.parseInt(partes[1]) - 1;
        } catch (NumberFormatException e) {
            throw new EntradaInvalidaException("Digite apenas números inteiros.");
        }

        if (linha < 0 || linha > 2 || coluna < 0 || coluna > 2) {
            throw new EntradaInvalidaException("Posição fora do tabuleiro! Use valores de 1 a 3.");
        }
        if (tabuleiro[linha][coluna] != ' ') {
            throw new EntradaInvalidaException("Essa posição já está ocupada!");
        }
        return new int[]{linha, coluna};
    }

    private void exibirTabuleiro() {
        System.out.println();
        System.out.println("    1   2   3");
        for (int i = 0; i < 3; i++) {
            System.out.println("  -------------");
            System.out.print((i + 1) + " |");
            for (int j = 0; j < 3; j++) {
                System.out.print(" " + tabuleiro[i][j] + " |");
            }
            System.out.println();
        }
        System.out.println("  -------------");
        System.out.println();
    }

    private boolean venceu(char s) {
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][0] == s && tabuleiro[i][1] == s && tabuleiro[i][2] == s) return true;
            if (tabuleiro[0][i] == s && tabuleiro[1][i] == s && tabuleiro[2][i] == s) return true;
        }
        if (tabuleiro[0][0] == s && tabuleiro[1][1] == s && tabuleiro[2][2] == s) return true;
        return tabuleiro[0][2] == s && tabuleiro[1][1] == s && tabuleiro[2][0] == s;
    }

    private boolean empate() {
        for (char[] linha : tabuleiro) {
            for (char c : linha) {
                if (c == ' ') return false;
            }
        }
        return true;
    }

    public void jogar() {
        System.out.println("=== JOGO DA VELHA ===");
        char atual = escolherSimbolo();

        while (true) {
            exibirTabuleiro();

            try {
                int[] jogada = lerJogada(atual);
                tabuleiro[jogada[0]][jogada[1]] = atual;
            } catch (EntradaInvalidaException e) {
                System.out.println(e.getMessage());
                continue; // mantém o mesmo jogador
            }

            if (venceu(atual)) {
                exibirTabuleiro();
                System.out.println("Jogador " + atual + " venceu! Parabéns!");
                break;
            }
            if (empate()) {
                exibirTabuleiro();
                System.out.println("Deu velha! Empate.");
                break;
            }
            atual = (atual == 'X') ? 'O' : 'X';
        }
    }

    public static void main(String[] args) {
        new JogoDaVelha().jogar();
    }
}