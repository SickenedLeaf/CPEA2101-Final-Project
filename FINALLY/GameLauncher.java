package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class GameLauncher extends Application {
    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 720;
    
    private Stage primaryStage;
    private GameLogic logic;
    private GamePanel panel;
    private InputHandler input;
    private AnimationTimer gameLoop;
    
    private LevelSelectView levelSelectView;
    
    private long lastPlayerMoveTime = 0;
    private static final double MIN_MOVE_INTERVAL_MS = 150;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        showLevelSelect();
        
        primaryStage.setTitle("Push Knight Peril - Wave Survival");
        primaryStage.show();
    }
    
    private void showLevelSelect() {
        levelSelectView = new LevelSelectView(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        levelSelectView.setOnLevel1Selected(() -> startGame(1));
        levelSelectView.setOnLevel2Selected(() -> startGame(2));
        levelSelectView.setOnLevel3Selected(() -> startGame(3));
        levelSelectView.setOnEndlessModeSelected(() -> startGame(0));
        levelSelectView.setOnBack(() -> {
            System.out.println("Back to menu");
        });
        
        primaryStage.setScene(levelSelectView.getScene());
    }
    
    private void startGame(int levelNumber) {
        System.out.println("[GAME] Starting " + 
            (levelNumber == 0 ? "Endless Mode" : "Level " + levelNumber));
        
        logic = new GameLogic(levelNumber);
        panel = new GamePanel(logic);
        input = new InputHandler();
        
        Scene gameScene = new Scene(panel.getGridView());
        input.handleInput(gameScene);
        
        startGameLoop();
        
        primaryStage.setScene(gameScene);
    }
    
    private void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!logic.getPlayer().isAlive()) {
                    handleGameOver(false);
                    return;
                }
                
                if (logic.isLevelComplete()) {
                    handleGameOver(true);
                    return;
                }
                
                logic.updateGame();
                
                updatePlayerInput();
                
                for (GameUpdateEvent event : logic.flushEvents()) {
                    panel.handleEvent(event);
                }
            }
        };
        
        gameLoop.start();
    }
    
    private void updatePlayerInput() {
        long currentTime = System.currentTimeMillis();
        
        // Handle push action (SPACEBAR)
        if (input.getSpaceKeyPressed() && !input.getSpacePushExecuted()) {
            boolean pushPerformed = logic.attemptPushAction();
            if (pushPerformed) {
                input.setSpacePushExecuted(true);
            }
        }
        
        // Handle movement
        if (currentTime - lastPlayerMoveTime < MIN_MOVE_INTERVAL_MS) {
            return;
        }
        
        int dirX = 0;
        int dirY = 0;
        boolean moveAttempted = false;
        
        if (input.getUpKeyPressed() && !input.getUpMoveExecuted()) {
            dirY = -1;
            moveAttempted = true;
        } else if (input.getDownKeyPressed() && !input.getDownMoveExecuted()) {
            dirY = 1;
            moveAttempted = true;
        } else if (input.getLeftKeyPressed() && !input.getLeftMoveExecuted()) {
            dirX = -1;
            moveAttempted = true;
        } else if (input.getRightKeyPressed() && !input.getRightMoveExecuted()) {
            dirX = 1;
            moveAttempted = true;
        }
        
        if (dirX != 0 && dirY != 0) {
            dirX = 0;
        }
        
        if (moveAttempted && (dirX != 0 || dirY != 0)) {
            boolean actionTaken = logic.attemptMove(dirX, dirY);
            
            if (actionTaken) {
                lastPlayerMoveTime = currentTime;
                
                if (dirY == -1) input.setUpMoveExecuted(true);
                else if (dirY == 1) input.setDownMoveExecuted(true);
                else if (dirX == -1) input.setLeftMoveExecuted(true);
                else if (dirX == 1) input.setRightMoveExecuted(true);
            }
        }
    }
    
    private void handleGameOver(boolean victory) {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        SpawnSystem spawn = logic.getSpawnSystem();
        
        String title = victory ? "LEVEL COMPLETE!" : "GAME OVER";
        String message;
        
        if (victory) {
            message = "Congratulations! You completed Level " + spawn.getLevelNumber() + "!\n\n" +
                     "Final Wave: " + spawn.getCurrentWave() + "\n" +
                     "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                     "Would you like to play again?";
        } else {
            if (spawn.isEndlessMode()) {
                message = "You survived until Wave " + spawn.getCurrentWave() + "!\n" +
                         "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                         "Would you like to try again?";
            } else {
                message = "You were defeated on Wave " + spawn.getCurrentWave() + "\n" +
                         "Enemies Defeated: " + spawn.getEnemiesDefeated() + "\n\n" +
                         "Would you like to try again?";
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        ButtonType playAgain = new ButtonType("Play Again");
        ButtonType levelSelect = new ButtonType("Level Select");
        ButtonType quit = new ButtonType("Quit");
        
        alert.getButtonTypes().setAll(playAgain, levelSelect, quit);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == playAgain) {
                if (spawn.isEndlessMode()) {
                    startGame(0);
                } else {
                    startGame(spawn.getLevelNumber());
                }
            } else if (response == levelSelect) {
                showLevelSelect();
            } else {
                System.exit(0);
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
