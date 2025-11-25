package com.pushknight.controllers;

import com.pushknight.views.GameOverView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the game over screen.
 * Handles game over screen interactions and navigation.
 */
public class GameOverController {
    private Stage primaryStage;
    private GameOverView gameOverView;
    private Runnable onRetry;
    private Runnable onMainMenu;
    
    /**
     * Creates a new GameOverController.
     * 
     * @param primaryStage The primary stage of the application
     */
    public GameOverController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.gameOverView = new GameOverView();
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for game over buttons.
     */
    private void setupEventHandlers() {
        gameOverView.getRetryButton().setOnAction(e -> {
            if (onRetry != null) {
                onRetry.run();
            }
        });
        
        gameOverView.getMainMenuButton().setOnAction(e -> {
            if (onMainMenu != null) {
                onMainMenu.run();
            }
        });
    }
    
    /**
     * Sets the callback for when "Retry" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnRetry(Runnable callback) {
        this.onRetry = callback;
    }
    
    /**
     * Sets the callback for when "Main Menu" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnMainMenu(Runnable callback) {
        this.onMainMenu = callback;
    }
    
    /**
     * Shows the game over screen with final statistics.
     * 
     * @param finalScore The final score
     * @param waveReached The wave reached
     * @param enemiesDefeated The number of enemies defeated
     * @param timeSurvived The time survived in seconds
     */
    public void showGameOver(int finalScore, int waveReached, int enemiesDefeated, double timeSurvived) {
        gameOverView.updateStats(finalScore, waveReached, enemiesDefeated, timeSurvived);
        Scene gameOverScene = gameOverView.getScene();
        primaryStage.setScene(gameOverScene);
    }
    
    /**
     * Gets the game over view.
     * 
     * @return The GameOverView instance
     */
    public GameOverView getGameOverView() {
        return gameOverView;
    }
}

