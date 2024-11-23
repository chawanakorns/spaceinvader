package com.example.invaders;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.invaders.Launcher;

import javafx.embed.swing.JFXPanel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharacterTest {
    private Launcher launcher;
    
    @BeforeClass
    public static void initToolkit() {
        new JFXPanel();
    }

    @Before
    public void setUp() {
        launcher = new Launcher();
    }

    @Test
    public void CharacterShouldMoveLeftAfterMethodIsCalled() {
        launcher.spaceInvaderGame.getCharacter().setX(100);
        launcher.moveLeft();
        double newX = launcher.spaceInvaderGame.getCharacter().getX();
        assertEquals(95, newX, 0.001);
    }

    @Test
    public void CharacterShouldMoveRightAfterMethodIsCalled() {
        launcher.spaceInvaderGame.getCharacter().setX(100);
        launcher.moveRight();
        double newX = launcher.spaceInvaderGame.getCharacter().getX();
        assertEquals(100, newX, 0.001);
    }

    @Test
    public void CharacterCanShootWhileStandStill() {
        int initialBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        launcher.spaceInvaderGame.getCharacter().shoot(launcher.spaceInvaderGame.getRoot());
        int currentBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        // Assert that the number of bullets has increased after shooting
        assertTrue(currentBullets > initialBullets);
    }

    @Test
    public void CharacterCanShootWhileMovingLeft() {
        int initialBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        launcher.moveLeft();
        launcher.spaceInvaderGame.getCharacter().shoot(launcher.spaceInvaderGame.getRoot());
        int currentBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        assertTrue(currentBullets > initialBullets);
    }

    @Test
    public void CharacterCanShootWhileMovingRight() {
        int initialBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        launcher.moveRight();
        launcher.spaceInvaderGame.getCharacter().shoot(launcher.spaceInvaderGame.getRoot());
        int currentBullets = launcher.spaceInvaderGame.getCharacter().getBullets().size();
        assertTrue(currentBullets > initialBullets);
    }

    @Test
    public void ScoreShouldIncreaseWhenBulletHitsEnemy() {
        int initialScore = launcher.spaceInvaderGame.score;
        launcher.spaceInvaderGame.getCharacter().shoot(launcher.spaceInvaderGame.getRoot());
        launcher.spaceInvaderGame.updateScore(10);
        int currentScore = launcher.spaceInvaderGame.score;
        assertEquals(initialScore + 10, currentScore);
    }
    
    @Test
    public void ScoreShouldIncreaseWhenBulletHitsBoss() {
        int initialScore = launcher.spaceInvaderGame.score;
        launcher.spaceInvaderGame.getCharacter().shoot(launcher.spaceInvaderGame.getRoot());
        launcher.spaceInvaderGame.updateScore(50);
        int currentScore = launcher.spaceInvaderGame.score;
        assertEquals(initialScore + 50, currentScore);
    }
}
