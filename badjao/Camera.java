package com.pushknight.systems;

import com.pushknight.entities.Player;
import com.pushknight.utils.Constants;
import com.pushknight.utils.Vector2D;

/**
 * Camera system that follows the player with smooth interpolation.
 */
public class Camera {
    private Vector2D position;          // Camera position (top-left corner of view)
    private Vector2D targetPosition;    // Target position to move towards
    private double lerpSpeed;           // Speed of camera smoothing
    private double screenWidth;         // Width of the screen
    private double screenHeight;        // Height of the screen
    private double worldWidth;          // Width of the game world
    private double worldHeight;         // Height of the game world
    
    /**
     * Creates a camera with specified screen and world dimensions.
     * @param screenWidth Width of the viewport
     * @param screenHeight Height of the viewport
     * @param worldWidth Width of the game world
     * @param worldHeight Height of the game world
     */
    public Camera(double screenWidth, double screenHeight, double worldWidth, double worldHeight) {
        this.position = new Vector2D(0, 0);
        this.targetPosition = new Vector2D(0, 0);
        this.lerpSpeed = Constants.CAMERA_LERP_SPEED;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }
    
    /**
     * Updates camera position to follow the player smoothly.
     * @param deltaTime Time elapsed since last update in seconds
     * @param player Player to follow
     */
    public void update(double deltaTime, Player player) {
        // Calculate target position (center player on screen)
        targetPosition.x = player.getPosition().x - screenWidth / 2;
        targetPosition.y = player.getPosition().y - screenHeight / 2;
        
        // Clamp target position to world bounds
        targetPosition.x = Math.max(0, Math.min(targetPosition.x, worldWidth - screenWidth));
        targetPosition.y = Math.max(0, Math.min(targetPosition.y, worldHeight - screenHeight));
        
        // Smoothly interpolate camera position towards target
        double lerpFactor = 1.0 - Math.pow(0.5, lerpSpeed * deltaTime);
        position.x += (targetPosition.x - position.x) * lerpFactor;
        position.y += (targetPosition.y - position.y) * lerpFactor;
    }
    
    /**
     * Sets camera position directly (no interpolation).
     * @param x X position
     * @param y Y position
     */
    public void setPosition(double x, double y) {
        position.x = Math.max(0, Math.min(x, worldWidth - screenWidth));
        position.y = Math.max(0, Math.min(y, worldHeight - screenHeight));
        targetPosition.x = position.x;
        targetPosition.y = position.y;
    }
    
    /**
     * Centers camera on a specific world position.
     * @param worldX X position in world coordinates
     * @param worldY Y position in world coordinates
     */
    public void centerOn(double worldX, double worldY) {
        setPosition(worldX - screenWidth / 2, worldY - screenHeight / 2);
    }
    
    /**
     * Converts world coordinates to screen coordinates.
     * @param worldPos Position in world coordinates
     * @return Position in screen coordinates
     */
    public Vector2D worldToScreen(Vector2D worldPos) {
        return new Vector2D(
            worldPos.x - position.x,
            worldPos.y - position.y
        );
    }
    
    /**
     * Converts screen coordinates to world coordinates.
     * @param screenPos Position in screen coordinates
     * @return Position in world coordinates
     */
    public Vector2D screenToWorld(Vector2D screenPos) {
        return new Vector2D(
            screenPos.x + position.x,
            screenPos.y + position.y
        );
    }
    
    /**
     * Checks if a world position is visible on screen.
     * @param worldX X position in world coordinates
     * @param worldY Y position in world coordinates
     * @param margin Extra margin around screen edges
     * @return True if position is visible
     */
    public boolean isVisible(double worldX, double worldY, double margin) {
        return worldX + margin > position.x &&
               worldX - margin < position.x + screenWidth &&
               worldY + margin > position.y &&
               worldY - margin < position.y + screenHeight;
    }
    
    /**
     * Checks if a rectangular area is visible on screen.
     * @param x X position of rectangle (top-left)
     * @param y Y position of rectangle (top-left)
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @return True if rectangle is at least partially visible
     */
    public boolean isRectVisible(double x, double y, double width, double height) {
        return x + width > position.x &&
               x < position.x + screenWidth &&
               y + height > position.y &&
               y < position.y + screenHeight;
    }
    
    /**
     * Shakes the camera (for impact effects).
     * @param intensity Shake intensity
     * @param duration Shake duration in seconds
     */
    public void shake(double intensity, double duration) {
        // To do: Implement camera shake effect
        // This would involve adding a shake offset that decays over time
    }
    
    // Getters
    public double getX() { return position.x; }
    public double getY() { return position.y; }
    public Vector2D getPosition() { return position; }
    public double getScreenWidth() { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
    
    // Setters
    public void setLerpSpeed(double speed) { this.lerpSpeed = speed; }
}
