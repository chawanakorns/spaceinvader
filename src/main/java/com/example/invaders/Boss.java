package com.example.invaders;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Boss extends Enemy{
    private AnimatedSprite bossSprite;
    private double x;
    private double y;
    private double width;
    private double height;
    private double direction = 1;
    private long lastShootTime = 0;
    private long lastAdjustmentTime = 0;
    private boolean isAdjusted = false;
    private List<Rectangle> bullets = new ArrayList<>();

    public Boss(AnimatedSprite bossSprite, double x, double y, double width, double height) {
        this.bossSprite = bossSprite;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        bossSprite.setFitWidth(width);
        bossSprite.setFitHeight(height);
        bossSprite.setLayoutX(x);
        bossSprite.setLayoutY(y);
    }

    public AnimatedSprite getAnimatedSprite() {
        return bossSprite;
    }

    public void moveBoss(double deltaX, double gameWidth) {
        double speed = isAdjusted ? 2 * deltaX : deltaX; // Adjust the speed during rapid shooting
        if (x <= 0) {
            direction = 1;
        } else if (x + width >= gameWidth) {
            direction = -1;
        }
        this.x += speed * direction;
        bossSprite.setLayoutX(x);
    }
    
    public boolean isColliding(ImageView bullet) {
        return bossSprite.getBoundsInParent().intersects(bullet.getBoundsInParent());
    }

    public void bossShoot(Pane root, Character player) {
        long currentTime = System.currentTimeMillis();
        if (!player.isDead) {
            if (currentTime - lastShootTime > (isAdjusted ? 50 : 300)) {
                Rectangle bullet = new Rectangle(5, 20, Color.RED);
                bullet.setLayoutX(x + width / 2 - bullet.getWidth() / 2);
                bullet.setLayoutY(y + height);
                root.getChildren().add(bullet);
                bullets.add(bullet);

                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(5), event -> {
                    bullet.setLayoutY(bullet.getLayoutY() + 3);

                    if (!player.isDead && bullet.getBoundsInParent().intersects(player.getImageView().getBoundsInParent())) {
                        if (!player.isDead) {
                            player.isDead = true;
                            playerDeathEffect(root, player.getImageView().getX(), player.getImageView().getY());
                            for (Rectangle b : bullets) {
                                root.getChildren().remove(b);
                            }
                            bullets.clear();
                        }
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
                lastShootTime = currentTime;

                if (currentTime - lastAdjustmentTime < 3000) { // Shooting at normal speed for 3 seconds
                    isAdjusted = false;
                } else if (currentTime - lastAdjustmentTime < 3500) { // Rapid shooting for 1 second
                    isAdjusted = true;
                } else {
                    lastAdjustmentTime = currentTime; // Reset the lastAdjustmentTime
                }
            }
        }
    }
    public void removeBullet(Rectangle bullet) {
        bullets.remove(bullet);
    }
}
