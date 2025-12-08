package application;;

public class GameUpdateEvent {
    public enum Type { PLAYER_MOVE, ENEMY_MOVE, DAMAGE, REMOVE_ENTITY, IMPACT }

    public final Type type;
    // Common coords
    public final int x, y;
    // Movement coords
    public final int oldX, oldY, newX, newY;
    // Payload (e.g., hp)
    public final int value;

    // Player move
    public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY) {
        this.type = type;
        this.oldX = oldX; this.oldY = oldY;
        this.newX = newX; this.newY = newY;
        this.x = newX; this.y = newY;
        this.value = 0;
    }

    // Enemy move
    public GameUpdateEvent(Type type, int oldX, int oldY, int newX, int newY, int value) {
        this.type = type;
        this.oldX = oldX; this.oldY = oldY;
        this.newX = newX; this.newY = newY;
        this.x = newX; this.y = newY;
        this.value = value;
    }

    // Damage, Remove, Impact
    public GameUpdateEvent(Type type, int x, int y, int value) {
        this.type = type;
        this.x = x; this.y = y;
        this.value = value;
        this.oldX = 0; this.oldY = 0; this.newX = x; this.newY = y;
    }

    public GameUpdateEvent(Type type, int x, int y) {
        this(type, x, y, 0);
    }
}

