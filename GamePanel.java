package application;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import application.GameLogic.GameUpdateEvent;

/**
 * GamePanel (View): Handles all graphical rendering and visual effects using JavaFX.
 * It reads the state from GameLogic but does not contain game rules.
 */
public class GamePanel {

	private static final int TILE_SIZE = 48; // Updated to match user's constant
	
	private GameLogic logic;
	private GridPane gridView = new GridPane();
	private StackPane[][] tilePanes = new StackPane[GameLogic.GRID_WIDTH][GameLogic.GRID_HEIGHT];
	
	private Rectangle playerNode;
	private Map<String, Label> enemyHealthLabels = new HashMap<>();

	public GamePanel(GameLogic logic) {
		this.logic = logic;
		initializeGameView();
	}

	/**
	 * Builds the initial JavaFX GridPane and all visual elements based on initial state.
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
				Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#7f8c8d"));
				tile.setStroke(Color.web("#34495e")); 
				tilePane.getChildren().add(tile);
				
				// 2. Entity Layer 
				Node entityNode = null;
				int type = logic.getEntityAt(x, y);

				if (type == 1) { 
					entityNode = createWallNode();
				} else if (type == 2) { 
					entityNode = createEnemyNode(x, y, logic.getEnemyHealthAt(x, y));
				}

				if (entityNode != null) {
					tilePane.getChildren().add(entityNode);
				}

				GridPane.setConstraints(tilePane, x, y);
				gridView.getChildren().add(tilePane);
			}
		}

		// 3. Player Node
		playerNode = new Rectangle(TILE_SIZE - 10, TILE_SIZE - 10, Color.web("#27ae60")); 
		playerNode.setArcWidth(15);
		playerNode.setArcHeight(15);

		// Initial player placement
		tilePanes[logic.getPlayerX()][logic.getPlayerY()].getChildren().add(playerNode);
	}
	
	private Rectangle createWallNode() {
		Rectangle wall = new Rectangle(TILE_SIZE, TILE_SIZE, Color.web("#34495e"));
		wall.setArcWidth(8);
		wall.setArcHeight(8);
		return wall;
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
	 */
	public void handleEvent(GameUpdateEvent event) {
		switch (event.type) {
			case PLAYER_MOVE:
				moveNode(playerNode, event.oldX, event.oldY, event.newX, event.newY);
				break;
			case ENEMY_MOVE:
				handleEnemyMove(event);
				break;
			case DAMAGE:
				updateHealthLabel(event.newX, event.newY, event.value);
				break;
			case REMOVE_ENTITY:
				removeEntityNode(event.newX, event.newY);
				break;
			case IMPACT:
				showAttackEffect(event.newX, event.newY);
				break;
			default:
				// Ignore other types
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