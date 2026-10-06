package arcade;

import arcade.jogos.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        while (executando) {
            System.out.println("\n=================================");
            System.out.println("             ARCADE             ");
            System.out.println("=================================");
            System.out.println("1 - Jogo da Forca");
            System.out.println("2 - Adivinhe o Número");
            System.out.println("3 - Tetris");
            System.out.println("4 - Jogo da Velha");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String entrada = scanner.nextLine().trim();

            try {
                int opcao = Integer.parseInt(entrada);

                switch (opcao) {
                    case 1:
                        Forca.jogar(scanner);
                        break;
                    case 2:
                        AdivinheNumero.jogar(scanner);
                        break;
                    case 3:
                        Tetris.jogar();
                        break;
                    case 4:
                        JogoDaVelha jogo = new JogoDaVelha();
                        jogo.jogar();
                        break;
                    case 0:
                        System.out.println("\nObrigado por jogar! Até à próxima.");
                        executando = false;
                        break;
                    default:
                        System.out.println("Opção inválida! Escolha um número do menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Digite apenas números inteiros!");
            }
        }

        scanner.close();
    }
}