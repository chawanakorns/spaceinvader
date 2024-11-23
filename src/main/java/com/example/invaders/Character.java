package com.example.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Character {
    private ImageView imageView;
    private double x;
    private double y;
    private List<ImageView> bullets = new ArrayList<>();
    public boolean isDead = false;
    public boolean canShoot = true;
    public boolean isStrongEnemy = false;
    private AudioClip shootingSound;
    private AudioClip enemyDeathSound;
    private static final Logger logger = LoggerFactory.getLogger(Character.class);
    int remainingPiercingBullets = 3;
    private boolean isPierce = false;
    private ImageView pierceLogo;
    public Character(String imagePath, double x, double y, double width, double height) {
        this.imageView = createSprite(imagePath, x, y, width, height);
        this.x = x;
        this.y = y;
        this.shootingSound = new AudioClip(getClass().getResource("/com/example/invaders/assets/shoot.wav").toString());
        this.enemyDeathSound = new AudioClip(getClass().getResource("/com/example/invaders/assets/invaderkilled.wav").toString());
    }
    //sound plays
    private void playEnemyDeathSound() {
        if (enemyDeathSound != null) {
            enemyDeathSound.play();
            logger.info("Playing enemy death sound.");
        }
    }
    private void playShootingSound() {
        if (shootingSound != null) {
            shootingSound.play();
        }
    }

    private ImageView createSprite(String imagePath, double x, double y, double width, double height) {
        InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream != null) {
            Image image = new Image(stream);
            ImageView imageView = new ImageView(image);
            imageView.setX(x);
            imageView.setY(y);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;
        } else {
            return null;
        }
    }

    // normal bullet
    public void shoot(Pane root) {
        isPierce = false;
        if (!isDead && canShoot) {
            ImageView bulletImageView = createSprite("/com/example/invaders/assets/bullet.png", this.getX() + 13.5, this.getY() - 20, 4, 12);
            if (bulletImageView != null) {
                root.getChildren().add(bulletImageView);
                bullets.add(bulletImageView);
                playShootingSound();
                logger.debug("Shooting action performed.");
            }
            canShoot = false;
        }
    }

    //pierce bullet
    public void shootPiercing(Pane root, MainGame mainGame) {
        isPierce = true;
        if (!isDead && canShoot && remainingPiercingBullets > 0) {
            ImageView bulletImageView = createSprite("/com/example/invaders/assets/playerIcon2.png", this.getX() + 13.5, this.getY() - 20, 5, 20);
            if (bulletImageView != null) {
                root.getChildren().add(bulletImageView);
                bullets.add(bulletImageView);
                playShootingSound();
                remainingPiercingBullets--;
                mainGame.updatePierceText(remainingPiercingBullets);
                logger.debug("Piercing bullet shooting action performed. Remaining piercing bullets: " + remainingPiercingBullets);
            }
            canShoot = false;
        }
    }

    public void updateBullets(Pane root, List<Character> enemies, MainGame mainGame) {
        List<ImageView> bulletsToRemove = new ArrayList<>();
        List<Character> enemiesToRemove = new ArrayList<>();
        boolean allBulletsRemoved = true;
        boolean hasEnemyDied = false;

        Random random = new Random();

        for (Iterator<ImageView> iterator = bullets.iterator(); iterator.hasNext(); ) {
            ImageView bullet = iterator.next();
            double newY = bullet.getY() - 10;
            bullet.setY(newY);

            if (newY < 0) {
                bulletsToRemove.add(bullet);
            } else {
                allBulletsRemoved = false;
                for (Iterator<Character> enemyIterator = enemies.iterator(); enemyIterator.hasNext(); ) {
                    Character enemy = enemyIterator.next();
                    if(isPierce){
                        if (isColliding(bullet, enemy.getImageView())) {
                            enemyIterator.remove();
                            enemiesToRemove.add(enemy);
                            if (enemy.isStrongEnemy()) {
                                mainGame.updateScore(true);
                            } else {
                                mainGame.updateScore(false);
                            }
                            hasEnemyDied = true;
                        }
                    }
                    else {
                        if (isColliding(bullet, enemy.getImageView())) {
                            bulletsToRemove.add(bullet);
                            enemyIterator.remove();
                            enemiesToRemove.add(enemy);
                            if (enemy.isStrongEnemy()) {
                                mainGame.updateScore(true);
                            } else {
                                mainGame.updateScore(false);
                            }
                            hasEnemyDied = true;
                        }
                    }
                }
            }
        }

        bullets.removeAll(bulletsToRemove);
        root.getChildren().removeAll(bulletsToRemove);

        for (Character enemy : enemiesToRemove) {
            root.getChildren().remove(enemy.getImageView());
            replaceEnemyWithEffect(root, enemy.getX(), enemy.getY());
            playEnemyDeathSound();
        }

        if (bullets.isEmpty() && allBulletsRemoved) {
            canShoot = true;
        }

        if (isDead) {
            root.getChildren().remove(imageView);
        }
    }


    private void replaceEnemyWithEffect(Pane root, double x, double y) {
        ImageView effect = createSprite("/com/example/invaders/assets/enemyExplosion_1.png", x, y, 48, 48);
        root.getChildren().add(effect);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.125), event -> root.getChildren().remove(effect)));
        timeline.play();
    }

    public boolean isColliding(ImageView bullet, ImageView enemy) {
        return bullet.getBoundsInParent().intersects(enemy.getBoundsInParent());
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (!isDead) {
            this.x = x;
            this.imageView.setX(x);
            logger.trace("Setting X position to: {}", x);
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (!isDead) {
            this.y = y;
            this.imageView.setY(y);
        }
    }

	public List<ImageView> getBullets() {
		return bullets;
	}
	
	public boolean isDead() {
        return isDead;
    }

    public void revive() {
        isDead = false;
        resetPosition(300, 720);
    }
    
    public void resetPosition(double x, double y) {
        this.x = x;
        this.y = y;
        imageView.setX(x);
        imageView.setY(y);
    }
    
    public boolean isStrongEnemy() {
        return isStrongEnemy;
    }

    public void setStrongEnemy(boolean isStrongEnemy) {
        this.isStrongEnemy = isStrongEnemy;
    }
    
    public boolean canMove() {
        return !isDead;
    }
    public void receivePiercingBullet() {
        remainingPiercingBullets++;
    }
}
