package application;

import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SpriteAnimator {

    private final ImageView imageView;
    private Image spriteSheet;
    private int frameWidth, frameHeight;
    private int currentFrame = 0;
    private int rowIndex = 0;
    private long frameDuration = 150_000_000; // default 150ms
    private long lastUpdate = 0;
    
    private int startColumn = 0;
    private int frameCount = 1;

    private boolean looping = true;
    private AnimationTimer timer;

    // Callback when animation ends
    private Runnable onAnimationEnd;

    public SpriteAnimator(Image spriteSheet, int frameWidth, int frameHeight) {
        this.spriteSheet = spriteSheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.spriteSheet = spriteSheet;
        this.imageView = new ImageView(spriteSheet);
        this.imageView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
    }

    public ImageView getImageView() {
        return imageView;
    }


    public void playAction(int rowIndex, int startColumn, int frameCount, long frameDurationNs, boolean looping, Runnable onAnimationEnd) {
        this.rowIndex = rowIndex;
        this.startColumn = startColumn;
        this.frameCount = frameCount;
        this.currentFrame = 0; // reset relative frame index
        this.frameDuration = frameDurationNs;
        this.looping = looping;
        this.onAnimationEnd = onAnimationEnd;

        if (timer != null) timer.stop();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= frameDuration) {
                    updateFrame();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void updateFrame() {
        int x = (startColumn + currentFrame) * frameWidth;
        int y = rowIndex * frameHeight;

        imageView.setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));

        currentFrame++;
        if (currentFrame >= frameCount) {
            if (looping) {
                currentFrame = 0;
            } else {
                timer.stop();
                if (onAnimationEnd != null) {
                    onAnimationEnd.run();
                }
            }
        }
    }
}
