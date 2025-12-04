package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Map;
import application.GameLogic.GameUpdateEvent;

/**
 * GameLauncher (Controller/Main App): The entry point for the application.
 * It connects the GameLogic (Model) to the GamePanel (View) and runs the game loop.
 */
public class GameLauncher extends Application {

	// --- Key State Tracking ---
	private boolean upKeyPressed = false;
	private boolean upMoveExecuted = false;
	private boolean downKeyPressed = false;
	private boolean downMoveExecuted = false;
	private boolean leftKeyPressed = false;
	private boolean leftMoveExecuted = false;
	private boolean rightKeyPressed = false;
	private boolean rightMoveExecuted = false;

	private GameLogic logic;
	private GamePanel panel;

	// Game loop variables for throttling (reintroduced for stability)
	private long lastUpdateTimestamp = 0;
	private static final double MIN_MOVE_INTERVAL_MS = 100; 
	
	@Override
	public void start(Stage primaryStage) {
		// 1. Initialize Model and View
		logic = new GameLogic();
		panel = new GamePanel(logic);

		// 2. Setup Scene and Input
		Scene scene = new Scene(panel.getGridView());
		handleInput(scene);

		// 3. Start the Game Loop
		startGameLoop();

		// 4. Show Stage
		primaryStage.setTitle("Push Knight Peril (MVC)");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * Sets up the keyboard input handler.
	 * NOTE: We only set the *KeyPressed* flag here. *MoveExecuted* is handled in update().
	 */
	private void handleInput(Scene scene) {
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.W) { upKeyPressed = true; } 
			else if (event.getCode() == KeyCode.S) { downKeyPressed = true; } 
			else if (event.getCode() == KeyCode.A) { leftKeyPressed = true; } 
			else if (event.getCode() == KeyCode.D) { rightKeyPressed = true; } 
			event.consume();
		});

		// Key Released: Clears both flags for the next move cycle
		scene.setOnKeyReleased(event -> {
			keyReleased(event.getCode());
			event.consume();
		});
	}
	
	private void keyReleased(KeyCode keyCode) {
		switch (keyCode) {
		case W:
			upKeyPressed = false;
			upMoveExecuted = false; // Reset lock
			break;
		case A:
			leftKeyPressed = false;
			leftMoveExecuted = false; // Reset lock
			break;
		case S:
			downKeyPressed = false;
			downMoveExecuted = false; // Reset lock
			break;
		case D:
			rightKeyPressed = false;
			rightMoveExecuted = false; // Reset lock
			break;
		default: 
		}
	}

	/**
	 * Starts the JavaFX game loop using AnimationTimer.
	 */
	private void startGameLoop() {
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				update();
			}
		}.start();
	}

	/**
	 * The core game update method. Enforces single-tap movement.
	 */
	private void update() {
		long currentTime = System.currentTimeMillis();
		
		// 1. Check Throttle
		if (currentTime - lastUpdateTimestamp < MIN_MOVE_INTERVAL_MS) {
			return;
		}

		int dirX = 0;
		int dirY = 0;
		boolean moveAttempted = false;

		// 2. Determine Direction & Check Execution Lock (The key change)
		if (upKeyPressed && !upMoveExecuted) { 
			dirY = -1; moveAttempted = true;
		} else if (downKeyPressed && !downMoveExecuted) { 
			dirY = 1; moveAttempted = true;
		} else if (leftKeyPressed && !leftMoveExecuted) { 
			dirX = -1; moveAttempted = true;
		} else if (rightKeyPressed && !rightMoveExecuted) { 
			dirX = 1; moveAttempted = true;
		}
		
		// Prevent diagonal movement (if multiple keys pressed)
		if (dirX != 0 && dirY != 0) { 
			dirX = 0; // Prioritize vertical movement if both pressed
		}
		
		// 3. Attempt Logic Update
		if (moveAttempted && (dirX != 0 || dirY != 0)) {
			
			boolean actionTaken = logic.attemptMove(dirX, dirY);
			
			if (actionTaken) {
				// 4. Successful Move: Lock the execution and process events
				lastUpdateTimestamp = currentTime;

				// Set the specific execution lock flag for the direction that succeeded
				if (dirY == -1) upMoveExecuted = true;
				else if (dirY == 1) downMoveExecuted = true;
				else if (dirX == -1) leftMoveExecuted = true;
				else if (dirX == 1) rightMoveExecuted = true;
				
				// Process Events and Update View
				Map<String, GameUpdateEvent> events = logic.flushEvents();
				for (GameUpdateEvent event : events.values()) {
					panel.handleEvent(event);
				}
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}