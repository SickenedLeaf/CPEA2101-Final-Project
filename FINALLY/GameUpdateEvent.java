package application;

/**
 * Event class for game state changes and updates.
 */
public class GameUpdateEvent {
    public enum Type {
        PLAYER_MOVE,
        ENEMY_MOVE,
        ENEMY_SPAWN,
        DAMAGE,
        REMOVE_ENTITY,
        IMPACT,
        PLAYER_DAMAGE
    }

    public final Type type;
    // Common coords
    public final int x, y;
    // Movement coords
    public final int oldX, oldY, newX, newY;
    // Payload (e.g., hp)
    public final int value;

    // Player move constructor
    public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY) {
        this.type = type;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.x = newX;
        this.y = newY;
        this.value = 0;
    }

    // Enemy move with value constructor
    public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY, int value) {
        this.type = type;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.x = newX;
        this.y = newY;
        this.value = value;
    }

    // Damage, Remove, Impact with value constructor
    public GameUpdateEvent(Type type, int x, int y, int value) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.value = value;
        this.oldX = 0;
        this.oldY = 0;
        this.newX = x;
        this.newY = y;
    }

    // Simple event constructor (no value)
    public GameUpdateEvent(Type type, int x, int y) {
        this(type, x, y, 0);
    }
}