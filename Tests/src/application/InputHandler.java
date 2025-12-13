package application;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private boolean upKeyPressed;
    private boolean upMoveExecuted;
    private boolean downKeyPressed;
    private boolean downMoveExecuted;
    private boolean leftKeyPressed;
    private boolean leftMoveExecuted;
    private boolean rightKeyPressed;
    private boolean rightMoveExecuted;
    
    // Push action
    private boolean spaceKeyPressed;
    private boolean spacePushExecuted;
    
    public InputHandler() {
        this.upKeyPressed = false;
        this.upMoveExecuted = false;
        this.downKeyPressed = false;
        this.downMoveExecuted = false;
        this.leftKeyPressed = false;
        this.leftMoveExecuted = false;
        this.rightKeyPressed = false;
        this.rightMoveExecuted = false;
        this.spaceKeyPressed = false;
        this.spacePushExecuted = false;
    }
    
    public void handleInput(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W || event.getCode() == KeyCode.UP) {
                this.upKeyPressed = true;
            } else if (event.getCode() == KeyCode.S || event.getCode() == KeyCode.DOWN) {
                this.downKeyPressed = true;
            } else if (event.getCode() == KeyCode.A || event.getCode() == KeyCode.LEFT) {
                this.leftKeyPressed = true;
            } else if (event.getCode() == KeyCode.D || event.getCode() == KeyCode.RIGHT) {
                this.rightKeyPressed = true;
            } else if (event.getCode() == KeyCode.SPACE) {
                this.spaceKeyPressed = true;
            }
            event.consume();
        });

        scene.setOnKeyReleased(event -> {
            keyReleased(event.getCode());
            event.consume();
        });
    }
    
    private void keyReleased(KeyCode keyCode) {
        switch (keyCode) {
            case W:
            case UP:
                this.upKeyPressed = false;
                this.upMoveExecuted = false;
                break;
            case A:
            case LEFT:
                this.leftKeyPressed = false;
                this.leftMoveExecuted = false;
                break;
            case S:
            case DOWN:
                this.downKeyPressed = false;
                this.downMoveExecuted = false;
                break;
            case D:
            case RIGHT:
                this.rightKeyPressed = false;
                this.rightMoveExecuted = false;
                break;
            case SPACE:
                this.spaceKeyPressed = false;
                this.spacePushExecuted = false;
                break;
            default:
                break;
        }
    }
    
    public void setUpMoveExecuted(boolean state) { this.upMoveExecuted = state; }
    public void setDownMoveExecuted(boolean state) { this.downMoveExecuted = state; }
    public void setRightMoveExecuted(boolean state) { this.rightMoveExecuted = state; }
    public void setLeftMoveExecuted(boolean state) { this.leftMoveExecuted = state; }
    public void setSpacePushExecuted(boolean state) { this.spacePushExecuted = state; }
    
    public boolean getUpMoveExecuted() { return this.upMoveExecuted; }
    public boolean getDownMoveExecuted() { return this.downMoveExecuted; }
    public boolean getRightMoveExecuted() { return this.rightMoveExecuted; }
    public boolean getLeftMoveExecuted() { return this.leftMoveExecuted; }
    public boolean getSpacePushExecuted() { return this.spacePushExecuted; }
    
    public boolean getUpKeyPressed() { return this.upKeyPressed; }
    public boolean getDownKeyPressed() { return this.downKeyPressed; }
    public boolean getRightKeyPressed() { return this.rightKeyPressed; }
    public boolean getLeftKeyPressed() { return this.leftKeyPressed; }
    public boolean getSpaceKeyPressed() { return this.spaceKeyPressed; }
}