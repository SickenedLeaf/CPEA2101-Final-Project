package com.pushknight.controllers;

import com.pushknight.views.PauseMenuView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the pause menu.
 * Handles pause menu interactions and navigation.
 */
public class PauseMenuController {
    private Stage primaryStage;
    private PauseMenuView pauseMenuView;
    private Runnable onResume;
    private Runnable onRestart;
    private Runnable onMainMenu;
    
    /**
     * Creates a new PauseMenuController.
     * 
     * @param primaryStage The primary stage of the application
     */
    public PauseMenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.pauseMenuView = new PauseMenuView();
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for pause menu buttons.
     */
    private void setupEventHandlers() {
        pauseMenuView.getResumeButton().setOnAction(e -> {
            if (onResume != null) {
                onResume.run();
            }
        });
        
        pauseMenuView.getRestartButton().setOnAction(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });
        
        pauseMenuView.getMainMenuButton().setOnAction(e -> {
            if (onMainMenu != null) {
                onMainMenu.run();
            }
        });
    }
    
    /**
     * Sets the callback for when "Resume" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnResume(Runnable callback) {
        this.onResume = callback;
    }
    
    /**
     * Sets the callback for when "Restart" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnRestart(Runnable callback) {
        this.onRestart = callback;
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
     * Shows the pause menu scene.
     */
    public void showPauseMenu() {
        Scene pauseScene = pauseMenuView.getScene();
        primaryStage.setScene(pauseScene);
    }
    
    /**
     * Updates the stats displayed in the pause menu.
     * 
     * @param wave The current wave number
     * @param score The current score
     * @param timeSurvived The time survived in seconds
     */
    public void updateStats(int wave, int score, double timeSurvived) {
        pauseMenuView.updateStats(wave, score, timeSurvived);
    }
    
    /**
     * Gets the pause menu view.
     * 
     * @return The PauseMenuView instance
     */
    public PauseMenuView getPauseMenuView() {
        return pauseMenuView;
    }
}

