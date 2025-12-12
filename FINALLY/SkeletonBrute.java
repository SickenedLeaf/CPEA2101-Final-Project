package application;

public class SkeletonBrute extends Enemy {
    private boolean isObstacle;
    
    public SkeletonBrute(int x, int y) {
        super(x, y, EnemyType.BRUTE);
        this.maxHp = 6;
        this.hp = maxHp;
        this.damage = 2;
        this.movementCooldownMax = 1.2;
        this.movementCooldown = 0;
        this.isObstacle = true;
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
            System.out.println("[AI] Skeleton Brute at (" + x + "," + y + ") moving slowly towards player");
        }
        
        return move;
    }
    
    public boolean isObstacle() {
        return isObstacle;
    }
}
