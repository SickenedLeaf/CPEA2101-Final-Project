package com.pushknight.views;

import com.pushknight.controllers.GameController;
import com.pushknight.entities.Character;
import com.pushknight.utils.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * View class for rendering the game HUD (Heads-Up Display).
 * Displays player health, score, wave number, and other game information.
 */
public class GameView {
    private GameController gameController;
    private GraphicsContext gc;
    
    // HUD positioning constants
    private static final int HUD_PADDING = 10;
    private static final int HEALTH_BAR_WIDTH = 200;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_X = HUD_PADDING;
    private static final int HEALTH_BAR_Y = HUD_PADDING;
    
    /**
     * Creates a new GameView.
     * 
     * @param gc The GraphicsContext for rendering
     * @param gameController The game controller to get game state from
     */
    public GameView(GraphicsContext gc, GameController gameController) {
        this.gc = gc;
        this.gameController = gameController;
    }
    
    /**
     * Renders the HUD overlay.
     */
    public void render() {
        if (gc == null || gameController == null) {
            return;
        }
        
        // Render health bar
        renderHealthBar();
        
        // Render game stats
        renderGameStats();
        
        // Render trap count
        renderTrapCount();
    }
    
    /**
     * Renders the player health bar.
     */
    private void renderHealthBar() {
        Character player = gameController.getPlayer();
        if (player == null) {
            return;
        }
        
        double healthPercentage = player.getHealthPercentage();
        
        // Background (empty health bar)
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(HEALTH_BAR_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        // Health bar fill (color-coded)
        Color healthColor;
        if (healthPercentage > 0.6) {
            healthColor = Color.GREEN;
        } else if (healthPercentage > 0.3) {
            healthColor = Color.YELLOW;
        } else {
            healthColor = Color.RED;
        }
        
        gc.setFill(healthColor);
        double healthWidth = HEALTH_BAR_WIDTH * healthPercentage;
        gc.fillRect(HEALTH_BAR_X, HEALTH_BAR_Y, healthWidth, HEALTH_BAR_HEIGHT);
        
        // Border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(HEALTH_BAR_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        
        // Health text
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        String healthText = String.format("%d / %d", player.getHealth(), player.getMaxHealth());
        double textX = HEALTH_BAR_X + (HEALTH_BAR_WIDTH / 2.0) - (healthText.length() * 3.5);
        double textY = HEALTH_BAR_Y + HEALTH_BAR_HEIGHT / 2.0 + 4;
        gc.fillText(healthText, textX, textY);
    }
    
    /**
     * Renders game statistics (wave, score, enemies remaining).
     */
    private void renderGameStats() {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        int yOffset = HEALTH_BAR_Y + HEALTH_BAR_HEIGHT + 30;
        int lineHeight = 25;
        
        // Wave number
        String waveText = "Wave: " + gameController.getCurrentWave();
        gc.fillText(waveText, HUD_PADDING, yOffset);
        
        // Score
        String scoreText = "Score: " + gameController.getScore();
        gc.fillText(scoreText, HUD_PADDING, yOffset + lineHeight);
        
        // Enemies remaining
        int enemiesAlive = gameController.getEnemiesAlive();
        String enemiesText = "Enemies: " + enemiesAlive;
        gc.fillText(enemiesText, HUD_PADDING, yOffset + lineHeight * 2);
    }
    
    /**
     * Renders the trap count.
     */
    private void renderTrapCount() {
        int trapCount = gameController.getTrapCount();
        int maxTraps = Constants.MAX_TRAPS;
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        String trapText = String.format("Traps: %d / %d", trapCount, maxTraps);
        double textX = Constants.WINDOW_WIDTH - HUD_PADDING - (trapText.length() * 8);
        double textY = HUD_PADDING + 20;
        gc.fillText(trapText, textX, textY);
    }
    
    /**
     * Sets the game controller.
     * 
     * @param gameController The game controller
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    
    /**
     * Sets the graphics context.
     * 
     * @param gc The GraphicsContext
     */
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
}

