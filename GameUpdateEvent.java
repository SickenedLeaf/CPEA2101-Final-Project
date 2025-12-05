package application;

public interface GameUpdateEvent {
    enum Type { PLAYER_MOVE, ENEMY_MOVE, DAMAGE, REMOVE_ENTITY, IMPACT }
    static Type type() {
		// TODO Auto-generated method stub
		return null;
	}
    int oldX();
    int oldY();
    int newX();
    int newY();
    int value(); // e.g., damage amount or new health
}
