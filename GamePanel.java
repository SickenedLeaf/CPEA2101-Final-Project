package application;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import application.GameLogic.GameUpdateEvent; // Added missing import

/**
 * GamePanel (View): Handles all graphical rendering and visual effects using JavaFX.
 * It reads the state from GameLogic but does not contain game rules.
 */
public class GamePanel {

	private static final int TILE_SIZE = 72;
	
	private GameLogic logic;
	private GridPane gridView = new GridPane();
	private StackPane[][] tilePanes = new StackPane[GameLogic.GRID_WIDTH][GameLogic.GRID_HEIGHT];
	private Image floorTile1 = new Image(getClass().getResource("/assets/floortiles/FloorTile1.png").toExternalForm());
	private Image floorTile2 = new Image(getClass().getResource("/assets/floortiles/FloorTile2.png").toExternalForm());
	private Image floorTile3 = new Image(getClass().getResource("/assets/floortiles/FloorTile3.png").toExternalForm());
	
	private Image wallTile = new Image(getClass().getResource("/assets/obstacles/Wall.png").toExternalForm());
	private Image spikeTile = new Image(getClass().getResource("/assets/obstacles/Spikes.png").toExternalForm());
	private Image campfireTile = new Image(getClass().getResource("/assets/obstacles/Campfire.png").toExternalForm());
	
	private Random random = new Random();
	
	private Image playerSpriteSheet = new Image(getClass().getResource("/assets/player/PushKnight.png").toExternalForm());
	
	private SpriteAnimator animator;
	private ImageView playerNode;
	
	private Map<String, Label> enemyHealthLabels = new HashMap<>();

	public GamePanel(GameLogic logic) {
		this.logic = logic;
		initializeGameView();
	}

	/**
	 * Builds the initial JavaFX GridPane and all visual elements based on initial state.
	 * Now uses logic.getEntityTypeAt() and handles WallSpikes (3) and Campfires (4).
	 */
	private void initializeGameView() {
		gridView.setAlignment(Pos.CENTER);
		gridView.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
		for (int x = 0; x < GameLogic.GRID_WIDTH; x++) {
			for (int y = 0; y < GameLogic.GRID_HEIGHT; y++) {
				StackPane tilePane = new StackPane();
		        tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);
		        tilePanes[x][y] = tilePane;

		        // 1. Floor Layer
		        int floorType = random.nextInt(100); // generate per tile
		        Image floorImage;

		        if (floorType >= 90) {
		            floorImage = floorTile3; // <-- should be Image, not ImageView
		        } else if (floorType >= 60) {
		            floorImage = floorTile2;
		        } else {
		            floorImage = floorTile1;
		        }

		        ImageView tileView = new ImageView(floorImage);
		        tileView.setFitWidth(TILE_SIZE);
		        tileView.setFitHeight(TILE_SIZE);
		        tilePane.getChildren().add(tileView);
				
				// 2. Entity Layer 
				Node entityNode = null;
				// Corrected: Using the correct method from GameLogic
				int type = logic.getEntityAt(x, y); 

				if (type == 1) { 
					entityNode = createWallNode();
				} else if (type == 2) { 
					entityNode = createEnemyNode(x, y, logic.getEnemyHealthAt(x, y));
				} else if (type == 3) { // WallSpikes
					entityNode = createWallSpikesNode();
				} else if (type == 4) { // Campfire
					entityNode = createCampfireNode();
				}

				if (entityNode != null) {
					tilePane.getChildren().add(entityNode);
				}

				GridPane.setConstraints(tilePane, x, y);
				gridView.getChildren().add(tilePane);
			}
		}
		
		animator = new SpriteAnimator(playerSpriteSheet, TILE_SIZE, TILE_SIZE);
		playerNode = animator.getImageView();

		// Place player in grid
		tilePanes[logic.getPlayerX()][logic.getPlayerY()].getChildren().add(animator.getImageView());
		playIdleAnimation();
	}
	
	private ImageView createWallNode() {
		ImageView wall = new ImageView(wallTile);
		return wall;
	}

	private ImageView createWallSpikesNode() {
		ImageView spikes = new ImageView(spikeTile);
		return spikes;
	}

	private ImageView createCampfireNode() {
		ImageView campfire = new ImageView(campfireTile);
		return campfire;
	}
	
	private StackPane createEnemyNode(int x, int y, int health) {
		Rectangle enemy = new Rectangle(TILE_SIZE - 10, TILE_SIZE - 10, Color.web("#e74c3c")); 
		enemy.setArcWidth(15);
		enemy.setArcHeight(15);
		
		Label hpLabel = new Label(String.valueOf(health));
		hpLabel.setFont(new Font("Arial", 16));
		hpLabel.setTextFill(Color.WHITE);
		
		String key = x + "," + y;
		enemyHealthLabels.put(key, hpLabel);
		
		StackPane enemyStack = new StackPane(enemy, hpLabel);
		enemyStack.setId("enemy_" + key); 
		
		return enemyStack;
	}
	
	/**
	 * Processes a single GameUpdateEvent and updates the GUI accordingly.
	 * Fixed: Accessing event properties via the 'event' instance, not statically.
	 */
	public void handleEvent(GameUpdateEvent event) {
	    switch (event.type) {
		    case PLAYER_MOVE:
		        moveNode(playerNode, event.oldX, event.oldY, event.newX, event.newY);
		        if ("DOWN".equals(logic.getDirection())) {
		            animator.playAction(2, 0, 6, 50_000_000L, false, () -> playIdleAnimation());
		        } else if ("UP".equals(logic.getDirection())) {
		            animator.playAction(2, 6, 6, 50_000_000L, false, () -> playIdleAnimation());
		        } else if ("RIGHT".equals(logic.getDirection())) {
		            animator.playAction(1, 0, 6, 50_000_000L, false, () -> playIdleAnimation());
		        } else if ("LEFT".equals(logic.getDirection())) {
		            animator.playAction(1, 6, 6, 50_000_000L, false, () -> playIdleAnimation());
		        }
		        break;
	        case ENEMY_MOVE:
	            handleEnemyMove(event);
	            if(logic.getDirection() == "DOWN") {
	            	animator.playAction(4, 2, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "UP") {
	            	animator.playAction(4, 8, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "RIGHT") {
	            	animator.playAction(3, 2, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "LEFT") {
	            	animator.playAction(3, 8, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            }
	            break;
	        case DAMAGE:
	            updateHealthLabel(event.newX, event.newY, event.value);
	            break;
	        case REMOVE_ENTITY:
	            removeEntityNode(event.newX, event.newY);
	            break;
	        case IMPACT:
	            showAttackEffect(event.newX, event.newY);
	            if(logic.getDirection() == "DOWN") {
	            	animator.playAction(4, 2, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "UP") {
	            	animator.playAction(4, 8, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "RIGHT") {
	            	animator.playAction(3, 2, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            } else if (logic.getDirection() == "LEFT") {
	            	animator.playAction(3, 8, 4, (long)50_000_000, false, () -> playIdleAnimation());
	            }
	            break;
	        default:
	    }
	}
	
	public void playIdleAnimation() {
	    if (animator != null) {
	    	if("DOWN".equals(logic.getDirection())) {
	    		animator.playAction(0, 6, 3, 500_000_000, true, null); 
            } else if ("UP".equals(logic.getDirection())) {
            	animator.playAction(0, 9, 3, 500_000_000, true, null); 
            } else if ("RIGHT".equals(logic.getDirection())) {
            	animator.playAction(0, 0, 3, 500_000_000, true, null); 
            } else if ("LEFT".equals(logic.getDirection())) {
            	animator.playAction(0, 3, 3, 500_000_000, true, null); 
            }
	    }
	}
	
	/**
	 * Moves a generic node (like the player) from one tile to another.
	 */
	private void moveNode(Node node, int oldX, int oldY, int newX, int newY) {
		if (tilePanes[oldX][oldY].getChildren().contains(node)) {
			tilePanes[oldX][oldY].getChildren().remove(node);
		}
		tilePanes[newX][newY].getChildren().add(node);
	}
	
	/**
	 * Handles moving an enemy node and updating its health map reference.
	 */
	private void handleEnemyMove(GameUpdateEvent event) {
		StackPane oldPane = tilePanes[event.oldX][event.oldY];
		StackPane newPane = tilePanes[event.newX][event.newY];
		
		// 1. Find the enemy StackPane (which holds the rect and label)
		Node enemyNode = oldPane.getChildren().stream()
			.filter(node -> node.getId() != null && node.getId().startsWith("enemy_"))
			.findFirst().orElse(null);

		if (enemyNode != null) {
			// 2. Move the node visually
			oldPane.getChildren().remove(enemyNode);
			newPane.getChildren().add(enemyNode);
			
			// 3. Update the key in the health label map
			String oldKey = event.oldX + "," + event.oldY;
			String newKey = event.newX + "," + event.newY;
			
			Label hpLabel = enemyHealthLabels.remove(oldKey);
			if (hpLabel != null) {
				enemyHealthLabels.put(newKey, hpLabel);
				enemyNode.setId("enemy_" + newKey); // Update ID for future lookups
			}
		}
	}
	
	/**
	 * Updates the text on an existing enemy's health label.
	 */
	private void updateHealthLabel(int x, int y, int newHealth) {
		Label hpLabel = enemyHealthLabels.get(x + "," + y);
		if (hpLabel != null) {
			hpLabel.setText(String.valueOf(newHealth));
		}
	}

	/**
	 * Removes an entity's node from the view.
	 */
	private void removeEntityNode(int x, int y) {
		StackPane targetPane = tilePanes[x][y];
		Node enemyNode = targetPane.getChildren().stream()
				.filter(node -> node.getId() != null && node.getId().startsWith("enemy_"))
				.findFirst().orElse(null);

		if (enemyNode != null) {
			targetPane.getChildren().remove(enemyNode);
			enemyHealthLabels.remove(x + "," + y);
		}
	}

	/**
	 * Shows a temporary visual hit effect on a tile (e.g., when an entity hits a wall).
	 */
	private void showAttackEffect(int x, int y) {
		StackPane targetPane = tilePanes[x][y];

		Rectangle flash = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#f1c40f")); 
		flash.setOpacity(0.9);

		targetPane.getChildren().add(flash);

		FadeTransition ft = new FadeTransition(Duration.millis(300), flash);
		ft.setFromValue(0.9);
		ft.setToValue(0.0);

		ft.setOnFinished(e -> targetPane.getChildren().remove(flash));

		ft.play();
	}

	public GridPane getGridView() {
		return gridView;
	}
}