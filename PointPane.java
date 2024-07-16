import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

/**
 * Class: CMSC 335
 * Instructor: Didier Vergamini
 * 
 * Description: Project 3
 * This class is the representation of the road and is a custom
 * Pane that will be the main animation portion of the UI.  
 *  			
 * Due: 05/07/2024
 * 
 * Platform/compiler: Java Build 18.0.2.1/Eclipse IDE 4.24.0

 * 
 * @version 04/19/2024
 * @author Muhammad Choudhury
 */
public class PointPane extends Pane {
    private Rectangle road;

    public PointPane() {
        // Create the road
        road = new Rectangle(0, 70, 1000, 30);
        road.setFill(Color.LIGHTGRAY);
        
        getChildren().add(road);

        setPrefSize(1000, 130);

        initializePositionMarkers();
    }
    
    // Method to add the position markers
    private void initializePositionMarkers() {
        double markerSpacing = 100;
        for (int i = 1; i <= 9; i++) {
            Text positionMarker = new Text(String.format("%dm", i * 1000));
            positionMarker.setFont(Font.font("Arial", 13));
            positionMarker.setX(i * markerSpacing - 19);
            positionMarker.setY(115);
            getChildren().add(positionMarker);
        }
    }
    
    // Method to clear all traffic light and car objects but keep the road and Position Markers
    public void clear() {
        getChildren().clear();
        getChildren().add(road);
        initializePositionMarkers();
    }

}
