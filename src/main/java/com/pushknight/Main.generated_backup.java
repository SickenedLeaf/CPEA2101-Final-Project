package com.pushknight;

import com.pushknight.controllers.GameController;
import com.pushknight.controllers.MenuController;
import com.pushknight.systems.GameDirector;
import com.pushknight.utils.Constants;
import com.pushknight.utils.GameState;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main application class for Push Knight Peril.
 * Sets up the JavaFX window and game loop.
 */
class MainGeneratedBackup extends Application {
    private Canvas canvas;
    private GraphicsContext gc;
    private long lastUpdateTime;

    private GameController gameController;
    private MenuController menuController;
    private GameState currentState;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create canvas for rendering
            canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // Create root pane
            StackPane root = new StackPane();
            root.getChildren().add(canvas);

            // Create scene
            Scene scene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

            // Set up stage
            primaryStage.setTitle("Push Knight Peril");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Handle window close
            primaryStage.setOnCloseRequest(e -> {
                System.exit(0);
            });

            // Initialize game controllers
            initializeGameControllers(scene);

            // Show window
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the game controllers and systems.
     *
     * @param scene The scene to attach input handlers to
     */
    private void initializeGameControllers(Scene scene) {
        // Initialize controllers
        gameController = new GameController();
        menuController = new MenuController();

        // Set up graphics contexts
        gameController.setGraphicsContext(gc);
        menuController.setGraphicsContext(gc);

        // Set up scenes for input
        gameController.setScene(scene);
        menuController.setScene(scene);

        // Connect controllers
        menuController.setGameController(gameController);

        // Initialize starting state
        currentState = GameState.MENU;

        // Start the game loop
        startGameLoop();
    }

    /**
     * Starts the game loop using AnimationTimer.
     */
    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate delta time
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert to seconds
                lastUpdateTime = now;

                // Update game logic based on current state
                update(deltaTime, now);

                // Render based on current state
                render();
            }
        }.start();
    }

    /**
     * Updates game logic based on the current game state.
     *
     * @param deltaTime Time elapsed since last frame in seconds
     * @param now Current timestamp in nanoseconds
     */
    private void update(double deltaTime, long now) {
        // Update based on current state
        GameState gameControllerState = gameController.getCurrentState();
        GameState menuControllerState = menuController.getCurrentState();

        // Use the state from the game controller as the primary state
        currentState = gameControllerState;

        switch (currentState) {
            case PLAYING:
                // The game controller handles all gameplay updates
                // No additional update logic needed here
                break;
            case MENU:
                // Menu updates would go here if needed
                break;
            case PAUSED:
                // Paused state - minimal updates
                break;
            case GAME_OVER:
                // Game over updates would go here if needed
                break;
            case UPGRADE_SELECTION:
                // Upgrade selection updates would go here if needed
                break;
        }
    }

    /**
     * Renders the game based on the current game state.
     */
    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);

        switch (currentState) {
            case PLAYING:
            case PAUSED:
            case GAME_OVER:
            case UPGRADE_SELECTION:
                // Let the game controller handle rendering for gameplay states
                gameController.render();
                break;
            case MENU:
                // Let the menu controller handle rendering for menu state
                menuController.render();
                break;
        }
    }

    /**
     * Backup main entry point (renamed to avoid conflicts).
     *
     * @param args Command line arguments
     */
    public static void main_backup(String[] args) {
        launch(args);
    }
}
