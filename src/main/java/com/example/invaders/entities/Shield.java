package com.example.invaders.entities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public class Shield {
    private ImageView imageView;

    public Shield(String imagePath, double x, double y, double width, double height) {
        this.imageView = createSprite(imagePath, x, y, width, height);
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

    public ImageView getImageView() {
        return imageView;
    }

    public double getX() {
        return imageView.getX();
    }

    public double getY() {
        return imageView.getY();
    }

    public boolean isColliding(ImageView other) {
        return imageView.getBoundsInParent().intersects(other.getBoundsInParent());
    }
}
