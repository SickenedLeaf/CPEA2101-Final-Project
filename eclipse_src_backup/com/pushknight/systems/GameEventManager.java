package com.pushknight.systems;

import com.pushknight.entities.Character;
import com.pushknight.utils.GameEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages game events using the Observer pattern.
 * Allows objects to register as listeners and receive notifications about game events.
 * Implements Singleton pattern to ensure only one event manager exists.
 */
public class GameEventManager {
    private static GameEventManager instance;
    private List<GameEventListener> listeners;
    
    /**
     * Private constructor to enforce Singleton pattern.
     */
    private GameEventManager() {
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Gets the singleton instance of GameEventManager.
     * 
     * @return The GameEventManager instance
     */
    public static GameEventManager getInstance() {
        if (instance == null) {
            instance = new GameEventManager();
        }
        return instance;
    }
    
    /**
     * Registers a listener to receive game events.
     * 
     * @param listener The listener to register
     */
    public void registerListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Unregisters a listener from receiving game events.
     * 
     * @param listener The listener to unregister
     */
    public void unregisterListener(GameEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Removes all registered listeners.
     */
    public void clearListeners() {
        listeners.clear();
    }
    
    /**
     * Notifies all listeners that an enemy was defeated.
     * 
     * @param enemy The enemy that was defeated
     */
    public void fireEnemyDefeated(Character enemy) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onEnemyDefeated(enemy);
            } catch (Exception e) {
                // Log error but continue notifying other listeners
                System.err.println("Error notifying listener of enemy defeat: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that a wave was completed.
     * 
     * @param waveNumber The wave number that was completed
     */
    public void fireWaveComplete(int waveNumber) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onWaveComplete(waveNumber);
            } catch (Exception e) {
                System.err.println("Error notifying listener of wave complete: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that the player took damage.
     * 
     * @param currentHealth The player's current health
     * @param maxHealth The player's maximum health
     */
    public void firePlayerDamaged(int currentHealth, int maxHealth) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onPlayerDamaged(currentHealth, maxHealth);
            } catch (Exception e) {
                System.err.println("Error notifying listener of player damage: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that the player died.
     */
    public void firePlayerDeath() {
        for (GameEventListener listener : listeners) {
            try {
                listener.onPlayerDeath();
            } catch (Exception e) {
                System.err.println("Error notifying listener of player death: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that a new wave started.
     * 
     * @param waveNumber The wave number that started
     */
    public void fireWaveStart(int waveNumber) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onWaveStart(waveNumber);
            } catch (Exception e) {
                System.err.println("Error notifying listener of wave start: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that a trap was triggered.
     * 
     * @param trapType The type of trap that was triggered
     */
    public void fireTrapTriggered(String trapType) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onTrapTriggered(trapType);
            } catch (Exception e) {
                System.err.println("Error notifying listener of trap trigger: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifies all listeners that the score changed.
     * 
     * @param newScore The new score value
     */
    public void fireScoreChanged(int newScore) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onScoreChanged(newScore);
            } catch (Exception e) {
                System.err.println("Error notifying listener of score change: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gets the number of registered listeners.
     * 
     * @return The number of listeners
     */
    public int getListenerCount() {
        return listeners.size();
    }
}

