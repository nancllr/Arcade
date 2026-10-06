import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// ==========================================
// 1. ABSTRAÇÃO & ENCAPSULAMENTO (Classe Mãe)
// ==========================================
abstract class ElementoArcade {
    private int x, y;
    private int largura, altura;
    private int velocidade;
    private boolean ativo = true;

    public ElementoArcade(int x, int y, int largura, int altura, int velocidade) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.velocidade = velocidade;
    }

    // Métodos Abstratos (Polimorfismo nas subclasses)
    public abstract void desenhar(Graphics2D g);
    public abstract void atualizarPosition();

    // Verificação de Colisão Base
    public boolean colidiuCom(ElementoArcade outro) {
        Rectangle r1 = new Rectangle(x, y, largura, altura);
        Rectangle r2 = new Rectangle(outro.x, outro.y, outro.largura, outro.altura);
        return r1.intersects(r2);
    }

    // Encapsulamento
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
    public int getVelocidade() { return velocidade; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}

// ==========================================
// 2. HERANÇA & POLIMORFISMO (Subclasses)
// ==========================================

// Jogador: Nave Amarela
class NaveJogador extends ElementoArcade {
    private int direcaoX = 0;

    public NaveJogador(int x, int y) {
        super(x, y, 40, 20, 7);
    }

    public void mover(int direcao) {
        this.direcaoX = direcao;
    }

    @Override
    public void atualizarPosition() {
        setX(getX() + direcaoX * getVelocidade());
        // Limites da tela
        if (getX() < 0) setX(0);
        if (getX() > 740) setX(740);
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(Color.YELLOW);
        // Desenha uma nave em formato triangular
        int[] px = {getX(), getX() + getLargura() / 2, getX() + getLargura()};
        int[] py = {getY() + getAltura(), getY(), getY() + getAltura()};
        g.fillPolygon(px, py, 3);
    }
}

// Inimigo: Nave Vermelha
class InimigoArcade extends ElementoArcade {
    public InimigoArcade(int x, int y) {
        super(x, y, 30, 20, 3);
    }

    @Override
    public void atualizarPosition() {
        setY(getY() + getVelocidade()); // Desce pela tela
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(getX(), getY(), getLargura(), getAltura());
        g.setColor(Color.WHITE);
        g.fillRect(getX() + 5, getY() + 5, 5, 5); // Detalhes dos olhos
        g.fillRect(getX() + 20, getY() + 5, 5, 5);
    }
}

// Tiro do Jogador: Projetil Cyan
class Tiro extends ElementoArcade {
    public Tiro(int x, int y) {
        super(x, y, 6, 12, 10);
    }

    @Override
    public void atualizarPosition() {
        setY(getY() - getVelocidade()); // Sobe pela tela
        if (getY() < 0) setAtivo(false);
    }

    @Override
    public void desenhar(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.fillRect(getX(), getY(), getLargura(), getAltura());
    }
}

// ==========================================
// 3. TELA DO JOGO (Painel Gráfico)
// ==========================================
class PainelArcade extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private NaveJogador jogador;
    private List<InimigoArcade> inimigos;
    private List<Tiro> tiros;
    private int pontos = 0;
    private boolean gameOver = false;
    private Random random = new Random();

    public PainelArcade() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        jogador = new NaveJogador(380, 520);
        inimigos = new ArrayList<>();
        tiros = new ArrayList<>();

        // Loop de renderização (60 FPS aprox.)
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (!gameOver) {
            // Desenhar Jogador
            jogador.desenhar(g2d);

            // Desenhar Tiros
            for (Tiro t : tiros) {
                t.desenhar(g2d);
            }

            // Desenhar Inimigos
            for (InimigoArcade inimigo : inimigos) {
                inimigo.desenhar(g2d);
            }

            // HUD do Placares Arcade
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Consolas", Font.BOLD, 20));
            g2d.drawString("SCORE: " + pontos, 20, 30);
        } else {
            // Tela de Game Over
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Impact", Font.BOLD, 50));
            g2d.drawString("GAME OVER", 280, 280);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Consolas", Font.PLAIN, 20));
            g2d.drawString("Pontuação Final: " + pontos, 290, 330);
            g2d.drawString("Pressione R para Reiniciar", 260, 370);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Atualizar Jogador
            jogador.atualizarPosition();

            // Gerar novos inimigos aleatoriamente
            if (random.nextInt(100) < 3) {
                inimigos.add(new InimigoArcade(random.nextInt(750), -20));
            }

            // Atualizar Tiros
            Iterator<Tiro> itTiro = tiros.iterator();
            while (itTiro.hasNext()) {
                Tiro t = itTiro.next();
                t.atualizarPosition();
                if (!t.isAtivo()) itTiro.remove();
            }

            // Atualizar Inimigos e Tratar Colisões
            Iterator<InimigoArcade> itInimigo = inimigos.iterator();
            while (itInimigo.hasNext()) {
                InimigoArcade inimigo = itInimigo.next();
                inimigo.atualizarPosition();

                // Colisão Inimigo vs Jogador
                if (inimigo.colidiuCom(jogador)) {
                    gameOver = true;
                }

                // Colisão Tiro vs Inimigo
                for (Tiro t : tiros) {
                    if (t.isAtivo() && t.colidiuCom(inimigo)) {
                        inimigo.setAtivo(false);
                        t.setAtivo(false);
                        pontos += 100;
                    }
                }

                // Remove se foi destruído ou saiu da tela
                if (!inimigo.isAtivo()) {
                    itInimigo.remove();
                } else if (inimigo.getY() > 600) {
                    itInimigo.remove(); // Passou da tela sem destruir
                }
            }

            repaint();
        }
    }

    // Controles pelo Teclado
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            jogador.mover(-1);
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            jogador.mover(1);
        }
        if (key == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                tiros.add(new Tiro(jogador.getX() + 17, jogador.getY()));
            }
        }
        if (key == KeyEvent.VK_R && gameOver) {
            // Reiniciar
            jogador = new NaveJogador(380, 520);
            inimigos.clear();
            tiros.clear();
            pontos = 0;
            gameOver = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT ||
            key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            jogador.mover(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

// ==========================================
// 4. EXECUTÁVEL PRINCIPAL (Janela Swing)
// ==========================================
public class JogoArcade extends JFrame {
    public JogoArcade() {
        setTitle("Arcade Space Shooter - POO em Java");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new PainelArcade());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(JogoArcade::new);
    }
}
