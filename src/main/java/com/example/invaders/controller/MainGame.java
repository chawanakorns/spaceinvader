package com.example.invaders.controller;

import java.util.*;

import com.example.invaders.entities.Boss;
import com.example.invaders.entities.Character;
import com.example.invaders.entities.Enemy;
import com.example.invaders.model.AnimatedSprite;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainGame {
    private Pane root = new Pane();
    private Character player;
    private double t = 0;
    private final double MIN_X = 0;
    private double MAX_X;
    private double direction = 1;
    private double enemySpeed = 15;
    private Enemy enemy;
    private Timeline timeline;
    public int score = 0;
    private Text scoreText = new Text("Score: 0");
    private Text gameOverText = new Text("Game Over!");
    private Boss boss;
    private boolean isDead = false;
    private Button closeButton = new Button("Close Game");
    private int playerLives = 3;
    private int pierceBullets = 3;
    private List<ImageView> lifeImages = new ArrayList<>();
    private List<Circle> shields = new ArrayList<>();
    private AudioClip bossDeathSound;
    private AudioClip enemyMoveSound;
    private MediaPlayer bossMoveSound;
    private ImageView pierceLogo;
    private Text pierceText;
    private static final Logger logger = LoggerFactory.getLogger(Character.class);

    // set entities
    public MainGame() {
        player = new Character("/com/example/invaders/assets/playerIcon2.png", 300, 720, 32, 32);
        enemy = new Enemy(2,5);
        this.bossDeathSound = new AudioClip(getClass().getResource("/com/example/invaders/assets/invaderkilled.wav").toString());
        this.enemyMoveSound = new AudioClip(getClass().getResource("/com/example/invaders/assets/invadermove1.wav").toString());
        this.bossMoveSound = new MediaPlayer(new Media(getClass().getResource("/com/example/invaders/assets/bossmove.wav").toString()));
        bossMoveSound.setCycleCount(MediaPlayer.INDEFINITE);
    }
    // update pierce text
    public void updatePierceText(int remainingPiercingBullets) {
        this.pierceText.setText(Integer.toString(remainingPiercingBullets));
    }

    // generate all objects in-game
    public Parent createContent() {
        root.setPrefSize(600, 800);
        BackgroundFill backgroundFill = new BackgroundFill(Color.BLACK, null, null);
        Background background = new Background(backgroundFill);
        root.setBackground(background);
        root.getChildren().add(player.getImageView());
        Image spriteSheetImage = new Image(getClass().getResourceAsStream("/com/example/invaders/assets/invaders_sheet.v4.png"));
        enemy.nextLevel(root, spriteSheetImage);

        timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> moveEnemies()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // score text
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreText.setX(10);
        scoreText.setY(30);
        root.getChildren().add(scoreText);

        // life display
        for (int i = 0; i < playerLives; i++) {
            ImageView lifeImage = new ImageView(new Image(getClass().getResourceAsStream("/com/example/invaders/assets/playerIcon2.png")));
            lifeImage.setFitWidth(32);
            lifeImage.setFitHeight(32);
            lifeImage.setLayoutX(475 + i * 40);
            lifeImage.setLayoutY(10);
            lifeImages.add(lifeImage);
            root.getChildren().add(lifeImage);
        }

        // pierce bullet display
        Image logoImage = new Image(getClass().getResourceAsStream("/com/example/invaders/assets/playerIcon2.png"));
        ImageView pierceLogo = new ImageView(logoImage);
        pierceLogo.setFitWidth(10);
        pierceLogo.setFitHeight(32);
        pierceLogo.setLayoutX(15);
        pierceLogo.setLayoutY(root.getPrefHeight() - 55);
        root.getChildren().add(pierceLogo);

        pierceText = new Text(Integer.toString(player.remainingPiercingBullets));
        pierceText.setFill(Color.WHITE);
        pierceText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pierceText.setX(40);
        pierceText.setY(root.getPrefHeight() - 30);
        root.getChildren().add(pierceText);

        addShields();

        AnimationTimer timer = new AnimationTimer() {
            private boolean isReviving = false;
            private long reviveStartTime = 0;

            @Override
            public void handle(long l) {
                update();
                player.updateBullets(root, enemy.getEnemyCharacters(), MainGame.this);
                for (Character enemyCharacter : enemy.getEnemyCharacters()) {
                    ((AnimatedSprite) enemyCharacter.getImageView()).tick();
                }

                if (!isReviving && player.isDead()) {
                    isReviving = true;
                    reviveStartTime = System.currentTimeMillis();
                }

                if (isReviving) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - reviveStartTime >= 1000) { // 1000 milliseconds delay for revival
                        if (playerLives > 1) {
                            playerLives--;
                            root.getChildren().remove(lifeImages.get(playerLives));
                            lifeImages.remove(playerLives);
                            player.revive();
                            root.getChildren().add(player.getImageView());
                        } else {
                        	root.getChildren().removeAll(lifeImages);
                            bossMoveSound.stop();
                            timeline.stop();
                            this.stop();
                            gameOverText.setFill(Color.RED);
                            gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 48));
                            gameOverText.setX(root.getPrefWidth() / 2 - 140);
                            gameOverText.setY(root.getPrefHeight() / 2);
                            root.getChildren().add(gameOverText);

                            // Creating a SequentialTransition for the text to appear gradually
                            SequentialTransition sequentialTransition = new SequentialTransition();
                            String fullText = gameOverText.getText();

                            for (int i = 0; i < fullText.length(); i++) {
                                final int index = i;
                                PauseTransition pauseTransition = new PauseTransition(Duration.millis(25 * i));
                                pauseTransition.setOnFinished(event -> {
                                    gameOverText.setText(fullText.substring(0, index + 1));
                                });
                                sequentialTransition.getChildren().add(pauseTransition);
                            }

                            sequentialTransition.play();

                            Button restartButton = new Button("Restart Game");
                            restartButton.setLayoutX(root.getPrefWidth() / 2 - 50);
                            restartButton.setLayoutY(root.getPrefHeight() / 2 + 50);
                            restartButton.setVisible(true);
                            restartButton.setOnAction(event -> {
                                restartGame();
                            });
                            root.getChildren().add(restartButton);
                        }
                        isReviving = false;
                    }
                }

                if (enemy.getEnemyCharacters().isEmpty() && boss == null) {
                    boss = createBoss();
                    if (boss != null) {
                        root.getChildren().add(boss.getAnimatedSprite());
                    }
                } else if (boss != null) {
                    boss.moveBoss(3, root.getPrefWidth());
                    playBossMoveSound();
                    boss.bossShoot(root, player);
                    for (ImageView bullet : player.getBullets()) {
                        if (boss.isColliding(bullet)) {
                        	root.getChildren().remove(bullet);
                            handleBossDefeat();
                            playBossDeathSound();

                            Text gotLife = new Text("+1 Life");
                            gotLife.setFill(Color.WHITE);
                            gotLife.setX(root.getWidth() / 2 - 25);
                            gotLife.setY(root.getHeight() - 680);
                            root.getChildren().add(gotLife);

                            Text gotPierce = new Text("+1 Pierce Ammo");
                            gotPierce.setFill(Color.WHITE);
                            gotPierce.setX(root.getWidth() / 2 - 50);
                            gotPierce.setY(root.getHeight() - 700);
                            root.getChildren().add(gotPierce);
                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), evt -> {
                                gotPierce.setVisible(false);
                                gotLife.setVisible(false);
                            }));
                            timeline.setCycleCount(1);
                            timeline.play();


                            Text bossDefeatedText = new Text("Boss Defeated!");
                            bossDefeatedText.setFill(Color.GREEN);
                            bossDefeatedText.setFont(Font.font("Arial", FontWeight.BOLD, 36));
                            bossDefeatedText.setX(root.getPrefWidth() / 2 - 135);
                            bossDefeatedText.setY(root.getPrefHeight() / 2);
                            root.getChildren().add(bossDefeatedText);

                            // Creating a SequentialTransition for the text to appear gradually
                            SequentialTransition sequentialTransition = new SequentialTransition();
                            String fullText = bossDefeatedText.getText();

                            for (int i = 0; i < fullText.length(); i++) {
                                final int index = i;
                                PauseTransition pauseTransition = new PauseTransition(Duration.millis(25 * i));
                                pauseTransition.setOnFinished(event -> {
                                    bossDefeatedText.setText(fullText.substring(0, index + 1));
                                });
                                sequentialTransition.getChildren().add(pauseTransition);
                            }
                            SequentialTransition reverseSequentialTransition = new SequentialTransition();
                            for (int i = fullText.length(); i >= 0; i--) {
                                final int index = i;
                                PauseTransition pauseTransition = new PauseTransition(Duration.millis(25 * (fullText.length() - i)));
                                pauseTransition.setOnFinished(event -> {
                                    bossDefeatedText.setText(fullText.substring(0, index));
                                });
                                reverseSequentialTransition.getChildren().add(pauseTransition);
                            }

                            sequentialTransition.setOnFinished(event -> reverseSequentialTransition.play());
                            sequentialTransition.play();
                        }
                    }
                }
            }
        };
        timer.start();
        return root;
    }
    private void playBossDeathSound() {
        if (bossDeathSound != null) {
            bossDeathSound.play();
        }
    }
    private void playEnemyMoveSound() {
        if (enemyMoveSound != null) {
            enemyMoveSound.play();
        }
    }

    private void playBossMoveSound() {
        if (bossMoveSound != null) {
            if (bossMoveSound.getStatus() != MediaPlayer.Status.PLAYING) {
                bossMoveSound.play();
            }
        }
    }

    // update tick
    private void update() {
        t += 0.016;
        long currentTime = System.currentTimeMillis();
        enemy.shoot(root, player, currentTime);
        handleEnemyBulletShieldCollisions();
        updateShieldPositions();
    }

    private void handleEnemyBulletShieldCollisions() {
        Iterator<Circle> shieldIterator = shields.iterator();
        while (shieldIterator.hasNext()) {
            Circle shield = shieldIterator.next();
            for (Character enemyCharacter : enemy.getEnemyCharacters()) {
                Iterator<Rectangle> bulletIterator = enemy.getBullets().iterator();
                while (bulletIterator.hasNext()) {
                    Rectangle bullet = bulletIterator.next();
                    if (enemy.isCircleColliding(shield, bullet)) {
                        root.getChildren().remove(shield);
                        shieldIterator.remove();
                        root.getChildren().remove(bullet);
                        bulletIterator.remove();
                    }
                }
            }
        }
    }

    private void updateShieldPositions() {
        for (Circle shield : shields) {
            shield.setCenterX(player.getImageView().getX() + 15);
            shield.setCenterY(player.getImageView().getY() + 15);
        }
    }

    // enemy movement logic
    private void moveEnemies() {
        boolean shouldChangeDirection = false;

        for (Character enemyCharacter : enemy.getEnemyCharacters()) {
            double newX = enemyCharacter.getX() + (enemySpeed * direction);
            enemyCharacter.setX(newX);
            playEnemyMoveSound();

            if (newX >= (MAX_X) || newX <= MIN_X) {
                shouldChangeDirection = true;
            }
        }

        if (shouldChangeDirection) {
            direction *= -1; // Reverse the direction when hitting the border
            for (Character enemyCharacter : enemy.getEnemyCharacters()) {
                enemyCharacter.setY(enemyCharacter.getY() + 50); // Move the enemies down
            }
        }
    }

    // character getter
    public Character getCharacter() {
        return player;
    }

    // minimum x-axis getter
    public double getMinX() {
        return MIN_X;
    }

    // dead checking boolean
    public boolean isDead() {
        return false;
    }

    // maximum x-axis getter
    public double getMaxX() {
        return MAX_X;
    }

    // maximum x-axis setter
    public void setMaxX(double maxX) {
        this.MAX_X = maxX - player.getImageView().getFitWidth();
    }

    // pane getter
    public Pane getRoot() {
        return root;
    }

    // update score text
    public void updateScore(int increment) {
        score += increment;
        scoreText.setText("Score: " + score);
        logger.warn("Score updated by: {}", increment);
    }

    // enemy type score
    public void updateScore(boolean isStrongEnemy) {
        if (isStrongEnemy) {
            updateScore(20); // Score for strong enemies
        } else {
            updateScore(10); // Score for regular enemies
        }
    }

    // boss objects
    private Boss createBoss() {
    	if(!isDead) {
    		Image bossImage = new Image(getClass().getResourceAsStream("/com/example/invaders/assets/bossSprite.png")); // Change the path accordingly
    		AnimatedSprite animatedBoss = new AnimatedSprite(bossImage, 1, 1, 1, 0, 0, 49, 21); // Adjust the parameters accordingly
    		boss = new Boss(animatedBoss, -300, 100, 64, 36); // Adjust the position and size accordingly
    		return boss;
    	}
        return null;
    }

    // bullet collide boss and score update for boss
    private void handleBossDefeat() {
        //update boss state
        updateDefeatBossState();
        stopBossSound();

        // Add buff to player
        updatePlayerBuffs();

        // Score
        updateScore(200);

        // Update Ui
        updatePierceText(player.remainingPiercingBullets);

        // delay enemy spawn
        scheduleEnemySpawn();
    }

    private void updatePlayerBuffs() {
        player.receivePiercingBullet();
        updatePlayerLives();
        removeShields();
        addShields();
    }


    private void scheduleEnemySpawn() {
        if (boss == null && enemy.getEnemyCharacters().isEmpty()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> spawnNextEnemySet());
                    isDead = false;
                }
            }, 1000);
        }
    }

    private void updateDefeatBossState() {
        root.getChildren().remove(boss.getAnimatedSprite());
        boss = null;
        isDead = true;
    }

    private void stopBossSound() {
        if (bossMoveSound != null) {
            bossMoveSound.stop();
        }
    }


    private void updatePlayerLives() {
        if (playerLives < 6) {
            playerLives++;
            ImageView newLifeImage = new ImageView(new Image(getClass().getResourceAsStream("/com/example/invaders/assets/playerIcon2.png")));
            newLifeImage.setFitWidth(32);
            newLifeImage.setFitHeight(32);
            if (playerLives <= 3) {
                newLifeImage.setLayoutX(475 + (playerLives - 1) * 40);
                newLifeImage.setLayoutY(10);
            } else {
                int row = (playerLives - 1) / 3;
                int col = (playerLives - 1) % 3;
                newLifeImage.setLayoutX(475 + col * 40);
                newLifeImage.setLayoutY(10 + row * 40);
            }
            lifeImages.add(newLifeImage);
            root.getChildren().add(newLifeImage);
        }
    }

    // spawn next set of enemies
    private void spawnNextEnemySet() {
        Image spriteSheetImage = new Image(getClass().getResourceAsStream("/com/example/invaders/assets/invaders_sheet.v4.png")); // Provide the path to the next set of enemies
        enemy.nextLevel(root, spriteSheetImage);
        direction = 1; // Set the direction to move right initially
        for (Character enemyCharacter : enemy.getEnemyCharacters()) {
            enemyCharacter.setY(enemyCharacter.getY() + 50); // Move the new set of enemies down
        }
        timeline.play();
    }
    private void restartGame() {
        score = 0;
        scoreText.setText("Score: " + score);
        isDead = false;
        boss = null;
        root.getChildren().clear();
        lifeImages.clear();
        player.getBullets().clear();
        player = new Character("/com/example/invaders/assets/playerIcon2.png", 300, 720, 32, 32);
        playerLives = 3;

        for (Character enemyCharacter : enemy.getEnemyCharacters()) {
            root.getChildren().remove(enemyCharacter.getImageView());
        }
        enemy.getEnemyCharacters().clear();
        for (ImageView bullet : player.getBullets()) {
            root.getChildren().remove(bullet);
        }
        createContent();
    }
    private void addShields() {
        double shieldX = player.getImageView().getX() + player.getImageView().getFitWidth() / 2;
        double shieldY = player.getImageView().getY() + player.getImageView().getFitHeight() / 2;
        Circle shieldCircle = new Circle(shieldX, shieldY, 30, Color.BLUE);
        shieldCircle.setFill(null);
        shieldCircle.setStroke(Color.BLUE);
        root.getChildren().add(shieldCircle);
        shields.add(shieldCircle);
    }
    private void removeShields() {
        for (Circle shield : shields) {
            root.getChildren().remove(shield);
        }
        shields.clear();
    }
}
