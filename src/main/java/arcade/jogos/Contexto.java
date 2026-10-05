package arcade.jogos;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.*;

public class Contexto {
    // Cada linha do arquivo: PALAVRA_SECRETA: palavra1, palavra2, ...
    // As palavras estão em ordem de proximidade (a primeira é a mais próxima)
    private static List<String> carregarLinhas() {
        List<String> linhas = new ArrayList<>();

        try (Scanner leitor = new Scanner(new File("src\\main\\java\\arcade\\jogos\\contexto.txt"), "UTF-8")) {
            while (leitor.hasNextLine()) {
                String linha = leitor.nextLine().trim();
                if (!linha.isEmpty() && linha.contains(":")) {
                    linhas.add(linha);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado!");
        }

        return linhas;
    }

    // Deixa a palavra em minúsculas e sem acentos (ex: "Cão" -> "cao")
    private static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().trim();
    }

    public static void jogar(Scanner scan) {
        List<String> linhas = carregarLinhas();

        if (linhas.isEmpty()) {
            System.out.println("Não há palavras no arquivo");
            return;
        }

        //
        Random sortear = new Random();
        String linhaSorteada = linhas.get(sortear.nextInt(linhas.size()));
        String[] partes = linhaSorteada.split(":", 2);
        String palavraSecreta = normalizar(partes[0]);

        // Monta o ranking: palavra secreta = 1, a próxima = 2, e assim por diante
        Map<String, Integer> ranking = new HashMap<>();
        ranking.put(palavraSecreta, 1);
        int posicao = 2;
        for (String palavra : partes[1].split(",")) {
            String p = normalizar(palavra);
            if (!p.isEmpty() && !ranking.containsKey(p)) {
                ranking.put(p, posicao);
                posicao++;
            }
        }

        //
        Map<String, Integer> palpites = new HashMap<>();
        Set<String> palavrasTentadas = new HashSet<>();
        int tentativas = 0;
        boolean acertou = false;

        System.out.println("\n======= Contexto =======");
        System.out.println("Descubra a palavra secreta!");
        System.out.println("Cada palpite recebe uma posição: quanto MENOR o número, mais perto você está.");
        System.out.println("A palavra secreta é a posição 1. Digite 'desistir' para ver a resposta.");

        while (!acertou) {
            System.out.print("\nDigite uma palavra: ");

            String palpite;

            //
            try {
                palpite = normalizar(scan.nextLine());

                if (palpite.isEmpty()) {
                    throw new IllegalArgumentException("A entrada não pode ser vazia!");
                }

                if (palpite.contains(" ")) {
                    throw new IllegalArgumentException("Digite apenas uma palavra");
                }
            }

            catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
                continue;
            }

            if (palpite.equals("desistir")) {
                break;
            }

            //
            if (palavrasTentadas.contains(palpite)) {
                System.out.println("Você já tentou a palavra " + palpite.toUpperCase());
                continue;
            }

            palavrasTentadas.add(palpite);
            tentativas++;
            int pos = ranking.getOrDefault(palpite, -1);

            if (pos == -1) {
                System.out.println(palpite.toUpperCase() + " -> muito distante (fora do contexto)");
            }

            else {
                palpites.put(palpite, pos);
                System.out.println(palpite.toUpperCase() + " -> posição " + pos + " " + dica(pos));
                acertou = pos == 1;
            }

            exibirMelhores(palpites);
        }

        if (acertou) {
            System.out.println("Parabéns!! Você acertou a palavra " + palavraSecreta.toUpperCase()
                    + " em " + tentativas + " tentativa(s)!");
        }

        else {
            System.out.println("Que pena! A palavra secreta era: " + palavraSecreta.toUpperCase());
        }
    }

    private static String dica(int pos) {
        if (pos == 1) return "(ACERTOU!)";
        if (pos <= 5) return "(muito quente!)";
        if (pos <= 15) return "(quente)";
        return "(morno)";
    }

    // Mostra os palpites do mais próximo para o mais distante
    private static void exibirMelhores(Map<String, Integer> palpites) {
        if (palpites.isEmpty()) {
            return;
        }

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(palpites.entrySet());
        lista.sort(Map.Entry.comparingByValue());

        System.out.println("--- Seus melhores palpites ---");
        for (Map.Entry<String, Integer> item : lista) {
            System.out.printf("%4d  %s%n", item.getValue(), item.getKey().toUpperCase());
        }
    }
}
