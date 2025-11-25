package com.pushknight.views;

import com.pushknight.controllers.GameController;
import com.pushknight.entities.Trap;

/**
 * View class responsible for rendering game elements in the game world.
 * Handles the visual presentation of gameplay elements separate from the controller logic.
 */
public class GameView {
    private GameController gameController;

    /**
     * Creates a new GameView instance.
     * 
     * @param gameController The GameController to associate with this view
     */
    public GameView(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Adds a trap to the game world at the player's current position.
     * This is typically called when the player wants to place a trap.
     */
    public void addTrapAtPlayerPosition() {
        if (gameController != null && gameController.getPlayer() != null) {
            // Get player's current position and place trap slightly offset from player
            var player = gameController.getPlayer();
            Trap newTrap = new Trap(player.getX() + player.getWidth() / 2, 
                                  player.getY() + player.getHeight() / 2);
            gameController.addTrap(newTrap);
        }
    }

    /**
     * Renders the game UI elements such as health bars, score, and wave indicators.
     * This method is called during the rendering process to overlay UI elements.
     */
    public void renderUI() {
        // UI rendering is handled in GameController
        // This class acts as a helper for UI elements
    }

    /**
     * Gets the associated game controller.
     * 
     * @return The GameController
     */
    public GameController getGameController() {
        return gameController;
    }

    /**
     * Sets the associated game controller.
     * 
     * @param gameController The GameController to associate with this view
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}