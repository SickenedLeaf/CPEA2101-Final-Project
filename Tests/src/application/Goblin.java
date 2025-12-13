package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Goblin extends Enemy {
    public Goblin(int x, int y) {
        super(x, y, EnemyType.GOBLIN,
              new Image(Goblin.class.getResource("/assets/enemies/GoblinSpriteSheet.png").toExternalForm()));

        this.maxHp = 2;
        this.hp = maxHp;
        this.damage = 1;
        this.movementCooldownMax = 3.0;
        this.movementCooldown = 0;

        // Start idle animation immediately
        playIdleAnimation();
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
            System.out.println("[AI] Goblin at (" + x + "," + y + ") moving towards player");
        }

        return move;
    }

    // --- Animation Helpers ---
    @Override
    public void playIdleAnimation() {
        if ("DOWN".equals(this.direction)) {
            animator.playAction(0, 6, 3, 250_000_000L, true, null);
        } else if ("UP".equals(this.direction)) {
            animator.playAction(0, 9, 3, 250_000_000L, true, null);
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(0, 0, 3, 250_000_000L, true, null);
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(0, 3, 3, 250_000_000L, true, null);
        }
    }

    @Override
    public void playMoveAnimation() {
        // Update instance variable

        Runnable idleCallback = this::playIdleAnimation;
        if ("DOWN".equals(this.direction)) {
            animator.playAction(2, 0, 6, 50_000_000L, false, idleCallback);
        } else if ("UP".equals(this.direction)) {
            animator.playAction(2, 6, 6, 50_000_000L, false, idleCallback);
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(1, 0, 6, 50_000_000L, false, idleCallback);
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(1, 6, 6, 50_000_000L, false, idleCallback);
        }
    }

    @Override
    public void playAttackAnimation() {
    	Runnable idleCallback = this::playIdleAnimation;
    	if ("DOWN".equals(this.direction)) {
            animator.playAction(4, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("UP".equals(this.direction)) {
            animator.playAction(4, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(3, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(3, 6, 6, 100_000_000L, false, idleCallback);
        }
    }

    @Override
    public void playDeathAnimation() {
        animator.playAction(4, 0, 6, 200_000_000L, false, null);
    }

    // Expose ImageView for GamePanel
    @Override
    public ImageView getImageView() {
        return animator.getImageView();
    }
}