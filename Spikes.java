package application;

public class Spikes extends Obstacle {
	public Spikes() {
		// entityType=3 (new type for visualization), collisionDamage=2, passDamage=0,
		// passable=false
		super(3, 2, 0, false);
	}
}
