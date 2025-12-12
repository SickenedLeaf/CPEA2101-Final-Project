package application;

public class BoomerGoblin extends Enemy {
    private boolean hasExploded;
    
    public BoomerGoblin(int x, int y) {
        super(x, y, EnemyType.BOOMER);
        this.maxHp = 1;
        this.hp = maxHp;
        this.damage = 0;
        this.movementCooldownMax = 0.3;
        this.movementCooldown = 0;
        this.hasExploded = false;
    }
    
    @Override
    public int[] updateAI(double deltaTime, int playerX, int playerY, 
                         Pathfinder pathfinder, int[][] grid) {
        if (dead) return null;
        
        movementCooldown -= deltaTime;
        if (movementCooldown > 0) {
            return null;
        }
        
        movementCooldown = movementCooldownMax;
        
        int[] move = pathfinder.getNextMove(x, y, playerX, playerY, grid);
        
        if (move != null) {
            System.out.println("[AI] Boomer Goblin at (" + x + "," + y + ") rushing towards player");
        }
        
        return move;
    }
    
    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        
        if (dead && !hasExploded) {
            explode();
        }
    }
    
    private void explode() {
        hasExploded = true;
        System.out.println("[EXPLOSION] Boomer Goblin exploded at (" + x + "," + y + ")!");
    }
    
    public int getExplosionDamage() {
        return 2;
    }
    
    public int getExplosionRange() {
        return 1;
    }
    
    public boolean hasExploded() {
        return hasExploded;
    }
}