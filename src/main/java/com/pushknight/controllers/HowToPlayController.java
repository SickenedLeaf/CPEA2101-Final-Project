package com.pushknight.controllers;

import com.pushknight.views.HowToPlayView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the "How to Play" screen.
 * Handles navigation back to the main menu.
 */
public class HowToPlayController {
    private Stage primaryStage;
    private HowToPlayView howToPlayView;
    private Runnable onBack;
    
    /**
     * Creates a new HowToPlayController.
     * 
     * @param primaryStage The primary stage of the application
     */
    public HowToPlayController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.howToPlayView = new HowToPlayView();
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for the "How to Play" screen.
     */
    private void setupEventHandlers() {
        howToPlayView.getBackButton().setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });
    }
    
    /**
     * Sets the callback for when "Back" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
    
    /**
     * Shows the "How to Play" screen.
     */
    public void showHowToPlay() {
        Scene howToPlayScene = howToPlayView.getScene();
        primaryStage.setScene(howToPlayScene);
    }
    
    /**
     * Gets the "How to Play" view.
     * 
     * @return The HowToPlayView instance
     */
    public HowToPlayView getHowToPlayView() {
        return howToPlayView;
    }
}

