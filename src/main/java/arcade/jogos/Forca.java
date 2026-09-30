package arcade.jogos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Forca {
    private static List<String> carregarPalavras() {
        List<String> lista = new ArrayList<>();

        try (Scanner leitor = new Scanner(new File("src\\main\\java\\arcade\\jogos\\palavras.txt"))) {
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine().trim();
                if (!linha.isEmpty()) {
                    lista.add(linha);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado!");
        }

        return lista;
    }

    private static String exibirProgresso(char[] progresso) {
        String resultado = "";

        for (char c : progresso) {
            resultado += c + " ";
        }
        return resultado.trim();
    }

    public static void jogar(Scanner scan) {
        List<String> palavras = carregarPalavras();

        if (palavras.isEmpty()) {
            System.out.println("Não há palavras no arquivo");
            return;
        }

        //
        Random sortear = new Random();
        String palavraSecreta = palavras.get(sortear.nextInt(palavras.size())).toUpperCase().trim();

        //
        char[] progresso = new char[palavraSecreta.length()];
        for (int i = 0; i < progresso.length; i++) {
            progresso[i] = '_';
        }

        //
        int tentativas = 6;
        List<Character> letrasTentadas = new ArrayList<>();
        boolean acertou = false;

        //
        System.out.println("\n======= Jogo da Forca =======");

        while (tentativas > 0 && !acertou) {
            System.out.println("\nPalavra: " + exibirProgresso(progresso));
            System.out.println("Tentativas restantes: " + tentativas);
            System.out.println("Letras já tentadas: " + letrasTentadas);
            System.out.print("Digite uma letra: ");

            char letra = ' ';

            //
            try {
                String entrada = scan.nextLine().trim().toUpperCase();

                if (entrada.isEmpty()) {
                    throw new IllegalArgumentException("A entrada não pode ser vazia!");
                }

                if  (entrada.length() > 1) {
                    throw new IllegalArgumentException("Digite apenas uma letra");
                }

                letra = entrada.charAt(0);

                if (!Character.isLetter(letra)) {
                    throw new IllegalArgumentException("Digite um caractere válido!");
                }
            }

            catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
                continue;
            }

            //
            if (letrasTentadas.contains(letra)) {
                System.out.println("Você já tentou a letra " + letra);
                continue;
            }

            letrasTentadas.add(letra);

            if (palavraSecreta.indexOf(letra) >= 0) {
                System.out.println(" A letra " + letra + " pertence a palavra.");
                for (int i = 0; i < palavraSecreta.length(); i++) {
                    if (palavraSecreta.charAt(i) == letra) {
                        progresso[i] = letra;
                    }
                }
            }

            else {
                tentativas--;
                System.out.println("A letra " + letra + " não está na palavra");
            }

            acertou = String.valueOf(progresso).equals(palavraSecreta);
        }

        if (acertou) {
            System.out.println("Parabéns!! Você acertou a palavra: " + palavraSecreta);
        }

        else {
            System.out.println("GAME OVER! Suas tentativas acabaram, a palavra era: " + palavraSecreta);
        }
    }
}
