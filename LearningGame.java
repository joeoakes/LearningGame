import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Project: Learning game
 * Purpose Details: Make an educational game
 * Course: IST 242
 * Authors: Matthias Fischer, Alice Lkhagvasuren, Erin McDonald, Alvin Li, Robert Griffiths
 * Date Developed: 6/20/2024
 * Last Date Changed: 6/21/2024
 * Rev:2

 */

public class LearningGame extends JFrame implements KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 50;
    private JPanel gamePanel;
    private boolean isGameOver;
    private int playerX, playerY;
    private static final int PLAYER_SPEED = 10;
    private List<Obstacle> obstacles;
    private int obstacleSpeed;
    private static final int OBSTACLE_WIDTH = 20;
    private static final int OBSTACLE_HEIGHT = 20;
    private Timer timer;
    private int currentLevel;

    private static final int SQUARE = 0;
    private static final int CIRCLE = 1;
    private static final int TRIANGLE = 2;

    private static final String[] OBSTACLE_NAMES = {"Square", "Circle", "Triangle"};

    private Obstacle correctObstacle;

    public LearningGame() {
        setTitle("Learning Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        add(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);

        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - PLAYER_HEIGHT - 20;
        isGameOver = false;
        obstacles = new ArrayList<>();
        currentLevel = 1;
        obstacleSpeed = 3;

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver) {
                    update();
                    gamePanel.repaint();
                }
            }
        });
        timer.start();

        startNewLevel();
    }

    private void startNewLevel() {
        obstacles.clear(); // Clear any existing obstacles

        Random random = new Random();

        // Pick random x positions for each type of obstacle
        int minX = OBSTACLE_WIDTH / 2; // Minimum x position
        int maxX = WIDTH - OBSTACLE_WIDTH / 2; // Maximum x position

        // Square
        int squareX = minX + random.nextInt(maxX - minX);
        obstacles.add(new Obstacle(squareX, 0, SQUARE));

        // Circle
        int circleX = minX + random.nextInt(maxX - minX);
        obstacles.add(new Obstacle(circleX, 0, CIRCLE));

        // Triangle
        int triangleX = minX + random.nextInt(maxX - minX);
        obstacles.add(new Obstacle(triangleX, 0, TRIANGLE));

        // Set correct obstacle between the ones that exist
        correctObstacle = obstacles.get(random.nextInt(3));
    }

    private void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLUE);
        g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level: " + currentLevel, 10, 20);

        g.drawString("Correct Obstacle: " + OBSTACLE_NAMES[correctObstacle.type], 10, 40);

        for (Obstacle obstacle : obstacles) {
            int obstacleX = obstacle.x;
            int obstacleY = obstacle.y;
            int obstacleType = obstacle.type;

            switch (obstacleType) {
                case SQUARE:
                    g.setColor(Color.RED);
                    g.fillRect(obstacleX, obstacleY, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
                    break;
                case CIRCLE:
                    g.setColor(Color.GREEN);
                    g.fillOval(obstacleX, obstacleY, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
                    break;
                case TRIANGLE:
                    int[] triangleX = {obstacleX, obstacleX + OBSTACLE_WIDTH / 2, obstacleX + OBSTACLE_WIDTH};
                    int[] triangleY = {obstacleY + OBSTACLE_HEIGHT, obstacleY, obstacleY + OBSTACLE_HEIGHT};
                    g.setColor(Color.BLUE);
                    g.fillPolygon(triangleX, triangleY, 3);
                    break;
            }
        }

        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over!", WIDTH / 2 - 80, HEIGHT / 2);
        }
    }

    private void update() {
        if (!isGameOver) {
            // Move obstacles
            for (int i = 0; i < obstacles.size(); i++) {
                obstacles.get(i).y += obstacleSpeed;
                if (obstacles.get(i).y > HEIGHT) {
                    obstacles.remove(i);
                    i--;
                }
            }

            // Check collision with correct obstacle to start new level
            Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
            Rectangle correctShape = new Rectangle(correctObstacle.x, correctObstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
            if (playerRect.intersects(correctShape)) {
                currentLevel++;
                obstacleSpeed += 1; // Increase obstacle speed for next level
                startNewLevel();
            }

            // Check if player collided with any other obstacles (game over condition)
            for (Obstacle obstacle : obstacles) {
                Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
                if (playerRect.intersects(obstacleRect)) {
                    if (obstacle != correctObstacle) {
                        isGameOver = true;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LearningGame().setVisible(true);
            }
        });
    }

    private class Obstacle {
        int x, y;
        int type;

        public Obstacle(int x, int y, int type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }
}