package com.pushknight.controllers;

import com.pushknight.utils.GameState;
import com.pushknight.views.MenuView;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the main menu.
 * Handles menu navigation and state transitions.
 */
public class MenuController {
    private Stage primaryStage;
    private MenuView menuView;
    private Runnable onStartGame;
    private Runnable onShowHowToPlay;
    private Runnable onShowSettings;
    
    /**
     * Creates a new MenuController.
     * 
     * @param primaryStage The primary stage of the application
     */
    public MenuController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.menuView = new MenuView();
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for menu buttons.
     */
    private void setupEventHandlers() {
        menuView.getStartButton().setOnAction(e -> {
            if (onStartGame != null) {
                onStartGame.run();
            }
        });
        
        menuView.getHowToPlayButton().setOnAction(e -> {
            if (onShowHowToPlay != null) {
                onShowHowToPlay.run();
            }
        });
        
        menuView.getSettingsButton().setOnAction(e -> {
            if (onShowSettings != null) {
                onShowSettings.run();
            }
        });
        
        menuView.getQuitButton().setOnAction(e -> {
            System.exit(0);
        });
    }
    
    /**
     * Sets the callback for when "Start Game" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnStartGame(Runnable callback) {
        this.onStartGame = callback;
    }
    
    /**
     * Sets the callback for when "How to Play" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnShowHowToPlay(Runnable callback) {
        this.onShowHowToPlay = callback;
    }
    
    /**
     * Sets the callback for when "Settings" is clicked.
     * 
     * @param callback The callback to execute
     */
    public void setOnShowSettings(Runnable callback) {
        this.onShowSettings = callback;
    }
    
    /**
     * Shows the menu scene.
     */
    public void showMenu() {
        Scene menuScene = menuView.getScene();
        primaryStage.setScene(menuScene);
    }
    
    /**
     * Gets the menu view.
     * 
     * @return The MenuView instance
     */
    public MenuView getMenuView() {
        return menuView;
    }
}

