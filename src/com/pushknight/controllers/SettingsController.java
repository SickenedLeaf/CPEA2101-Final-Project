package com.pushknight.controllers;

import com.pushknight.systems.AudioManager;
import com.pushknight.views.SettingsView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.util.Properties;

/**
 * Controller for the settings screen.
 * Manages settings state and save/load functionality.
 */
public class SettingsController {
    private Stage primaryStage;
    private SettingsView settingsView;
    private Runnable onBack;
    private AudioManager audioManager;
    private static final String SETTINGS_FILE = "game_settings.properties";
    
    /**
     * Creates a new SettingsController.
     * 
     * @param primaryStage The primary stage of the application
     */
    public SettingsController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.settingsView = new SettingsView();
        this.audioManager = AudioManager.getInstance();
        setupEventHandlers();
        loadSettings();
    }
    
    /**
     * Sets up event handlers for settings screen buttons.
     */
    private void setupEventHandlers() {
        settingsView.getSaveButton().setOnAction(e -> {
            saveSettings();
            applySettings();
            if (onBack != null) {
                onBack.run();
            }
        });
        
        settingsView.getBackButton().setOnAction(e -> {
            // Reload settings to discard changes
            loadSettings();
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
     * Shows the settings screen.
     */
    public void showSettings() {
        loadSettings();
        Scene settingsScene = settingsView.getScene();
        primaryStage.setScene(settingsScene);
    }
    
    /**
     * Saves settings to a properties file.
     */
    public void saveSettings() {
        Properties props = new Properties();
        
        // Save audio settings
        props.setProperty("soundEffectVolume", String.valueOf(settingsView.getSoundEffectVolume()));
        props.setProperty("musicVolume", String.valueOf(settingsView.getMusicVolume()));
        props.setProperty("soundEffectsEnabled", String.valueOf(settingsView.isSoundEffectsEnabled()));
        props.setProperty("musicEnabled", String.valueOf(settingsView.isMusicEnabled()));
        
        // Save game settings
        props.setProperty("fullscreen", String.valueOf(settingsView.isFullscreen()));
        
        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Push Knight Peril Settings");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
    
    /**
     * Loads settings from a properties file.
     */
    public void loadSettings() {
        Properties props = new Properties();
        
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            props.load(in);
            
            // Load audio settings
            double soundVolume = Double.parseDouble(props.getProperty("soundEffectVolume", "0.7"));
            double musicVol = Double.parseDouble(props.getProperty("musicVolume", "0.5"));
            boolean soundEnabled = Boolean.parseBoolean(props.getProperty("soundEffectsEnabled", "true"));
            boolean musicEnabled = Boolean.parseBoolean(props.getProperty("musicEnabled", "true"));
            
            settingsView.setSoundEffectVolume(soundVolume);
            settingsView.setMusicVolume(musicVol);
            settingsView.setSoundEffectsEnabled(soundEnabled);
            settingsView.setMusicEnabled(musicEnabled);
            
            // Load game settings
            boolean fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen", "false"));
            settingsView.setFullscreen(fullscreen);
            
        } catch (FileNotFoundException e) {
            // Settings file doesn't exist - use defaults
            System.out.println("Settings file not found, using defaults");
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }
        
        // Apply loaded settings
        applySettings();
    }
    
    /**
     * Applies the current settings to the game systems.
     */
    public void applySettings() {
        // Apply audio settings
        audioManager.setSoundEffectVolume(settingsView.getSoundEffectVolume());
        audioManager.setMusicVolume(settingsView.getMusicVolume());
        audioManager.setSoundEffectsEnabled(settingsView.isSoundEffectsEnabled());
        audioManager.setMusicEnabled(settingsView.isMusicEnabled());
        
        // Apply game settings
        primaryStage.setFullScreen(settingsView.isFullscreen());
    }
    
    /**
     * Gets the settings view.
     * 
     * @return The SettingsView instance
     */
    public SettingsView getSettingsView() {
        return settingsView;
    }
}

