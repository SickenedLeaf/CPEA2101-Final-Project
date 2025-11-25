package com.pushknight;

import com.pushknight.controllers.MenuController;
import com.pushknight.controllers.GameController;
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
 * Sets up the JavaFX window, menu, and game loop.
 * Compatible with Eclipse IDE and manual JavaFX SDK setup.
 */
public class Main extends Application {
    private Stage primaryStage;
    private GameState currentState;
    
    // Menu components
    private MenuController menuController;
    private GameController gameController;
    
    // Game components
    private Canvas canvas;
    private GraphicsContext gc;
    private Scene gameScene;
    private long lastUpdateTime;
    private AnimationTimer gameLoop;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.currentState = GameState.MENU;
            
            // Set up stage
            primaryStage.setTitle("Push Knight Peril");
            primaryStage.setResizable(false);
            
            // Handle window close
            primaryStage.setOnCloseRequest(e -> {
                System.exit(0);
            });
            
            // Initialize game scene (but don't show it yet)
            initializeGameScene();
            
            // Initialize menu (depends on canvas/scene)
            initializeMenu();
            
            // Show menu first
            showMenu();
            
            // Show window
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the menu system.
     */
    private void initializeMenu() {
        // Use the controllers that exist in the codebase
        menuController = new MenuController();
        gameController = new GameController();

        // Provide rendering and input scene to both controllers
        menuController.setGraphicsContext(gc);
        menuController.setScene(gameScene);

        gameController.setGraphicsContext(gc);
        gameController.setScene(gameScene);

        // Connect the menu to the game controller for state transitions
        menuController.setGameController(gameController);
    }
    
    /**
     * Shows the main menu.
     */
    private void showMenu() {
        currentState = GameState.MENU;
        // Ensure the menu scene is shown and menu is rendered
        primaryStage.setScene(gameScene);
        stopGameLoop();
        menuController.render();
    }
    
    /**
     * Initializes the game scene and components.
     */
    private void initializeGameScene() {
        // Create canvas for rendering with fixed size
        canvas = new Canvas(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        canvas.setWidth(Constants.WINDOW_WIDTH);
        canvas.setHeight(Constants.WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // Create root pane
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        
        // Create game scene with fixed dimensions
        gameScene = new Scene(root, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Initialize game state
        lastUpdateTime = System.nanoTime();
    }
    
    /**
     * Starts the game.
     */
    private void startGame() {
        currentState = GameState.PLAYING;
        primaryStage.setScene(gameScene);
        
        // Start game loop
        startGameLoop();
    }
    
    /**
     * Starts the game loop using AnimationTimer.
     */
    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        lastUpdateTime = System.nanoTime();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentState != GameState.PLAYING) {
                    return; // Only update when playing
                }
                
                // Calculate delta time
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert to seconds
                lastUpdateTime = now;
                
                // Update game logic
                update(deltaTime, now);
                
                // Render game
                render();
            }
        };
        gameLoop.start();
    }
    
    /**
     * Stops the game loop.
     */
    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }
    
    /**
     * Updates game logic.
     * 
     * @param deltaTime Time elapsed since last frame in seconds
     * @param now Current timestamp in nanoseconds
     */
    private void update(double deltaTime, long now) {
        // TODO: Update game entities, systems, etc.
        // This will be implemented as we add game systems
    }
    
    /**
     * Renders the game to the canvas.
     */
    private void render() {
        // Clear canvas
        gc.clearRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Set background color
        gc.setFill(javafx.scene.paint.Color.DARKGRAY);
        gc.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Draw grid system
        drawGrid();
        
        // TODO: Render game entities
        // This will be implemented as we add game entities
        
        // Temporary: Draw a simple test to verify rendering works
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillText("Push Knight Peril - Game Running", 10, 30);
        gc.fillText("Press ESC to return to menu (coming soon)", 10, 50);
    }
    
    /**
     * Draws the grid system on the playing field.
     * This shows where the player and enemies can walk.
     * Uses fixed dimensions to ensure consistency across different screens.
     */
    private void drawGrid() {
        // Use fixed window dimensions for consistency (window is not resizable)
        double canvasWidth = Constants.WINDOW_WIDTH;
        double canvasHeight = Constants.WINDOW_HEIGHT;
        
        // Calculate exact number of full cells that fit (no partial cells)
        // This ensures no cutoff tiles on the edges
        int numCols = (int) (canvasWidth / Constants.GRID_CELL_SIZE);
        int numRows = (int) (canvasHeight / Constants.GRID_CELL_SIZE);
        
        // Calculate the actual grid area (only full cells, no partial cells)
        double gridWidth = numCols * Constants.GRID_CELL_SIZE;
        double gridHeight = numRows * Constants.GRID_CELL_SIZE;
        
        // Set grid line color (lighter gray for visibility)
        gc.setStroke(javafx.scene.paint.Color.rgb(100, 100, 100, 0.5));
        gc.setLineWidth(1.0);
        
        // Draw vertical grid lines (only within full cell boundaries)
        for (int col = 0; col <= numCols; col++) {
            double x = col * Constants.GRID_CELL_SIZE;
            // Only draw if within grid bounds
            if (x <= gridWidth) {
                gc.strokeLine(x, 0, x, gridHeight);
            }
        }
        
        // Draw horizontal grid lines (only within full cell boundaries)
        for (int row = 0; row <= numRows; row++) {
            double y = row * Constants.GRID_CELL_SIZE;
            // Only draw if within grid bounds
            if (y <= gridHeight) {
                gc.strokeLine(0, y, gridWidth, y);
            }
        }
        
        // Draw slightly darker lines every 5 cells for easier navigation
        gc.setStroke(javafx.scene.paint.Color.rgb(80, 80, 80, 0.7));
        gc.setLineWidth(1.5);
        
        // Draw thicker vertical lines every 5 columns (only within bounds)
        for (int col = 0; col <= numCols; col += 5) {
            double x = col * Constants.GRID_CELL_SIZE;
            if (x <= gridWidth) {
                gc.strokeLine(x, 0, x, gridHeight);
            }
        }
        
        // Draw thicker horizontal lines every 5 rows (only within bounds)
        for (int row = 0; row <= numRows; row += 5) {
            double y = row * Constants.GRID_CELL_SIZE;
            if (y <= gridHeight) {
                gc.strokeLine(0, y, gridWidth, y);
            }
        }
    }
    
    /**
     * Shows the "How to Play" screen.
     * Placeholder for now - will be implemented later.
     */
    private void showHowToPlay() {
        // TODO: Implement How to Play screen
        System.out.println("How to Play - Coming soon!");
    }
    
    /**
     * Shows the settings screen.
     * Placeholder for now - will be implemented later.
     */
    private void showSettings() {
        // TODO: Implement Settings screen
        System.out.println("Settings - Coming soon!");
    }
    
    /**
     * Gets the current game state.
     * 
     * @return The current game state
     */
    public GameState getCurrentState() {
        return currentState;
    }
    
    /**
     * Sets the current game state.
     * 
     * @param state The new game state
     */
    public void setCurrentState(GameState state) {
        this.currentState = state;
        
        // Handle state transitions
        if (state == GameState.MENU) {
            stopGameLoop();
            showMenu();
        } else if (state == GameState.PLAYING) {
            startGame();
        }
    }
    
    /**
     * Main entry point.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

