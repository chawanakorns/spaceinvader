package com.example.invaders;

import com.example.invaders.controller.MainGame;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher extends Application {
    private Stage primaryStage;
    public MainGame spaceInvaderGame = new MainGame();
    private int moveSpeed = 5;
    public boolean movingLeft = false;
    public boolean movingRight = false;
    public boolean shooting = false;
    public boolean pierceShooting = false;
    private int widthSize = 600;
    private int heightSize = 800;
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    
    String buttonStyle = "-fx-font-size: 20px; -fx-min-width: 150px; -fx-min-height: 50px; -fx-text-fill: green; -fx-background-color: transparent; -fx-border-color: transparent; -fx-font-weight: bold;";
    String hoverButtonStyle = "-fx-font-size: 20px; -fx-min-width: 150px; -fx-min-height: 50px; -fx-text-fill: darkgreen; -fx-background-color: transparent; -fx-border-color: transparent;";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/invaders/assets/playerIcon2.png")));
        showMainMenu();
    }

    private void showMainMenu() {
    	// primary scene
        primaryStage.setTitle("Space Invaders");
        VBox menuLayout = new VBox(10);
        menuLayout.setStyle("-fx-background-color: black; -fx-alignment: center;");
        Scene menuScene = new Scene(menuLayout, widthSize, heightSize);
        
        // game logo
        Image logoImage = new Image(getClass().getResourceAsStream("/com/example/invaders/assets/spaceinvaders_logo.png"));
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(400);
        logoImageView.setFitHeight(500);
        StackPane.setAlignment(logoImageView, Pos.TOP_CENTER);

        // start button
        Button startButton = new Button("PRESS SPACE KEY TO START");
        startButton.setStyle(buttonStyle);
        startButton.setOnAction(event -> {
            startGame();
        });
        startButton.setOnMouseEntered(e -> startButton.setStyle(hoverButtonStyle));
        startButton.setOnMouseExited(e -> startButton.setStyle(buttonStyle));

        menuLayout.getChildren().addAll(logoImageView, startButton);
        
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // in game
    public void startGame() {
        primaryStage.setTitle("Space Invaders");
        Scene scene = new Scene(spaceInvaderGame.createContent());
        primaryStage.setResizable(false);

        scene.setOnKeyPressed(e -> {
            if (!spaceInvaderGame.isDead()) {
                switch (e.getCode()) {
                    case LEFT:
                        movingLeft = true;
                        break;
                    case RIGHT:
                        movingRight = true;
                        break;
                    case SPACE:
                        shooting = true;
                        pierceShooting = false;
                        break;
                    case SHIFT:
                        pierceShooting = true;
                        shooting = false;
                        break;
                }
            }
        });

        // Stop moving action
        scene.setOnKeyReleased(e -> {
            if (!spaceInvaderGame.isDead()) {
                switch (e.getCode()) {
                    case LEFT:
                        movingLeft = false;
                        break;
                    case RIGHT:
                        movingRight = false;
                        break;
                    case SPACE:
                    	shooting = false;
                    	break;
                    case SHIFT:
                        pierceShooting = false;
                        break;
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        spaceInvaderGame.setMaxX(primaryStage.getWidth() - spaceInvaderGame.getCharacter().getImageView().getFitWidth());

        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (movingLeft) {
                    moveLeft();
                }
                if (movingRight) {
                    moveRight();
                }
                if (shooting) {
                	shoot();
                }
                if(pierceShooting) {
                    shootPierce();
                }
            }
        }.start();
    }
    
    public void moveLeft() {
        double newXLeft = spaceInvaderGame.getCharacter().getX() - moveSpeed;
        if (newXLeft >= spaceInvaderGame.getMinX()) {
            spaceInvaderGame.getCharacter().setX(newXLeft);
            logger.info("Moving left. New X position: {}", newXLeft);
        }
    }

    public void moveRight() {
        double newXRight = spaceInvaderGame.getCharacter().getX() + moveSpeed;
        if (newXRight <= spaceInvaderGame.getMaxX()) {
            spaceInvaderGame.getCharacter().setX(newXRight);
            logger.info("Moving right. New X position: {}", newXRight);
        }
    }
    public void shoot() {
    	spaceInvaderGame.getCharacter().shoot(spaceInvaderGame.getRoot());
    }
    public void shootPierce() { spaceInvaderGame.getCharacter().shootPiercing(spaceInvaderGame.getRoot(), spaceInvaderGame);}

    public static void main(String[] args) {
        launch(args);
    }
}
