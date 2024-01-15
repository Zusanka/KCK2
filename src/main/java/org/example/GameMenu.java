package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map;

public class GameMenu extends JPanel {
    private JButton startButton;
    private JButton changeColorButton;
    private SpikesGame gamePanel;
    private TreeMap<Long, String> highScores; // Mapa przechowująca wyniki: <Wynik, Gracz>
    private JTextField playerNameField;

    public GameMenu() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 400));

        setBackground(Color.WHITE);

        ImageIcon logoIcon = new ImageIcon("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/logo.png");
        JLabel logoLabel = new JLabel(logoIcon, SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        ImageIcon startIcon = new ImageIcon("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/start.png");

        startButton = new JButton(startIcon);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                setVisible(false);
                gamePanel.reset();
                gamePanel.startGame(playerName);
                gamePanel.setVisible(true);
                gamePanel.requestFocusInWindow();
            }
        });

        ImageIcon changeIcon = new ImageIcon("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/change.png");
        changeColorButton = new JButton(changeIcon);
        changeColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] colorNames = {"red", "blue", "black", "yellow", "white","cherry"};
                String[] colors = {"angry", "blue", "black", "yellow","white","big"};

                String selectedColorName = (String) JOptionPane.showInputDialog(
                        null,
                        "Choose Character Color",
                        "Color Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        colorNames,
                        colorNames[0]);

                if (selectedColorName != null) {
                    for (int i = 0; i < colorNames.length; i++) {
                        if (colorNames[i].equals(selectedColorName)) {
                            gamePanel.setPlayerColor(colors[i]);
                            break;
                        }
                    }
                    }
                }
        });

        highScores = new TreeMap<>(); // Inicjalizacja mapy z wynikami
        ImageIcon scoresIcon = new ImageIcon("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/scores.png");
        JButton highScoresButton = new JButton(scoresIcon);
        highScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHighScores();
            }
        });
        // Dodanie pola tekstowego do wprowadzania nazwy gracza
        playerNameField = new JTextField(10); // Ustawienie szerokości pola tekstowego
        JPanel playerNamePanel = new JPanel();
        playerNamePanel.setBackground(Color.WHITE);
        ImageIcon enterIcon = new ImageIcon("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/enter.png");
        playerNamePanel.add(new JLabel(enterIcon));
        playerNamePanel.add(playerNameField);

        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(startButton);
        buttonPanel.add(changeColorButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(playerNamePanel); // Dodanie panelu z polem tekstowym

        add(buttonPanel, BorderLayout.WEST);
        add(logoLabel, BorderLayout.NORTH);
    }
    void showHighScores() {
        if (highScores.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No high scores yet!");
            return;
        }

        StringBuilder scoresText = new StringBuilder("High Scores:\n");

        int counter = 1;
        for (Map.Entry<Long, String> entry : highScores.descendingMap().entrySet()) {
            scoresText.append(counter).append(". ").append(entry.getValue()).append(": ").append(entry.getKey()).append("\n");
            counter++;
            if (counter > 5) break; // Pokazuj tylko 5 najlepszych wyników
        }

        JOptionPane.showMessageDialog(null, scoresText.toString());
    }
    // Metoda dodająca wynik do listy
    public void addScore(long score, String playerName) {
        highScores.put(score, playerName);
    }
    public void setGamePanel(SpikesGame game) {
        this.gamePanel = game;
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Spikes Game");
        SpikesGame game = new SpikesGame();
        GameMenu menu = new GameMenu();

        game.setParentFrame(frame);
        game.setGameMenu(menu);
        menu.setGamePanel(game);

        frame.add(game);
        frame.add(menu, BorderLayout.WEST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();

        //dodane teraz
        game.setParentFrame(frame);
    }
}
