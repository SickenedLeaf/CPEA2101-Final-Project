package com.pushknight.objects;

import com.pushknight.entities.Character;
import com.pushknight.utils.Constants;
import com.pushknight.utils.TrapType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Spike trap that deals instant damage to enemies when triggered.
 * Has a cooldown between activations to prevent spam.
 */
public class SpikeTrap extends Trap {
    
    /**
     * Creates a new spike trap at the specified position.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public SpikeTrap(double x, double y) {
        super(x, y, TrapType.SPIKE, Constants.TRAP_DAMAGE, Constants.TRAP_COOLDOWN);
    }
    
    /**
     * Creates a new spike trap with custom damage and cooldown.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param damage The damage this trap deals
     * @param cooldownTime The cooldown time in nanoseconds
     */
    public SpikeTrap(double x, double y, int damage, long cooldownTime) {
        super(x, y, TrapType.SPIKE, damage, cooldownTime);
    }
    
    /**
     * Triggers the spike trap on an enemy, dealing instant damage.
     * 
     * @param enemy The enemy that triggered the trap
     * @param now The current timestamp in nanoseconds
     * @return true if the trap was successfully triggered, false if on cooldown
     */
    @Override
    public boolean trigger(Character enemy, long now) {
        if (enemy == null || !enemy.isAlive()) {
            return false;
        }
        
        if (!canActivate(now)) {
            return false; // Trap is on cooldown
        }
        
        // Deal damage to the enemy
        enemy.takeDamage(damage);
        
        // Activate the trap
        activate(now);
        
        return true;
    }
    
    /**
     * Renders the spike trap with visual indicators for state.
     * 
     * @param gc The GraphicsContext to render to
     */
    @Override
    public void render(GraphicsContext gc) {
        long now = System.nanoTime();
        
        // Choose color based on state
        if (activated) {
            // Just activated - bright red
            gc.setFill(Color.RED);
        } else if (canActivate(now)) {
            // Ready to activate - orange/yellow
            gc.setFill(Color.ORANGE);
        } else {
            // On cooldown - gray with progress indicator
            double progress = getCooldownProgress(now);
            Color cooldownColor = Color.gray(0.3 + (progress * 0.4)); // Dark gray to lighter gray
            gc.setFill(cooldownColor);
        }
        
        // Draw trap body
        gc.fillRect(x, y, width, height);
        
        // Draw spikes (simple visual representation)
        gc.setFill(Color.DARKRED);
        double spikeSize = 4.0;
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;
        
        // Draw 4 spikes pointing outward
        if (canActivate(now) || activated) {
            // Top spike
            gc.fillPolygon(
                new double[]{centerX, centerX - spikeSize, centerX + spikeSize},
                new double[]{y, y + spikeSize * 2, y + spikeSize * 2},
                3
            );
            // Bottom spike
            gc.fillPolygon(
                new double[]{centerX, centerX - spikeSize, centerX + spikeSize},
                new double[]{y + height, y + height - spikeSize * 2, y + height - spikeSize * 2},
                3
            );
            // Left spike
            gc.fillPolygon(
                new double[]{x, x + spikeSize * 2, x + spikeSize * 2},
                new double[]{centerY, centerY - spikeSize, centerY + spikeSize},
                3
            );
            // Right spike
            gc.fillPolygon(
                new double[]{x + width, x + width - spikeSize * 2, x + width - spikeSize * 2},
                new double[]{centerY, centerY - spikeSize, centerY + spikeSize},
                3
            );
        }
        
        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(x, y, width, height);
        
        // Draw cooldown indicator if on cooldown
        if (!canActivate(now)) {
            double progress = getCooldownProgress(now);
            gc.setFill(Color.rgb(0, 0, 0, 0.5));
            gc.fillRect(x, y + height * (1.0 - progress), width, height * progress);
        }
    }
}

