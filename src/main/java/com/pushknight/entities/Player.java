package com.pushknight.entities;

import com.pushknight.utils.Constants;
import com.pushknight.utils.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The player character controlled by the user.
 * Extends Character and includes movement and push mechanics.
 */
public class Player extends Character {
    private MovementComponent movement;
    private int score;
    private long lastPushTime;
    private boolean pushAvailable;

    /**
     * Creates a new Player at the center of the screen.
     */
    public Player() {
        super(
            Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0,
            Constants.WINDOW_HEIGHT / 2.0 - Constants.PLAYER_HEIGHT / 2.0,
            Constants.PLAYER_WIDTH,
            Constants.PLAYER_HEIGHT,
            Constants.PLAYER_INITIAL_HEALTH
        );
        this.movement = new MovementComponent();
        this.score = 0;
        this.lastPushTime = System.nanoTime();
        this.pushAvailable = true;
    }

    /**
     * Updates the player state based on elapsed time.
     *
     * @param now The current timestamp in nanoseconds
     */
    @Override
    public void update(long now) {
        // Update push availability based on cooldown
        if (!pushAvailable && (now - lastPushTime) >= Constants.PUSH_COOLDOWN) {
            pushAvailable = true;
        }
    }

    /**
     * Renders the player to the graphics context.
     *
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        // Draw player as a blue rectangle
        gc.setFill(Color.BLUE);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());

        // Draw a simple border
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);
        gc.strokeRect(getX(), getY(), getWidth(), getHeight());

        // Optional: Draw health bar above player
        double healthBarWidth = getWidth();
        double healthBarHeight = 5;
        double healthPercentage = getHealthPercentage();

        // Health bar background
        gc.setFill(Color.RED);
        gc.fillRect(getX(), getY() - 10, healthBarWidth, healthBarHeight);

        // Health bar fill
        gc.setFill(Color.GREEN);
        gc.fillRect(getX(), getY() - 10, healthBarWidth * healthPercentage, healthBarHeight);
    }

    /**
     * Moves the player in the specified direction.
     *
     * @param direction The direction to move
     * @param now       The current timestamp in nanoseconds
     * @return true if the move was successful, false otherwise
     */
    public boolean move(Direction direction, long now) {
        return movement.move(this, direction, now);
    }

    /**
     * Gets the movement component of this player.
     *
     * @return The MovementComponent
     */
    public MovementComponent getMovement() {
        return movement;
    }

    /**
     * Gets the current score of the player.
     *
     * @return The score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     *
     * @param score The new score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Adds to the player's score.
     *
     * @param points The points to add
     */
    public void addScore(int points) {
        this.score += points;
    }

    /**
     * Checks if the push ability is currently available.
     *
     * @return true if push is available, false otherwise
     */
    public boolean isPushAvailable() {
        return pushAvailable;
    }

    /**
     * Uses the push ability if available.
     *
     * @param now The current timestamp in nanoseconds
     * @return true if push was used, false if not available
     */
    public boolean usePush(long now) {
        if (pushAvailable) {
            pushAvailable = false;
            lastPushTime = now;
            return true;
        }
        return false;
    }

    /**
     * Gets the push radius (area of effect for the push ability).
     *
     * @return The push radius in pixels
     */
    public double getPushRadius() {
        return Constants.PUSH_RADIUS;
    }

    /**
     * Gets the push force (how far enemies are pushed).
     *
     * @return The push force
     */
    public double getPushForce() {
        return Constants.PUSH_FORCE;
    }
}