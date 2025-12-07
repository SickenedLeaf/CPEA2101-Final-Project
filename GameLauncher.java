package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


import application.GameLogic.GameUpdateEvent;

/**
 * GameLauncher (Controller/Main App): The entry point for the application.
 * It connects the GameLogic (Model) to the GamePanel (View) and runs the game loop.
 */
public class GameLauncher extends Application {

	private GameLogic logic;
	private GamePanel panel;
	private InputHandler input;

	// Game loop variables for throttling (reintroduced for stability)
	private long lastUpdateTimestamp = 0;
	private static final double MIN_MOVE_INTERVAL_MS = 100; 
	
	@Override
	public void start(Stage primaryStage) {
		// 1. Initialize Model and View
		logic = new GameLogic();
		panel = new GamePanel(logic);
		input = new InputHandler();

		// 2. Setup Scene and Input
		Scene scene = new Scene(panel.getGridView());
		input.handleInput(scene);

		// 3. Start the Game Loop
		startGameLoop();

		// 4. Show Stage
		primaryStage.setTitle("Push Knight Peril");
		primaryStage.setScene(scene);
		primaryStage.show();
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
		if (input.getUpKeyPressed() && !input.getUpMoveExecuted()) { 
			dirY = -1; moveAttempted = true;
		} else if (input.getDownKeyPressed() && !input.getDownMoveExecuted()) { 
			dirY = 1; moveAttempted = true;
		} else if (input.getLeftKeyPressed() && !input.getLeftMoveExecuted()) { 
			dirX = -1; moveAttempted = true;
		} else if (input.getRightKeyPressed() && !input.getRightMoveExecuted()) { 
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
				if (dirY == -1) input.setUpMoveExecuted(actionTaken);
				else if (dirY == 1) input.setDownMoveExecuted(actionTaken);
				else if (dirX == -1) input.setLeftMoveExecuted(actionTaken);
				else if (dirX == 1) input.setRightMoveExecuted(actionTaken);
				
				// Process Events and Update View
				for (GameUpdateEvent event : logic.flushEvents()) {
				    panel.handleEvent(event);
				}
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}