package org.example;

import javax.swing.JFrame;
import java.io.Serial;

public class GameFrame extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    protected GameFrame() {
        GamePanel panel = new GamePanel();
        this.add(panel);
        this.setTitle("snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}