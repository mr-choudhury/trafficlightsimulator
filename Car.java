import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.util.List;
import java.util.Random;

/**
 * Class: CMSC 335
 * Instructor: Didier Vergamini
 * 
 * Description: Project 3
 * This class represents each Car and implements the Runnable class
 * Each instance of Car runs in its own thread.
 *  			
 * Due: 05/07/2024
 * 
 * Platform/compiler: Java Build 18.0.2.1/Eclipse IDE 4.24.0

 * 
 * @version 04/19/2024
 * @author Muhammad Choudhury
 */
@SuppressWarnings("unused")
public class Car extends Rectangle implements Runnable {
    private static final int MAX_SPEED = 200;
    private static final int MIN_SPEED = 20;
    private volatile int speed;
    private volatile int permanentSpeed;
	private int carNumber;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private SimpleIntegerProperty speedTable = new SimpleIntegerProperty();
    private SimpleIntegerProperty carNumberTable = new SimpleIntegerProperty();
    private SimpleIntegerProperty positionTable = new SimpleIntegerProperty();
    private Text carNumberLabel;
    private List<TrafficLight> trafficLights; 

    public Car(int carNumber, List<TrafficLight> trafficLights) {
        super(30, 20, Color.DARKBLUE);
        this.carNumber = carNumber;
        this.trafficLights = trafficLights;
        this.setX(-30);
        this.setY(75);
        
        carNumberLabel = new Text(String.valueOf(carNumber));
        carNumberLabel.setFill(Color.ORANGE);
        carNumberLabel.setY(90);
        
        Random rand = new Random();
        speed = rand.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED;
        permanentSpeed = speed;
        carNumberTable.set(carNumber);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Platform.runLater(() -> {
                	Color trafficLightColor = findNearestTrafficLightColor();
                    if (trafficLightColor.equals(Color.RED) || paused) {
                        speed = 0;
//                        for (int i=0; i<9; i++) {
//                        	System.out.println("Value of traffic light # " +i+" "+ trafficLights.get(i).equals(null));
//                        }
//                        System.out.println("Number of lights in list: " + trafficLights.size());
                    } else {
                    	speed = permanentSpeed;
                    }
                    updateCarPosition();
                });
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                running = false;
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public void stop() {
        running = false;
    }
    
    public Text getCarLabel() {
    	return carNumberLabel;
    }

    public SimpleIntegerProperty carNumberProperty() {
        return carNumberTable;
    }

    public SimpleIntegerProperty speedProperty() {
        return speedTable;
    }

    public SimpleIntegerProperty positionProperty() {
        return positionTable;
    }
    
    // Method to update the car position and table values
    private void updateCarPosition() {
        double newX = getX() + speed / 40.0;
        if (newX > 1000) newX = -30;
        setX(newX);
        carNumberLabel.setX(getX() + 12);
        speedTable.set(speed);
        positionTable.set((int) (getX() + 30) * 10 + 10);
    }
    
    // Method that finds the next nearest Traffic light in front of the car
    private Color findNearestTrafficLightColor() {
        TrafficLight nearestLight = null;
        double minDistance = Double.MAX_VALUE;

        for (TrafficLight light : trafficLights) {
            if (light != null) {
                double lightPosition = light.getPosition();
                double carFrontPosition = this.getX() + this.getWidth();

                if (carFrontPosition < lightPosition) {
                    double distance = lightPosition - carFrontPosition;
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestLight = light;
                    }
                }
            }
        }

        if (nearestLight != null && nearestLight.getCurrentLightColor() == Color.RED) {
            if (minDistance <= 5) { 	// Car is right at the line within 5 units
            	double newX = getX() + minDistance - 1;
                setX(newX);
                return Color.RED;
            }
        }
        return Color.GREEN;
    }

}
