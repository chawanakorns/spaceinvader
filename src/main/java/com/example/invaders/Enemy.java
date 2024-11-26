package com.example.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Enemy {
    protected final List<Character> enemies = new ArrayList<>();
    protected final List<Rectangle> bullets = new ArrayList<>();
    protected final Random random = new Random();
    protected long lastShootTime = 0;
    protected int numRows;
    protected int numEnemiesPerRow;
    private final AudioClip PlayerDeathSound = new AudioClip(getClass()
            .getResource("/com/example/invaders/assets/explosion.wav").toString());
    private static final Logger logger = LoggerFactory.getLogger(Character.class);

    public Enemy(int numRows, int numEnemiesPerRow) {
        this.numRows = numRows;
        this.numEnemiesPerRow = numEnemiesPerRow;
    }

    public Enemy() {
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumEnemiesPerRow() {
        return numEnemiesPerRow;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public void setNumEnemiesPerRow(int numEnemiesPerRow) {
        this.numEnemiesPerRow = numEnemiesPerRow;
    }

    private void playPlayerDeathSound() {
        PlayerDeathSound.stop();
        PlayerDeathSound.play();
    }
    
    public void nextLevel(Pane root, Image spriteSheetImage) {
    	double spacingX = 48;
        double spacingY = 48;
        
    	double totalEnemiesWidth = numEnemiesPerRow * spacingX;
        double paneWidth = root.getPrefWidth();
        double initialX = (paneWidth - totalEnemiesWidth) / 2;
        double initialY = 150;

        for (int row = 0; row < numRows; row++) {
            boolean isStrongRow = random.nextBoolean(); // Determine if the row should contain strong enemies
            for (int i = 0; i < numEnemiesPerRow; i++) {
                if (isStrongRow) {
                    AnimatedSprite strongEnemyAnimatedSprite = new AnimatedSprite(spriteSheetImage, 6, 6, 1, 0, 0, 32, 32); // special enemies
                    strongEnemyAnimatedSprite.setX(initialX + i * spacingX);
                    strongEnemyAnimatedSprite.setY(initialY + row * spacingY);
                    strongEnemyAnimatedSprite.setFitWidth(48);
                    strongEnemyAnimatedSprite.setFitHeight(48);

                    root.getChildren().add(strongEnemyAnimatedSprite);

                    Character strongEnemyCharacter = new Character("", strongEnemyAnimatedSprite.getX(), strongEnemyAnimatedSprite.getY(), 64, 64);
                    strongEnemyCharacter.setImageView(strongEnemyAnimatedSprite);
                    strongEnemyCharacter.isStrongEnemy = true;
                    enemies.add(strongEnemyCharacter);
                } else {
                    AnimatedSprite enemyAnimatedSprite = new AnimatedSprite(spriteSheetImage, 2, 2, 1, 0, 0, 32, 32); // normal enemies
                    enemyAnimatedSprite.setX(initialX + i * spacingX);
                    enemyAnimatedSprite.setY(initialY + row * spacingY);
                    enemyAnimatedSprite.setFitWidth(48);
                    enemyAnimatedSprite.setFitHeight(48);

                    root.getChildren().add(enemyAnimatedSprite);

                    Character enemyCharacter = new Character("", enemyAnimatedSprite.getX(), enemyAnimatedSprite.getY(), 64, 64);
                    enemyCharacter.setImageView(enemyAnimatedSprite);
                    enemies.add(enemyCharacter);
                }
            }
        }
    }


    public List<Character> getEnemyCharacters() {
        return enemies;
    }
    public void shoot(Pane root, Character player, long currentTime) {
        if (!player.isDead) {
            if (!root.getChildren().contains(player.getImageView())) {
                root.getChildren().add(player.getImageView());
            }
            for (Character enemyCharacter : enemies) {
                if (enemyCharacter.getY() >= 720) {
                    player.isDead = true;
                }
                long shootingInterval = 1000;
                if (random.nextDouble() < 0.01 && (currentTime - lastShootTime) > shootingInterval) {
                    lastShootTime = currentTime;
                    Rectangle bullet = new Rectangle(4, 10, Color.WHITE);
                    bullet.setX(enemyCharacter.getX() + enemyCharacter.getImageView().getFitWidth() / 2);
                    bullet.setY(enemyCharacter.getY() + enemyCharacter.getImageView().getFitHeight());
                    bullets.add(bullet);

                    Platform.runLater(() -> root.getChildren().add(bullet));

                    new Thread(() -> {
                        while (bullet.getY() < 800) {
                            try {
                                Thread.sleep(20); // Adjust the bullet speed as needed
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(() -> {
                                if (!root.getChildren().contains(bullet)) {
                                    return; // Exit the loop if the bullet is removed
                                }
                                bullet.setY(bullet.getY() + 5); // Adjust the bullet speed as needed
                                if (!player.isDead && isColliding(bullet, player.getImageView())) {
                                    player.isDead = true;
                                    Platform.runLater(() -> root.getChildren().remove(player.getImageView()));
                                    playerDeathEffect(root, player.getImageView().getX(), player.getImageView().getY());
                                    root.getChildren().remove(bullet); // Remove the bullet immediately after collision
                                    bullets.remove(bullet);
                                    logger.warn("Player shot by {}", enemyCharacter);
                                    return; // Exit the loop once the collision occurs
                                }
                                if (bullet.getY() >= 800 || player.isDead) {
                                    root.getChildren().remove(bullet);
                                    bullets.remove(bullet);
                                    // Exit the loop if the bullet reaches the end or the player is dead
                                }
                            });
                        }
                    }).start();
                }
            }
        } else {
            for (Rectangle bullet : bullets) {
                root.getChildren().remove(bullet);
            }
            bullets.clear();
        }
    }
    public List<Rectangle> getBullets() {
        return bullets;
    }

    private ImageView createSprite(String imagePath, double x, double y, int width, int height) {
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView sprite = new ImageView(image);
        sprite.setX(x);
        sprite.setY(y);
        sprite.setFitWidth(width);
        sprite.setFitHeight(height);
        return sprite;
    }

    public boolean isColliding(Rectangle bullet, ImageView player) {
        return bullet.getBoundsInParent().intersects(player.getBoundsInParent());
    }
    public boolean isCircleColliding(Circle circle, Rectangle bullet) {
        double circleCenterX = circle.getCenterX();
        double circleCenterY = circle.getCenterY();
        double circleRadius = circle.getRadius();
        double bulletX = bullet.getX() + bullet.getWidth() / 2;
        double bulletY = bullet.getY() + bullet.getHeight() / 2;

        double distanceX = Math.abs(circleCenterX - bulletX);
        double distanceY = Math.abs(circleCenterY - bulletY);

        if (distanceX > (circleRadius + bullet.getWidth() / 2)) {
            return false;
        }
        if (distanceY > (circleRadius + bullet.getHeight() / 2)) {
            return false;
        }

        if (distanceX <= circleRadius || distanceY <= circleRadius) {
            return true;
        }

        double cornerDistanceSquared = Math.pow((distanceX - circleRadius), 2) + Math.pow((distanceY - circleRadius), 2);

        return cornerDistanceSquared <= Math.pow(circleRadius, 2);
    }
    
    public void playerDeathEffect(Pane root, double x, double y) {
        ImageView effect = createSprite("/com/example/invaders/assets/playerExplosion.png", x, y, 32, 32); // Assuming you have a player explosion image
        root.getChildren().add(effect);
        playPlayerDeathSound();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> root.getChildren().remove(effect)));
        timeline.play();
    }
}
