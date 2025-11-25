package com.pushknight.utils;

import com.pushknight.entities.Character;

/**
 * Interface for objects that listen to game events.
 * Implements the Observer pattern for game event handling.
 * Classes that need to respond to game events should implement this interface.
 */
public interface GameEventListener {
    /**
     * Called when an enemy is defeated.
     * 
     * @param enemy The enemy that was defeated
     */
    void onEnemyDefeated(Character enemy);
    
    /**
     * Called when a wave is completed.
     * 
     * @param waveNumber The wave number that was completed
     */
    void onWaveComplete(int waveNumber);
    
    /**
     * Called when the player takes damage.
     * 
     * @param currentHealth The player's current health after taking damage
     * @param maxHealth The player's maximum health
     */
    void onPlayerDamaged(int currentHealth, int maxHealth);
    
    /**
     * Called when the player dies.
     */
    void onPlayerDeath();
    
    /**
     * Called when a new wave starts.
     * 
     * @param waveNumber The wave number that started
     */
    void onWaveStart(int waveNumber);
    
    /**
     * Called when a trap is triggered.
     * 
     * @param trapType The type of trap that was triggered
     */
    void onTrapTriggered(String trapType);
    
    /**
     * Called when the player's score changes.
     * 
     * @param newScore The new score value
     */
    void onScoreChanged(int newScore);
}

