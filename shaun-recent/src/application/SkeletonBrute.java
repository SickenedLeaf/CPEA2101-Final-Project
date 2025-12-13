package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SkeletonBrute extends Enemy {
    private boolean isObstacle;

    public SkeletonBrute(int x, int y) {
        super(x, y, EnemyType.BRUTE,
              new Image(SkeletonBrute.class.getResource("/assets/enemies/BruteSpriteSheet.png").toExternalForm()));

        this.maxHp = 6;
        this.hp = maxHp;
        this.damage = 2;
        this.movementCooldownMax = 3.0;
        this.movementCooldown = 0;
        this.isObstacle = true;

        // Default facing this.direction
        this.direction = "DOWN";

        // Start idle animation immediately
        playIdleAnimation();
    }

    @Override
    public int[] updateAI(double deltaTime, int playerX, int playerY,
                          Pathfinder pathfinder, int[][] grid) {
        if (dead) return null;

        // Check if immobilized by trap
        if (isImmobilized()) {
            return null; // Don't move while trapped
        }

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

    // --- Animation Helpers ---
    @Override
    public void playIdleAnimation() {
        if ("DOWN".equals(this.direction)) {
            animator.playAction(0, 6, 3, 500_000_000L, true, null);
        } else if ("UP".equals(this.direction)) {
            animator.playAction(0, 9, 3, 500_000_000L, true, null);
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(0, 0, 3, 500_000_000L, true, null);
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(0, 3, 3, 500_000_000L, true, null);
        }
    }

    @Override
    public void playMoveAnimation() {

        Runnable idleCallback = this::playIdleAnimation;

        if ("DOWN".equals(this.direction)) {
            animator.playAction(2, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("UP".equals(this.direction)) {
            animator.playAction(2, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("RIGHT".equals(this.direction)) {
            animator.playAction(1, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("LEFT".equals(this.direction)) {
            animator.playAction(1, 6, 6, 100_000_000L, false, idleCallback);
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
    public void playPushAnimation(String playerDirection) {
    	String dir = playerDirection;
        Runnable idleCallback = this::playIdleAnimation;
        // Example: row 5 contains push frames
        if ("DOWN".equals(dir)) {
            animator.playAction(8, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("UP".equals(dir)) {
            animator.playAction(8, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("RIGHT".equals(dir)) {
            animator.playAction(7, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("LEFT".equals(dir)) {
            animator.playAction(7, 0, 6, 100_000_000L, false, idleCallback);
        }
    }
    @Override
    public void playHitAnimation(String playerDirection) {
    	String dir = playerDirection;
        Runnable idleCallback = this::playIdleAnimation;
        // Example: row 5 contains push frames
        if ("DOWN".equals(dir)) {
            animator.playAction(6, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("UP".equals(dir)) {
            animator.playAction(6, 0, 6, 100_000_000L, false, idleCallback);
        } else if ("RIGHT".equals(dir)) {
            animator.playAction(5, 6, 6, 100_000_000L, false, idleCallback);
        } else if ("LEFT".equals(dir)) {
            animator.playAction(5, 0, 6, 100_000_000L, false, idleCallback);
        }
    }

    @Override
    public void playDeathAnimation() {
    	ImageView view = animator.getImageView();
        if (view.getParent() != null) {
            ((Pane) view.getParent()).getChildren().remove(view);
        };
    }

    // Expose ImageView for GamePanel
    @Override
    public ImageView getImageView() {
        return animator.getImageView();
    }

    public boolean isObstacle() {
        return isObstacle;
    }
}
