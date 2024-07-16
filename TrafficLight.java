import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class: CMSC 335
 * Instructor: Didier Vergamini
 * 
 * Description: Project 3
 * This class represents each Traffic light and implements the Runnable class
 * Each instance of Traffic light runs in its own thread.
 *  			
 * Due: 05/07/2024
 * 
 * Platform/compiler: Java Build 18.0.2.1/Eclipse IDE 4.24.0

 * 
 * @version 04/19/2024
 * @author Muhammad Choudhury
 */
public class TrafficLight implements Runnable {
	private final int position;
    private final Circle lightCircle;
    private final Line centerLine;
    private final Label countdownLabel;
    private Thread lightThread;
    private final Random random = new Random();

    private static final int RED_DURATION = 7000;
    private static final int GREEN_DURATION = 5000;
    private static final int YELLOW_DURATION = 2000;

    private ReentrantLock pauseLock = new ReentrantLock();

    private final ObjectProperty<Color> lightColor = new SimpleObjectProperty<>(Color.RED);
    private IntegerProperty countdown = new SimpleIntegerProperty(RED_DURATION);

    private volatile int startDuration;

    public TrafficLight(int position) {
        this.position = position;

        lightCircle = new Circle(position, 50, 13);
        lightCircle.setFill(Color.RED);

        centerLine = new Line(position, 50, position, 100);
        centerLine.setStrokeWidth(2);
        centerLine.setStroke(Color.RED);

        countdownLabel = new Label();
        countdownLabel.setLayoutX(position - 10);
        countdownLabel.setLayoutY(20);
        countdownLabel.textProperty().bind(Bindings.format("%.2f", countdown.divide(1000.0)));

        lightCircle.fillProperty().bind(lightColor);
        centerLine.strokeProperty().bind(lightColor);
    }

    @Override
    public void run() {
        try {
            // Randomly determine the starting state and corresponding duration
            int startIndex = random.nextInt(3);
            Color startColor = startIndex == 0 ? Color.RED : startIndex == 1 ? Color.GREEN : Color.YELLOW;
            startDuration = startIndex == 0 ? RED_DURATION : startIndex == 1 ? GREEN_DURATION : YELLOW_DURATION;

            lightColor.set(startColor);
            countdown.set(startDuration);

            while (!Thread.currentThread().isInterrupted()) {
                long startTime = System.currentTimeMillis();
                long endTime = startTime + startDuration;

                while (System.currentTimeMillis() < endTime) {
                    if (pauseLock.isLocked()) {
                        synchronized (pauseLock) {
                            try {
                                while (pauseLock.isLocked()) {
                                    long pausedTime = System.currentTimeMillis();
                                    pauseLock.wait();
                                    long resumeTime = System.currentTimeMillis();
                                    endTime += (resumeTime - pausedTime);
                                }
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                    }

                    long remainingTime = endTime - System.currentTimeMillis();
                    if (remainingTime > 0) {
                        Platform.runLater(() -> countdown.set(Math.max(0, (int) remainingTime)));
                        Thread.sleep(10);
                    } else {
                        break;
                    }
                }

                if (Thread.currentThread().isInterrupted() || pauseLock.isLocked()) continue;

                synchronized (this) {
                    Color nextColor = getNextState(lightColor.get());
                    startDuration = getNextDuration(nextColor);
                    Platform.runLater(() -> {
                        lightColor.set(nextColor);
                        countdown.set(startDuration);
                    });
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Method to get the next color state
    private Color getNextState(Color current) {
        return current == Color.RED ? Color.GREEN : current == Color.GREEN ? Color.YELLOW : Color.RED;
    }

    // Method to get the next time duration
    private int getNextDuration(Color nextColor) {
        return nextColor == Color.GREEN ? GREEN_DURATION : nextColor == Color.YELLOW ? YELLOW_DURATION : RED_DURATION;
    }

    public Circle getLightCircle() {
        return lightCircle;
    }

    public Line getCenterLine() {
        return centerLine;
    }

    public Label getCountdownLabel() {
        return countdownLabel;
    }

    // Method that starts the thread
    public void start() {
        lightThread = new Thread(this);
        lightThread.start();
    }

    // Method that Stops the thread
    public void stop() {
        lightThread.interrupt();
    }

    public void pause() {
        pauseLock.lock();
    }

    public void resume() {
        synchronized (pauseLock) {
            pauseLock.unlock();
            pauseLock.notifyAll();
        }
    }
    
    public Color getCurrentLightColor() {
        return lightColor.get();
    }
    
    public int getPosition() {
        return position;
    }

}
