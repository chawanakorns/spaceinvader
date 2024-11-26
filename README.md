# Space Invaders

Welcome to **Space Invaders**, a JavaFX-based game where you pilot a spaceship, shoot down alien invaders, and aim for the highest score! Use your skills to defend the galaxy.

## Requirements

- **Java**: JDK 17 or higher
- **JavaFX**: Ensure JavaFX libraries are included in your project setup.
- **SLF4J**: Logging library is required for debug information.

## Setup and Launch

1. Clone or download this project.
2. Ensure the `assets` directory contains the following:
   - `playerIcon2.png`: Icon for the game window.
   - `spaceinvaders_logo.png`: Game logo for the main menu.
3. Build and run the project:
   - Run the `Launcher` class using your IDE.
   - Or, build the project into a JAR file and execute it.

## How to Play

### Controls

- **LEFT Arrow**: Move the spaceship left.
- **RIGHT Arrow**: Move the spaceship right.
- **SPACE Key**: Shoot standard lasers.
- **SHIFT Key**: Fire piercing lasers (special attack).

### Gameplay

1. **Start the Game**: 
   - Launch the game.
   - Press the "PRESS SPACE KEY TO START" button in the main menu.

2. **Objective**: 
   - Destroy the alien invaders before they reach your base or destroy your spaceship.

3. **Movement**: 
   - Use the LEFT and RIGHT arrow keys to navigate the spaceship.

4. **Shooting**: 
   - Press **SPACE** to shoot standard lasers.
   - Press **SHIFT** to fire piercing lasers that hit multiple enemies.

### Scoring

- Gain points by destroying aliens.
- Survive as long as possible to achieve the highest score.

### Game Over

- The game ends when your spaceship is destroyed or the invaders reach your base.

## Customization

### Modify Gameplay

- **Player Speed**: Update the `moveSpeed` value in the `Launcher` class.
- **Window Size**: Modify `widthSize` and `heightSize` for a custom resolution.
- **Assets**: Replace images in the `assets` folder to change icons or the game logo.

## Logs

- The game logs events like movement and shooting to the console using SLF4J. 
- Use the logs for debugging or analyzing gameplay.

## Known Issues

- Ensure all asset paths are correct and accessible in the `assets` directory.
- Verify JavaFX libraries are properly configured in your environment.

---

Enjoy saving the galaxy with **Space Invaders**! 🚀
