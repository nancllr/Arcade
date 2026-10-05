package arcade;

import arcade.jogos.Forca;
import arcade.jogos.Tetris;

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
            System.out.println("2 - Tetris (JavaFX)");
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
                        System.out.println("Iniciando interface gráfica do Tetris...");
                        Tetris.jogar();
                        break;
                    case 0:
                        System.out.println("\nObrigado por jogar! Até a próxima.");
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