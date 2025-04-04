import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class snakegame extends JPanel implements ActionListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            String[] speeds = {"Easy", "Normal", "Hard"};
            int speedSelection = JOptionPane.showOptionDialog(frame, "Choose Difficulty", "Snake Game",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, speeds, speeds[1]);

            int speed;
            switch (speedSelection) {
                case 0: speed = 150; break; // Easy
                case 2: speed = 5; break;  // Hard
                default: speed = 100; // Normal
            }
            frame.add(new snakegame(speed));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private int TILE_SIZE = 20;
    private int WIDTH = 1000;
    private int HEIGHT = 600;
    private int MAX_TILES = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);
    private int[] x = new int[MAX_TILES];
    private int[] y = new int[MAX_TILES];
    private int snakeLength;
    private int foodX, foodY;
    private int score;
    private int highestScore = 0;
    private char direction;
    private char lastDirection = 'R';
    private boolean keyPressedLock = false;  // Prevent multiple inputs per frame
    private boolean running = false;
    private boolean gameOver = false;
    private boolean started = false;
    private Timer timer;

    public snakegame(int speed) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_R)
                        restartGame(speed);
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                        System.exit(0);
                } else {
                    if (!started) {
                        started = true;
                        running = true;
                    }
                    if (keyPressedLock)
                        return;
                    keyPressedLock = true;

                    char newDirection = direction;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_W:
                            if (lastDirection != 'D' && direction != 'D')
                                newDirection = 'U';
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_S:
                            if (lastDirection != 'U' && direction != 'U')
                                newDirection = 'D';
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_A:
                            if (lastDirection != 'R' && direction != 'R')
                                newDirection = 'L';
                            break;
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_D:
                            if (lastDirection != 'L' && direction != 'L')
                                newDirection = 'R';
                            break;
                    }
                    if (newDirection != direction) {
                        direction = newDirection;
                        lastDirection = newDirection;
                    }
                }
            }
        });

        startGame(speed);
    }

    private void startGame(int speed) {
        snakeLength = 3;
        score = 0;
        direction = 'R';
        lastDirection = 'R';
        started = false;
        gameOver = false;

        for (int snakeBody = 0; snakeBody < snakeLength; snakeBody++) {
            x[snakeBody] = 200 - (snakeBody * TILE_SIZE);
            y[snakeBody] = 200;
        }
        placeFood();

        timer = new Timer(speed, this);
        timer.start();
    }

    private void restartGame(int speed) {
        timer.stop();
        startGame(speed);
        repaint();
    }

    private void placeFood() {
        Random rand = new Random();
        foodX = rand.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = rand.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);

        if (running || !started) {
          
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, TILE_SIZE, TILE_SIZE);

      
            for (int snakeBody = 0; snakeBody < snakeLength; snakeBody++) {
                if (score >= 500) {
                    g.setColor(snakeBody == 0 ? Color.RED : Color.BLACK);
                } else if (score >= 300) {
                    g.setColor(snakeBody == 0 ? Color.RED : Color.ORANGE);
                } else if (score >= 200) {
                    g.setColor(snakeBody == 0 ? Color.RED : Color.BLUE);
                } else if (score >= 100) {
                    g.setColor(snakeBody == 0 ? Color.RED : Color.GREEN);
                } else {
                    g.setColor(snakeBody == 0 ? Color.RED : new Color(255 - (snakeBody + 1), 255, 100));
                }
                g.fillRoundRect(x[snakeBody], y[snakeBody], TILE_SIZE, TILE_SIZE, 10, 10);
            }

    
            if (!started) {
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.CYAN);
                g.drawString("Press Any Keys to Start!", WIDTH / 2 - 120, HEIGHT / 2);
            }
        } else {
            gameOverScreen(g);
        }
    }

    private void move() {
    
        int snakeHeadX = x[0];
        int snakeHeadY = y[0];

  
        for (int snakeBody = snakeLength; snakeBody > 0; snakeBody--) {
            x[snakeBody] = x[snakeBody - 1];
            y[snakeBody] = y[snakeBody - 1];
        }

   
        switch (direction) {
            case 'U':
                snakeHeadY -= TILE_SIZE;
                break;
            case 'D':
                snakeHeadY += TILE_SIZE;
                break;
            case 'L':
                snakeHeadX -= TILE_SIZE;
                break;
            case 'R':
                snakeHeadX += TILE_SIZE;
                break;
        }
    
        x[0] = snakeHeadX;
        y[0] = snakeHeadY;
    }

    private void checkCollision() {
    
        int snakeHeadX = x[0];
        int snakeHeadY = y[0];
        for (int snakeBody = 1; snakeBody < snakeLength; snakeBody++) {
            if (snakeHeadX == x[snakeBody] && snakeHeadY == y[snakeBody])
                running = false;
        }
        if (snakeHeadX < 0 || snakeHeadX >= WIDTH || snakeHeadY < 0 || snakeHeadY >= HEIGHT)
            running = false;
        if (!running) {
            if (score > highestScore) {
                highestScore = score;
            }
            gameOver = true;
            timer.stop();
            repaint();
        }
    }

    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++;
            score += 5;
            placeFood();
        }
    }

    private int centerX(Graphics g, String text) {
        FontMetrics metrics = g.getFontMetrics();
        return (WIDTH - metrics.stringWidth(text)) / 2;
    }

    private void gameOverScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.RED);
        g.drawString("Game Over!", centerX(g, "Game Over!"), HEIGHT / 2 - 40);
        g.drawString("Your Score: " + score, centerX(g, "Your Score: " + score), HEIGHT / 2 - 10);
        g.drawString("Highest Score: " + highestScore, centerX(g, "Highest Score: " + highestScore), HEIGHT / 2 + 20);
        g.drawString("Press 'R' to Restart", centerX(g, "Press 'R' to Restart"), HEIGHT / 2 + 50);
        g.drawString("Press 'ESC' to Quit", centerX(g, "Press 'ESC' to Quit"), HEIGHT / 2 + 80);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollision();
            keyPressedLock = false;  
        }
        repaint();
    }
}
