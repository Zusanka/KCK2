package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;

public class SpikesGame extends JPanel implements ActionListener, KeyListener {
    private long startScore = 0;
    private long Score = 0;
    private long startTime;
    private JLabel scoreLabel;
    private GameMenu gamePanel; // Deklaracja zmiennej gamePanel
    private final int screenWidth = 400; //szerokosc okna
    private final int screenHeight = 500; //wysokosc okna
    private BufferedImage birdImage;
    private BufferedImage grassImage;
    private BufferedImage greenImage;
    private BufferedImage goldImage;
    private BufferedImage pigImage;
    private BufferedImage bonusImage;
    private BufferedImage spikeImage;
    private BufferedImage backImage;
    private BufferedImage boomImage;
    private BufferedImage longSpikeImage;
    private BufferedImage upImage;
    private final int playerWidth = 40;
    private final int playerHeight = 40;
    private final int spikeWidth = 10;
    private final int spikeHeight = 10;
    private final int bonusWidth = 5;
    private final int bonusHeight = 5;
    private int playerX = screenWidth / 2;
    private int playerY = screenHeight - playerHeight - 10;
    private int spikeSpeed = 5;
    private boolean leftKeyPressed = false;
    private boolean rightKeyPressed = false;
    private boolean isPaused = false;
    private List<Spikes> spikesList = new ArrayList<>();
    private List<Bonus> bonusList = new ArrayList<>();
    private Timer gameTimer;
    private long lastBonusTime = 0;
    private final int bonusAppearInterval = 5; // Interval co ile punktów ma pojawiać się bonus
    private final int bonusDisappearTime = 10; // Czas w sekundach, po którym bonus znika
    private boolean bonusOnScreen = false;

    public SpikesGame() {
        try {
            birdImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/angry.png"));
            grassImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/maitai.png"));
            pigImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/badpig.png"));
            bonusImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/bonus.png"));
            spikeImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/spike.png"));
            backImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/back.png"));
            boomImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/boom.png"));
            greenImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/green.png"));
            goldImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/gold.png"));
            longSpikeImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/longspike.png"));
            upImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/up.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.WHITE);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        try{
            scoreLabel = new JLabel(new ImageIcon(ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/score.png"))));
        }catch (IOException e){
            e.printStackTrace();
        }
        add(scoreLabel);

        gameTimer = new Timer(50, this);
    }
    private String playerName;
    public void startGame(String playerName) {
        this.playerName = playerName;
        startScore = 0;
        Score = 0;
        startTime = System.currentTimeMillis();
        gameTimer.start();
        initializeSpikes();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) {
            movePlayer();
            moveSpikes();

            checkCollision();
            updateScoreLabel();
            handleBonusAppearance();
            repaint();
        }
    }
    private void handleBonusAppearance() {
        long currentTime = System.currentTimeMillis() / 1000; // Aktualny czas w sekundach

        if (startScore % bonusAppearInterval == 0 && startScore > 0 && currentTime - lastBonusTime >= bonusDisappearTime) {
            addBonus();
            lastBonusTime = currentTime;
            bonusOnScreen = true;
        }

        for (int i = 0; i < bonusList.size(); i++) {
            Bonus bonus = bonusList.get(i);
            if (currentTime - bonus.getAppearTime() >= bonusDisappearTime) {
                bonusList.remove(i);
                bonusOnScreen = false;
                repaint();
            }
        }
    }

    private void movePlayer() {
        if (leftKeyPressed && playerX > 0) {
            playerX -= 5;
        }
        if (rightKeyPressed && playerX < screenWidth - playerWidth) {
            playerX += 5;
        }
    }
    private void moveSpikes() {
        for (Spikes spike : spikesList) {
            spike.y += spikeSpeed + (startScore/5);
            if (spike.y > screenHeight) {
                spike.y = -spikeHeight;
                spike.x = new Random().nextInt(screenWidth - spikeWidth);
            }
        }
    }
    private boolean collisionDetected = false;
    private void checkCollision() {
        for (Spikes spike : spikesList) {
            if (playerX < spike.x + spikeWidth && playerX + playerWidth > spike.x &&
                    playerY < spike.y + spikeHeight && playerY + playerHeight > spike.y) {
                collisionDetected = true;
                endGame();
            }
        }
        for (int i = 0; i < bonusList.size(); i++) {
            Bonus bonus = bonusList.get(i);
            if (playerX < bonus.x + bonusWidth && playerX + playerWidth > bonus.x &&
                    playerY < bonus.y + bonusHeight && playerY + playerHeight > bonus.y) {
                bonusList.remove(i);
                Score += 2;
                updateScoreLabel();
                break;
            }
        }
    }
    private JFrame parentFrame;
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }
    private GameMenu gameMenu;
    public void setGameMenu(GameMenu menu) {
        this.gameMenu = menu;
    }
    private void endGame() {
        gameTimer.stop();
        long endTime = System.currentTimeMillis();
        long timeSurvived = (endTime - startTime) / 1000; // Calculate time survived in seconds
        int choice = JOptionPane.showConfirmDialog(this, "Game Over! Your score: " +
                 + (startScore+Score) + " \nReturn to Main Menu?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            parentFrame.getContentPane().getComponent(1).setVisible(true);
            this.setVisible(false);
            parentFrame.requestFocusInWindow();

            gameMenu.addScore(startScore, playerName); // Dodaj wynik do listy najlepszych wyników
            gamePanel.showHighScores(); //Zaktualizuj wyświetlanie najlepszych wyników
            gamePanel.repaint();
            gamePanel.setVisible(true); //panel gry jest widoczny
            gamePanel.requestFocusInWindow(); //focus na panelu gry
        }
        else {
            JOptionPane.showMessageDialog(this, "You left the game");
        }
        reset();
    }
    void reset() {
        spikesList.clear();
        bonusList.clear();
        playerX = screenWidth / 2;
        playerY = screenHeight - playerHeight - 10;
        startScore = 0;
        scoreLabel.setText("Score: " + startScore);
        //Zresetowanie stanu klawiszy i flagi kolizji
        leftKeyPressed = false;
        rightKeyPressed = false;
        collisionDetected = false;
        //ponowne zainicjalizowanie elementów gry
        initializeSpikes();
        //odświeżenie ekranu
        repaint();
    }
    private void updateScoreLabel() {
        long currentTime = System.currentTimeMillis();
        int timeSurvived = (int) ((currentTime - startTime) / 1000); // Czas przetrwania w sekundach
            startScore = timeSurvived;
            scoreLabel.setText(" " +(startScore+Score));
    }
    public void setPlayerColor(String image) {
        try {
            birdImage = ImageIO.read(new FileInputStream("/Users/Kalinka/Downloads/SpikesSwing/src/main/resources/"+image+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(upImage, 500, 244, null);
        g.drawImage(grassImage, 500, 144, null);
        g.drawImage(grassImage, 0, 144, null);
        g.drawImage(pigImage, 470, 307, null);
        g.drawImage(longSpikeImage, 640, 360, null);
        g.drawImage(greenImage, 490, 433, null);
        g.drawImage(birdImage,playerX,playerY, playerWidth,playerHeight,null);
        g.drawImage(goldImage, 70, 467, null);
        g.drawImage(longSpikeImage, 680, 400, null);

        for (Spikes spike : spikesList) {
                g.drawImage(spikeImage, spike.x, spike.y, null);
        }
        for (Bonus bonus : bonusList) {
            g.drawImage(bonusImage,bonus.x,bonus.y, playerWidth,playerHeight,null);
        }

        if(collisionDetected){
            int boomY = (500 - boomImage.getHeight())/2;
            g.drawImage(boomImage, 10, boomY, null);
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKeyPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKeyPressed = true;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isPaused = !isPaused;
            if (isPaused) {
                gameTimer.stop();
                JOptionPane.showMessageDialog(this, "Game Paused");
            } else {
                gameTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftKeyPressed = false;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightKeyPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this case
    }
    private class Spikes {
        private int x;
        private int y;
        public Spikes(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private class Bonus {
        private int x;
        private int y;
        private long appearTime; // Czas pojawienia się bonusu
        public Bonus(int x, int y) {
            this.x = x;
            this.y = y;
            this.appearTime = System.currentTimeMillis() / 1000; // Ustalenie czasu pojawienia się bonusu
        }
        public long getAppearTime(){
            return appearTime;
        }
    }
    private void initializeSpikes() {
        Random random = new Random();
        int gap = 100; // odstep miedzy kolcami
        for (int i = 0; i < screenHeight; i+= gap) {
            int x = random.nextInt(screenWidth - spikeWidth);
            spikesList.add(new Spikes(x, -i));
        }
    }
    public void addBonus() {
        Random random = new Random();
        int x = random.nextInt(screenWidth - bonusWidth);
        int y = screenHeight - playerHeight - bonusHeight;
        bonusList.add(new Bonus(x, y));
    }
}
