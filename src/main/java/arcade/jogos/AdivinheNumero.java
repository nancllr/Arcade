package arcade.jogos;

import java.util.Random;
import java.util.Scanner;

public class AdivinheNumero {
    private static final int MAXIMO = 100;
    private static final int TENTATIVAS = 7;

    public static void jogar(Scanner scan) {
        Random sortear = new Random();
        int numeroSecreto = sortear.nextInt(MAXIMO) + 1;
        int tentativasRestantes = TENTATIVAS;
        boolean acertou = false;

        System.out.println("\n======= Adivinhe o Número =======");
        System.out.println("Pensei em um número entre 1 e " + MAXIMO + ".");
        System.out.println("Você tem " + TENTATIVAS + " tentativas para descobrir!");

        while (tentativasRestantes > 0 && !acertou) {
            System.out.println("\nTentativas restantes: " + tentativasRestantes);
            System.out.print("Digite seu palpite: ");

            int palpite;

            //
            try {
                String entrada = scan.nextLine().trim();

                if (entrada.isEmpty()) {
                    throw new IllegalArgumentException("A entrada não pode ser vazia!");
                }

                palpite = Integer.parseInt(entrada);

                if (palpite < 1 || palpite > MAXIMO) {
                    throw new IllegalArgumentException("Digite um número entre 1 e " + MAXIMO + "!");
                }
            }

            catch (NumberFormatException e) {
                System.out.println("Erro: Digite apenas números inteiros!");
                continue;
            }

            catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
                continue;
            }

            //
            tentativasRestantes--;

            if (palpite == numeroSecreto) {
                acertou = true;
            }

            else if (palpite < numeroSecreto) {
                System.out.println("O número secreto é MAIOR que " + palpite);
            }

            else {
                System.out.println("O número secreto é MENOR que " + palpite);
            }
        }

        if (acertou) {
            int usadas = TENTATIVAS - tentativasRestantes;
            System.out.println("Parabéns!! Você acertou o número " + numeroSecreto + " em " + usadas + " tentativa(s)!");
        }

        else {
            System.out.println("GAME OVER! Suas tentativas acabaram, o número era: " + numeroSecreto);
        }
    }
}
