package application;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler extends GameLauncher{
	private boolean upKeyPressed;
	private boolean upMoveExecuted;
	private boolean downKeyPressed;
	private boolean downMoveExecuted;
	private boolean leftKeyPressed;
	private boolean leftMoveExecuted;
	private boolean rightKeyPressed;
	private boolean rightMoveExecuted;
	
	public InputHandler() {
		this.upKeyPressed = false;
		this.upMoveExecuted = false;
		this.downKeyPressed = false;
		this.downMoveExecuted = false;
		this.leftKeyPressed = false;
		this.leftMoveExecuted = false;
		this.rightKeyPressed = false;
		this.rightMoveExecuted = false;
	}
	
	
	public void handleInput(Scene scene) {
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.W) { this.upKeyPressed = true; } 
			else if (event.getCode() == KeyCode.S) { this.downKeyPressed = true; } 
			else if (event.getCode() == KeyCode.A) { this.leftKeyPressed = true; } 
			else if (event.getCode() == KeyCode.D) { this.rightKeyPressed = true; } 
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
			this.upKeyPressed = false;
			this.upMoveExecuted = false; // Reset lock
			break;
		case A:
			this.leftKeyPressed = false;
			this.leftMoveExecuted = false; // Reset lock
			break;
		case S:
			this.downKeyPressed = false;
			this.downMoveExecuted = false; // Reset lock
			break;
		case D:
			this.rightKeyPressed = false;
			this.rightMoveExecuted = false; // Reset lock
			break;
		default: 
		}
	}
	
	//setters
	public void setUpMoveExecuted(boolean state) {
		this.upMoveExecuted = state;
	}
	public void setDownMoveExecuted(boolean state) {
		this.downMoveExecuted = state;
	}
	public void setRightMoveExecuted(boolean state) {
		this.rightMoveExecuted = state;
	}
	public void setLeftMoveExecuted(boolean state) {
		this.leftMoveExecuted = state;
	}
	
	//getters
	public boolean getUpMoveExecuted() {
		return this.upMoveExecuted;
	}
	public boolean getDownMoveExecuted() {
		return this.downMoveExecuted;
	}
	public boolean getRightMoveExecuted() {
		return this.rightMoveExecuted;
	}
	public boolean getLeftMoveExecuted() {
		return this.leftMoveExecuted;
	}
	public boolean getUpKeyPressed() {
		return this.upKeyPressed;
	}
	public boolean getDownKeyPressed() {
		return this.downKeyPressed;
	}
	public boolean getRightKeyPressed() {
		return this.rightKeyPressed;
	}
	public boolean getLeftKeyPressed() {
		return this.leftKeyPressed;
	}
}
