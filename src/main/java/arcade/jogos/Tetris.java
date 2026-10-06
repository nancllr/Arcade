package arcade.jogos;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Random;

public class Tetris extends Application {

    private static boolean javaFxIniciado = false;
    private static final int TAMANHO_BLOCO = 30;
    private static final int COLUNAS = 10;
    private static final int LINHAS = 20;
    private static final int COLUNAS_PAINEL = 6; // Espaço extra para o painel lateral

    private int[][] grade = new int[LINHAS][COLUNAS];

    // Variáveis da peça atual
    private int[][] pecaAtual;
    private int pecaX, pecaY;
    private int corAtual;

    // Fila e Reserva
    private int[][] pecaProxima;
    private int corProxima;
    private int[][] pecaGuardada;
    private int corGuardada;
    private boolean jaTrocou = false; // Impede múltiplas trocas no mesmo turno

    // Cores das peças (Índices de 1 a 7)
    private final Color[] cores = {
            Color.rgb(30, 30, 30), // Fundo do tabuleiro (0)
            Color.CYAN, Color.YELLOW, Color.PURPLE,
            Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED
    };

    // Matrizes dos Tetrominós
    private final int[][][] TETROMINOS = {
            {{1, 1, 1, 1}},                         // I
            {{2, 2}, {2, 2}},                       // O
            {{0, 3, 0}, {3, 3, 3}},                 // T
            {{4, 0, 0}, {4, 4, 4}},                 // J
            {{0, 0, 5}, {5, 5, 5}},                 // L
            {{0, 6, 6}, {6, 6, 0}},                 // S
            {{7, 7, 0}, {0, 7, 7}}                  // Z
    };

    public static void jogar() {
        if (!javaFxIniciado) {
            javaFxIniciado = true;
            Application.launch(Tetris.class);
        } else {
            System.out.println("O motor gráfico já foi utilizado nesta sessão. Reinicie o Arcade para jogar Tetris novamente.");
        }
    }

    @Override
    public void start(Stage palco) {
        // O Canvas agora é mais largo para caber o painel lateral
        Canvas canvas = new Canvas((COLUNAS + COLUNAS_PAINEL) * TAMANHO_BLOCO, LINHAS * TAMANHO_BLOCO);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        inicializarJogo();

        // Loop principal do jogo (Gravidade)
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            if (podeMover(pecaAtual, pecaX, pecaY + 1)) {
                pecaY++;
            } else {
                travarPeca();
            }
            desenhar(gc);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene cena = new Scene(new StackPane(canvas));

        // Captura de controles do teclado
        cena.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.RIGHT && podeMover(pecaAtual, pecaX + 1, pecaY)) {
                pecaX++;
            } else if (e.getCode() == KeyCode.LEFT && podeMover(pecaAtual, pecaX - 1, pecaY)) {
                pecaX--;
            } else if (e.getCode() == KeyCode.DOWN && podeMover(pecaAtual, pecaX, pecaY + 1)) {
                pecaY++;
            } else if (e.getCode() == KeyCode.UP) {
                int[][] rotacionada = rotacionar(pecaAtual);
                if (podeMover(rotacionada, pecaX, pecaY)) {
                    pecaAtual = rotacionada;
                }
            } else if (e.getCode() == KeyCode.E) { // Tecla E para guardar/trocar a peça
                trocarPecaGuardada();
            }
            desenhar(gc);
        });

        palco.setScene(cena);
        palco.setTitle("Tetris Arcade");
        palco.setResizable(false);
        palco.setOnCloseRequest(e -> {
            timeline.stop();
            System.out.println("\nFechando Tetris... Pressione ENTER no terminal para continuar.");
        });
        palco.show();
    }

    private void inicializarJogo() {
        grade = new int[LINHAS][COLUNAS];
        pecaGuardada = null;
        gerarProxima();
        pegarProxima();
    }

    private void gerarProxima() {
        Random random = new Random();
        pecaProxima = TETROMINOS[random.nextInt(TETROMINOS.length)];
        corProxima = extrairCor(pecaProxima);
    }

    private void pegarProxima() {
        pecaAtual = pecaProxima;
        corAtual = corProxima;
        gerarProxima(); // Prepara a próxima da fila

        resetarPosicaoAtual();
        jaTrocou = false; // Permite trocar a peça neste novo turno

        // Verifica Game Over
        if (!podeMover(pecaAtual, pecaX, pecaY)) {
            inicializarJogo(); // Reseta o jogo
        }
    }

    private void trocarPecaGuardada() {
        if (jaTrocou) return; // Só pode trocar uma vez antes de travar no fundo

        if (pecaGuardada == null) {
            // Guarda a atual e puxa a próxima da fila
            pecaGuardada = pecaAtual;
            corGuardada = corAtual;
            pegarProxima();
        } else {
            // Troca a atual pela guardada
            int[][] tempPeca = pecaAtual;
            int tempCor = corAtual;
            pecaAtual = pecaGuardada;
            corAtual = corGuardada;
            pecaGuardada = tempPeca;
            corGuardada = tempCor;

            resetarPosicaoAtual();
        }
        jaTrocou = true; // Bloqueia novas trocas até a peça atual travar
    }

    private void resetarPosicaoAtual() {
        pecaX = COLUNAS / 2 - pecaAtual[0].length / 2;
        pecaY = 0;
    }

    private int extrairCor(int[][] peca) {
        for (int[] linha : peca) {
            for (int bloco : linha) {
                if (bloco != 0) return bloco;
            }
        }
        return 1;
    }

    private boolean podeMover(int[][] peca, int novoX, int novoY) {
        for (int r = 0; r < peca.length; r++) {
            for (int c = 0; c < peca[r].length; c++) {
                if (peca[r][c] != 0) {
                    int x = novoX + c;
                    int y = novoY + r;
                    if (x < 0 || x >= COLUNAS || y >= LINHAS || (y >= 0 && grade[y][x] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int[][] rotacionar(int[][] peca) {
        int r = peca.length;
        int c = peca[0].length;
        int[][] rotacionada = new int[c][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                rotacionada[j][r - 1 - i] = peca[i][j];
            }
        }
        return rotacionada;
    }

    private void travarPeca() {
        for (int r = 0; r < pecaAtual.length; r++) {
            for (int c = 0; c < pecaAtual[r].length; c++) {
                if (pecaAtual[r][c] != 0) {
                    grade[pecaY + r][pecaX + c] = corAtual;
                }
            }
        }
        limparLinhas();
        pegarProxima(); // Puxa a próxima peça da fila
    }

    private void limparLinhas() {
        for (int r = LINHAS - 1; r >= 0; r--) {
            boolean cheia = true;
            for (int c = 0; c < COLUNAS; c++) {
                if (grade[r][c] == 0) {
                    cheia = false;
                    break;
                }
            }
            if (cheia) {
                for (int y = r; y > 0; y--) {
                    System.arraycopy(grade[y - 1], 0, grade[y], 0, COLUNAS);
                }
                Arrays.fill(grade[0], 0);
                r++;
            }
        }
    }

    private void desenhar(GraphicsContext gc) {
        // 1. Limpa fundo do tabuleiro principal
        gc.setFill(cores[0]);
        gc.fillRect(0, 0, COLUNAS * TAMANHO_BLOCO, LINHAS * TAMANHO_BLOCO);

        // 2. Desenha painel lateral (Fundo)
        gc.setFill(Color.rgb(50, 50, 50));
        gc.fillRect(COLUNAS * TAMANHO_BLOCO, 0, COLUNAS_PAINEL * TAMANHO_BLOCO, LINHAS * TAMANHO_BLOCO);

        // 3. Desenha a grade travada
        for (int r = 0; r < LINHAS; r++) {
            for (int c = 0; c < COLUNAS; c++) {
                if (grade[r][c] != 0) {
                    gc.setFill(cores[grade[r][c]]);
                    gc.fillRect(c * TAMANHO_BLOCO, r * TAMANHO_BLOCO, TAMANHO_BLOCO - 1, TAMANHO_BLOCO - 1);
                }
            }
        }

        // 4. Desenha a peça atual caindo
        desenharPeca(gc, pecaAtual, corAtual, pecaX, pecaY);

        // 5. Desenha textos e peças no painel lateral
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));

        gc.fillText("PRÓXIMA", (COLUNAS + 1) * TAMANHO_BLOCO, 2 * TAMANHO_BLOCO);
        desenharPeca(gc, pecaProxima, corProxima, COLUNAS + 1.5, 3);

        gc.fillText("GUARDADA (E)", (COLUNAS + 0.5) * TAMANHO_BLOCO, 9 * TAMANHO_BLOCO);
        if (pecaGuardada != null) {
            // Se a peça já foi trocada neste turno, desenha ela meio transparente (cinza) para indicar bloqueio
            int corDraw = jaTrocou ? 0 : corGuardada;
            if(jaTrocou) gc.setFill(Color.GRAY);

            desenharPeca(gc, pecaGuardada, corDraw != 0 ? corDraw : 0, COLUNAS + 1.5, 10);

            // Corrige a cor da peça guardada desenhando por cima caso esteja bloqueada
            if(jaTrocou) {
                for (int r = 0; r < pecaGuardada.length; r++) {
                    for (int c = 0; c < pecaGuardada[r].length; c++) {
                        if (pecaGuardada[r][c] != 0) {
                            gc.setFill(Color.GRAY);
                            gc.fillRect((COLUNAS + 1.5 + c) * TAMANHO_BLOCO, (10 + r) * TAMANHO_BLOCO, TAMANHO_BLOCO - 1, TAMANHO_BLOCO - 1);
                        }
                    }
                }
            }
        }
    }

    // Método auxiliar para desenhar peças em qualquer lugar (usado no tabuleiro e no painel)
    private void desenharPeca(GraphicsContext gc, int[][] peca, int corIndex, double offsetX, double offsetY) {
        if (corIndex != 0) gc.setFill(cores[corIndex]);
        for (int r = 0; r < peca.length; r++) {
            for (int c = 0; c < peca[r].length; c++) {
                if (peca[r][c] != 0) {
                    gc.fillRect((offsetX + c) * TAMANHO_BLOCO, (offsetY + r) * TAMANHO_BLOCO, TAMANHO_BLOCO - 1, TAMANHO_BLOCO - 1);
                }
            }
        }
    }
}