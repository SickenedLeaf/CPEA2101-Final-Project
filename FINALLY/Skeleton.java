package application;

public class Skeleton extends Enemy {
    public Skeleton(int x, int y) {
        super(x, y, EnemyType.SKELETON);
        this.maxHp = 3;
        this.hp = maxHp;
        this.damage = 1;
        this.movementCooldownMax = 0.6;
        this.movementCooldown = 0;
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
            System.out.println("[AI] Skeleton at (" + x + "," + y + ") moving towards player");
        }
        
        return move;
    }
}
