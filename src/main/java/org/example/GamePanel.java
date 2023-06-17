package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.Serial;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int UNIT_SIZE = WIDTH / 20;
    private static final int NUMBER_OF_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    private final int[] x = new int[NUMBER_OF_UNITS];
    private final int[] y = new int[NUMBER_OF_UNITS];

    private int length = 2;
    private int foodEaten;
    private int foodX;
    private int foodY;
    private Directions direction = Directions.DOWN;
    private boolean running = false;
    private boolean canChangeDirection = true;
    private final Random random;
    private Timer timer;

    private JButton restartButton;
    private FontMetrics metrics;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        x[0] = WIDTH / 2;
        y[0] = HEIGHT / 2;
        setupRestartButton();
        play();
    }

    private void play() {
        addFood();
        running = true;
        timer = new Timer(80, this);
        timer.start();
    }

    private void setupRestartButton() {
        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        restartButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 25));
        metrics = restartButton.getFontMetrics(restartButton.getFont());
        int buttonWidth = metrics.stringWidth("Restart");
        int buttonHeight = metrics.getHeight();
        int buttonX = (WIDTH - buttonWidth) / 2;
        int buttonY = HEIGHT / 2 + metrics.getAscent() - buttonHeight / 2;
        restartButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        this.add(restartButton);
        restartButton.setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        drawBackground(graphics);
        draw(graphics);
    }

    private void drawBackground(Graphics graphics) {
        for (int i = 0; i < UNIT_SIZE; i++) {
            for (int j = 0; j < UNIT_SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    graphics.setColor(new Color(170, 215, 81));
                } else {
                    graphics.setColor(new Color(162, 209, 73));
                }
                graphics.fillRect(i * UNIT_SIZE, j * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    private void move() {
        for (int i = length; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (direction == Directions.LEFT) {
            x[0] = x[0] - UNIT_SIZE;
        } else if (direction == Directions.RIGHT) {
            x[0] = x[0] + UNIT_SIZE;
        } else if (direction == Directions.UP) {
            y[0] = y[0] - UNIT_SIZE;
        } else {
            y[0] = y[0] + UNIT_SIZE;
        }

        canChangeDirection = true;
    }

    private void checkFood() {
        if (x[0] == foodX && y[0] == foodY) {
            length++;
            foodEaten++;
            addFood();
        }
    }

    private void draw(Graphics graphics) {
        graphics.setColor(new Color(210, 115, 90));
        graphics.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

        graphics.setColor(Color.white);
        graphics.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

        for (int i = 1; i < length; i++) {
            graphics.setColor(new Color(40, 200, 150));
            graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }

        graphics.setColor(Color.white);
        graphics.setFont(new Font("JetBrains Mono", Font.PLAIN, 25));
        graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, graphics.getFont().getSize());

        if (!running) {
            gameOver(graphics);
        }
    }

    private void addFood() {
        foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void checkHit() {
        // check if head run into its body
        for (int i = length; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        // check if head run into walls
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (running) {
            move();
            checkFood();
            checkHit();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (canChangeDirection) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                    if (direction != Directions.RIGHT) {
                        direction = Directions.LEFT;
                        canChangeDirection = false;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                    if (direction != Directions.LEFT) {
                        direction = Directions.RIGHT;
                        canChangeDirection = false;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                    if (direction != Directions.DOWN) {
                        direction = Directions.UP;
                        canChangeDirection = false;
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                    if (direction != Directions.UP) {
                        direction = Directions.DOWN;
                        canChangeDirection = false;
                    }
                }
            }
        }
    }

    private void gameOver(Graphics graphics) {
        graphics.setColor(Color.red);
        graphics.setFont(new Font("JetBrains Mono", Font.PLAIN, 50));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);

        graphics.setColor(Color.white);
        graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, HEIGHT / 2 + 50);

        restartButton.setVisible(true);
    }

    private void restartGame() {
        length = 2;
        foodEaten = 0;
        direction = Directions.DOWN;
        running = false;
        x[0] = WIDTH / 2;
        y[0] = HEIGHT / 2;
        addFood();
        timer.stop();
        restartButton.setVisible(false);
        play();
        requestFocusInWindow();
        repaint();
    }
}
