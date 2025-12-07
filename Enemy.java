package application;

public class Enemy {
    private int x;
    private int y;
    private int hp;

    public Enemy(int x, int y, int maxHp) {
        this.x = x;
        this.y = y;
        this.hp = maxHp;
    }

    public Enemy(int maxEnemyHp) {
    	this.hp = maxEnemyHp;
	}

	public int getX() { return x; }
    public int getY() { return y; }
    public int getHp() { return hp; }

    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    @Override
    public String toString() {
        return "Enemy[x=" + x + ", y=" + y + ", hp=" + hp + "]";
    }
}
