package com.pushknight.controllers;

import com.pushknight.utils.GameState;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller for handling the main menu and other UI screens.
 */
public class MenuController {
    private GameState currentState;
    private GraphicsContext graphicsContext;
    private Scene scene;
    private GameController gameController;

    /**
     * Creates a new MenuController.
     */
    public MenuController() {
        this.currentState = GameState.MENU;
    }

    /**
     * Sets the graphics context for rendering.
     *
     * @param gc The GraphicsContext to use for rendering
     */
    public void setGraphicsContext(GraphicsContext gc) {
        this.graphicsContext = gc;
    }

    /**
     * Sets the scene for input handling.
     *
     * @param scene The Scene to attach input handlers to
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        setupInputHandlers();
    }

    /**
     * Sets the game controller for state transitions.
     *
     * @param gameController The GameController to use for state transitions
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Sets up input handlers for the scene.
     */
    private void setupInputHandlers() {
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPress);
        }
    }

    /**
     * Handles key press events.
     *
     * @param event The key event
     */
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        
        switch (currentState) {
            case MENU:
                handleMenuKeyPress(code);
                break;
            case UPGRADE_SELECTION:
                handleUpgradeSelectionKeyPress(code);
                break;
        }
    }

    /**
     * Handles key presses in the main menu state.
     *
     * @param code The key code
     */
    private void handleMenuKeyPress(KeyCode code) {
        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            // Start the game
            if (gameController != null) {
                gameController.setCurrentState(GameState.PLAYING);
                currentState = GameState.PLAYING;
            }
        } else if (code == KeyCode.ESCAPE) {
            // Close the application
            System.exit(0);
        }
    }

    /**
     * Handles key presses in the upgrade selection state.
     *
     * @param code The key code
     */
    private void handleUpgradeSelectionKeyPress(KeyCode code) {
        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            // Continue to next wave
            if (gameController != null) {
                gameController.setCurrentState(GameState.PLAYING);
                currentState = GameState.PLAYING;
            }
        } else if (code == KeyCode.ESCAPE) {
            // Go back to main menu
            if (gameController != null) {
                gameController.setCurrentState(GameState.MENU);
                currentState = GameState.MENU;
            }
        }
    }

    /**
     * Renders the current menu screen.
     */
    public void render() {
        if (graphicsContext == null) {
            return; // Can't render without graphics context
        }

        // Set background
        graphicsContext.setFill(javafx.scene.paint.Color.BLACK);
        graphicsContext.fillRect(0, 0, 
            graphicsContext.getCanvas().getWidth(), 
            graphicsContext.getCanvas().getHeight());

        // Set text properties
        graphicsContext.setFill(javafx.scene.paint.Color.WHITE);
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 36));

        switch (currentState) {
            case MENU:
                renderMainMenu();
                break;
            case UPGRADE_SELECTION:
                renderUpgradeSelection();
                break;
        }
    }

    /**
     * Renders the main menu screen.
     */
    private void renderMainMenu() {
        // Title
        String title = "PUSH KNIGHT PERIL";
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 48));
        double titleWidth = graphicsContext.getFont().getSize() * title.length() / 2; // Rough estimation
        graphicsContext.fillText(title, 
            graphicsContext.getCanvas().getWidth() / 2 - titleWidth / 2, 
            graphicsContext.getCanvas().getHeight() / 2 - 100);

        // Menu options
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 24));
        String startText = "Press ENTER to Start Game";
        String quitText = "Press ESC to Quit";

        double startWidth = graphicsContext.getFont().getSize() * startText.length() / 3; // Rough estimation
        double quitWidth = graphicsContext.getFont().getSize() * quitText.length() / 3; // Rough estimation

        graphicsContext.fillText(startText, 
            graphicsContext.getCanvas().getWidth() / 2 - startWidth / 2, 
            graphicsContext.getCanvas().getHeight() / 2);

        graphicsContext.fillText(quitText, 
            graphicsContext.getCanvas().getWidth() / 2 - quitWidth / 2, 
            graphicsContext.getCanvas().getHeight() / 2 + 50);
    }

    /**
     * Renders the upgrade selection screen.
     */
    private void renderUpgradeSelection() {
        // Title
        String title = "UPGRADE SELECTION";
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 36));
        double titleWidth = graphicsContext.getFont().getSize() * title.length() / 2.5; // Rough estimation
        graphicsContext.fillText(title, 
            graphicsContext.getCanvas().getWidth() / 2 - titleWidth / 2, 
            graphicsContext.getCanvas().getHeight() / 2 - 100);

        // Upgrade options (placeholder)
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 18));
        String option1Text = "1. Increase Health";
        String option2Text = "2. Increase Damage";
        String option3Text = "3. Faster Movement";

        double optionWidth1 = graphicsContext.getFont().getSize() * option1Text.length() / 4; // Rough estimation
        double optionWidth2 = graphicsContext.getFont().getSize() * option2Text.length() / 4; // Rough estimation
        double optionWidth3 = graphicsContext.getFont().getSize() * option3Text.length() / 4; // Rough estimation

        graphicsContext.fillText(option1Text, 
            graphicsContext.getCanvas().getWidth() / 2 - optionWidth1 / 2, 
            graphicsContext.getCanvas().getHeight() / 2 - 20);

        graphicsContext.fillText(option2Text, 
            graphicsContext.getCanvas().getWidth() / 2 - optionWidth2 / 2, 
            graphicsContext.getCanvas().getHeight() / 2 + 20);

        graphicsContext.fillText(option3Text, 
            graphicsContext.getCanvas().getWidth() / 2 - optionWidth3 / 2, 
            graphicsContext.getCanvas().getHeight() / 2 + 60);

        // Continue instruction
        graphicsContext.setFont(javafx.scene.text.Font.font("Arial", 24));
        String continueText = "Press ENTER to select and continue";
        double continueWidth = graphicsContext.getFont().getSize() * continueText.length() / 3; // Rough estimation

        graphicsContext.fillText(continueText, 
            graphicsContext.getCanvas().getWidth() / 2 - continueWidth / 2, 
            graphicsContext.getCanvas().getHeight() / 2 + 150);
    }

    /**
     * Gets the current menu state.
     *
     * @return The current GameState
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current menu state.
     *
     * @param state The new GameState
     */
    public void setCurrentState(GameState state) {
        this.currentState = state;
    }
}