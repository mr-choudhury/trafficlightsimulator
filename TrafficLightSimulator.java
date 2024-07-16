import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class: CMSC 335
 * Instructor: Didier Vergamini
 * 
 * Description: Project 3
 * Program that creates a JavaFX GUI that simulates Traffic Lights and
 * cars. Each traffic light runs on its own thread as well as each car.
 * Simulation can be started and will begin with selected number of lights and cars.
 * Traffic lights can be added or removed (minimum 1 light and maximum 9).
 * Cars can be added or removed (minimum 1 and maximum 20).
 * If simulation is stopped all threads are stopped.
 * Simulation can be paused - this will pause the state of all traffic lights and
 * All cars will be frozen by making the speed 0. Upon continue all the
 * traffic lights and cars will resume exactly where they were paused.
 * A road if provided with a length of 10,000m
 * Cars move in kph (however, for simulation purposes not shown in actual speed).
 * A live informational display table is provided to show the current speed and 
 * position of every car in the simulation.
 *  			
 * Due: 05/07/2024
 * 
 * Platform/compiler: Java Build 18.0.2.1/Eclipse IDE 4.24.0

 * 
 * @version 04/19/2024
 * @author Muhammad Choudhury
 */
@SuppressWarnings("unchecked")
public class TrafficLightSimulator extends Application {
    private final int MAXIMUM_TRAFFIC_LIGHTS = 9;
    private int numberTrafficLights = 3;
    private final int MAXIMUM_CARS = 20;
    private int numberCars = 3;
    private TrafficLight[] trafficLights = new TrafficLight[MAXIMUM_TRAFFIC_LIGHTS];
    private List<Car> cars = new ArrayList<>();
    private Button startStopButton, pauseContinueButton, addLightButton, removeLightButton, addCarButton, removeCarButton, exitButton;
    private Label titleLabel, informationLabel, currentTimeLabel, trafficLightCountLabel, carCountLabel, systemTime;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private PointPane pointPane = new PointPane();
    private TableView<Car> leftCarTable, rightCarTable;
    private ObservableList<Car> leftCars, rightCars;

    @Override
    public void start(Stage primaryStage) {
        // Create layout
        GridPane gridPane = new GridPane();
        gridPane.setVgap(20);
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        HBox timeDisplayBox = new HBox(5);
        timeDisplayBox.setAlignment(Pos.CENTER);  
        HBox lightControlBox = new HBox(10);
        lightControlBox.setAlignment(Pos.CENTER);
        HBox carControlBox = new HBox(10);
        carControlBox.setAlignment(Pos.CENTER);
        HBox startStopBox = new HBox(20);
        startStopBox.setAlignment(Pos.CENTER);
        HBox carsInformationBox = new HBox(10);
        carsInformationBox.setAlignment(Pos.CENTER);
        
        // Initialize the cars information tables/Lists
        leftCars = FXCollections.observableArrayList();
        rightCars = FXCollections.observableArrayList();
        leftCarTable = new TableView<>();
        leftCarTable.setMinWidth(300);
        leftCarTable.setMaxWidth(300);
        leftCarTable.setMinHeight(267);
        leftCarTable.setMaxHeight(267);
        rightCarTable = new TableView<>();
        rightCarTable.setMinWidth(300);
        rightCarTable.setMaxWidth(300);
        rightCarTable.setMinHeight(267);
        rightCarTable.setMaxHeight(267);
        setupCarTable(leftCarTable, leftCars);
        setupCarTable(rightCarTable, rightCars);
                
        // Create Labels
        titleLabel = new Label("Real Time Traffic Light Simulation Display");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
        informationLabel = new Label("Click Start to begin simulation");
        informationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        systemTime = new Label();
        systemTime.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        currentTimeLabel = new Label("Current System Time: ");
        currentTimeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        trafficLightCountLabel = new Label("Select the number of traffic lights: " + numberTrafficLights);
        carCountLabel = new Label("Select the number of cars: " + numberCars);
        
        // Start the Current system time thread
        CurrentTime currentTime = new CurrentTime(currentTimeLabel);

        //create the buttons, event handlers and properties
        startStopButton = new Button("Start");
        startStopButton.setPrefWidth(75);
        startStopButton.setOnAction(event -> handleStartStop(event));

        pauseContinueButton = new Button("Pause");
        pauseContinueButton.setPrefWidth(75);
        pauseContinueButton.setOnAction(event -> handlePauseContinue(event));
        pauseContinueButton.setDisable(true);

        addLightButton = new Button("+");
        addLightButton.setPrefWidth(30);
        addLightButton.setOnAction(event -> {
            if (numberTrafficLights < MAXIMUM_TRAFFIC_LIGHTS) {
                addTrafficLight();
            }
        });

        removeLightButton = new Button("-");
        removeLightButton.setPrefWidth(30);
        removeLightButton.setOnAction(event -> {
            if (numberTrafficLights > 1) {
                removeTrafficLight();
            }
        });
        
        addCarButton = new Button("+");
        addCarButton.setPrefWidth(30);
        addCarButton.setOnAction(event -> {
            if (numberCars < MAXIMUM_CARS) {
                addCar();
            }
        });

        removeCarButton = new Button("-");
        removeCarButton.setPrefWidth(30);
        removeCarButton.setOnAction(event -> {
            if (numberCars > 1) {
                removeCar();
            }
        });
        
        exitButton = new Button("Exit");
        exitButton.setPrefWidth(75);
        exitButton.setOnAction(event -> {
            currentTime.stop();
        	stopAllThreads();
        	Platform.exit();
        });
        
        // Add components to layout panes
        vBox.getChildren().addAll(titleLabel, informationLabel);
        timeDisplayBox.getChildren().addAll(currentTimeLabel, systemTime);
        lightControlBox.getChildren().addAll(trafficLightCountLabel, addLightButton, removeLightButton);
        carControlBox.getChildren().addAll(carCountLabel, addCarButton, removeCarButton);
        startStopBox.getChildren().addAll(startStopButton, pauseContinueButton, exitButton);
        carsInformationBox.getChildren().addAll(leftCarTable, rightCarTable);
        gridPane.add(vBox, 0, 0);
        gridPane.add(timeDisplayBox, 0, 1);
        gridPane.add(lightControlBox, 0, 2);
        gridPane.add(carControlBox, 0, 3);
        gridPane.add(startStopBox, 0, 4);
        gridPane.add(pointPane, 0, 5);
        gridPane.add(carsInformationBox, 0, 6);
        
        // Main Scene
        Scene scene = new Scene(gridPane, 1000, 700);
        primaryStage.setTitle("Real Time Traffic Light Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //Close all threads on forced Window Shutdown
        primaryStage.setOnCloseRequest((EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                currentTime.stop();
            	stopAllThreads();
                Platform.exit();
                System.exit(0);
            }
         });
    }

    // Method that starts all initial traffic light threads when Start is pressed
    private void addStartingTrafficLights() {
        for (int i = 0; i < numberTrafficLights; i++) {
            int position = 100 + i * 100;
            trafficLights[i] = new TrafficLight(position);
            pointPane.getChildren().addAll(trafficLights[i].getLightCircle(), trafficLights[i].getCenterLine(), trafficLights[i].getCountdownLabel());
        }
    }
    
    // Method that starts all initial car threads when Start is pressed
    private void addStartingCars() {
    	List<TrafficLight> trafficLightsList = Arrays.asList(trafficLights);
        for (int i = 0; i < numberCars; i++) {
            Car newCar = new Car(i + 1, trafficLightsList); 
            cars.add(newCar);
            Platform.runLater(() -> {
                pointPane.getChildren().addAll(newCar, newCar.getCarLabel());
            });
            new Thread(newCar).start();
        }
    }
    
    // Method that handles the Start,Stop button action
    private void handleStartStop(ActionEvent event) {
        if (!isRunning) {
            isRunning = true;
            pauseContinueButton.setDisable(false);
            startStopButton.setText("Stop");
            addStartingTrafficLights();
            addStartingCars();
            for (TrafficLight trafficLight : trafficLights) {
                if (trafficLight != null) trafficLight.start();
            }
            updateCarTables();
        } else {
            isRunning = false;
            pauseContinueButton.setDisable(true);
            pauseContinueButton.setText("Pause");
            isPaused = false;
            startStopButton.setText("Start");
            stopAllThreads();
            pointPane.clear();
            cars.clear();
            updateCarTables();
        }
    }
    
    // Method to stop all running threads for traffic lights and cars
    private void stopAllThreads() {
        for (int i = 0; i < 9; i++) {
        	if (trafficLights[i] != null) {
        		trafficLights[i].stop();
        		trafficLights[i] = null;
        	}
        }
        cars.forEach(Car::stop);
    }

    // Method that handles the Pause, Continue button action
    private void handlePauseContinue(ActionEvent event) {
        if (!isPaused) {
            isPaused = true;
            pauseContinueButton.setText("Continue");
            for (TrafficLight trafficLight : trafficLights) {
                if (trafficLight != null) trafficLight.pause();
            }
            for (Car car : cars) {
            	if (car != null) car.pause();
            }
        } else {
            isPaused = false;
            pauseContinueButton.setText("Pause");
            for (TrafficLight trafficLight : trafficLights) {
                if (trafficLight != null) trafficLight.resume();
            }
            for (Car car : cars) {
            	if (car != null) car.resume();
            }
        }
    }
	
    // Method that sets up the TableView for the running cars
    private void setupCarTable(TableView<Car> tableView, ObservableList<Car> carList) {
        TableColumn<Car, Integer> carNumberColumn = new TableColumn<>("Car Number");
        carNumberColumn.setPrefWidth(99); 
        carNumberColumn.setResizable(false); 
        TableColumn<Car, Integer> speedColumn = new TableColumn<>("Speed (kph)");
        speedColumn.setPrefWidth(99); 
        speedColumn.setResizable(false); 
        TableColumn<Car, Integer> positionColumn = new TableColumn<>("Position (m)");
        positionColumn.setPrefWidth(99); 
        positionColumn.setResizable(false); 

        carNumberColumn.setCellValueFactory(new PropertyValueFactory<>("carNumber"));
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        tableView.getColumns().addAll(carNumberColumn, speedColumn, positionColumn);
        tableView.setItems(carList);
    }
    
    // Method that updates the car information TableView when a car is removed or added
    private void updateCarTables() {
        leftCars.clear();
        rightCars.clear();

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            if (i < 10) {
                leftCars.add(car);
            } else{
                rightCars.add(car);
            }
        }
    }

    // Methods that adds a Traffic Light
    private void addTrafficLight() {
    	if (isRunning) {
            if (numberTrafficLights < MAXIMUM_TRAFFIC_LIGHTS) {
                int position = 100 + numberTrafficLights * 100;
                TrafficLight newLight = new TrafficLight(position);
                trafficLights[numberTrafficLights] = newLight;
                pointPane.getChildren().addAll(newLight.getLightCircle(), newLight.getCenterLine(), newLight.getCountdownLabel());
                newLight.start();
                if (isPaused)
                	newLight.pause();
                numberTrafficLights++;
                updateTrafficLightCount();
            }
    	} else {
            if (numberTrafficLights < MAXIMUM_TRAFFIC_LIGHTS) {
                numberTrafficLights++;
                updateTrafficLightCount();
            }
    	}
    	updateButtonStatus(numberTrafficLights, MAXIMUM_TRAFFIC_LIGHTS, addLightButton, removeLightButton);
    }

    // Methods that removes a Traffic Light
    private void removeTrafficLight() {
    	if (isRunning) {
            if (numberTrafficLights > 1) {
                TrafficLight lightToRemove = trafficLights[numberTrafficLights - 1];
                pointPane.getChildren().removeAll(lightToRemove.getLightCircle(), lightToRemove.getCenterLine(), lightToRemove.getCountdownLabel());
                lightToRemove.stop();
                trafficLights[numberTrafficLights - 1] = null;
                numberTrafficLights--;
                updateTrafficLightCount();
            }
    	} else {
            if (numberTrafficLights > 1) {
                numberTrafficLights--;
                updateTrafficLightCount();
            }
    	}
    	updateButtonStatus(numberTrafficLights, MAXIMUM_TRAFFIC_LIGHTS, addLightButton, removeLightButton);
    }
    
    // Methods that adds a Car
    private void addCar() {
    	List<TrafficLight> trafficLightsList = Arrays.asList(trafficLights);
    	if (isRunning) {
    		if (numberCars < MAXIMUM_CARS) {
                Car newCar = new Car(cars.size() + 1, trafficLightsList);
                cars.add(newCar);
                Platform.runLater(() -> {
                	pointPane.getChildren().addAll(newCar, newCar.getCarLabel());
                    updateCarTables();
                });
                new Thread(newCar).start();
                if (isPaused)
                	newCar.pause();
                numberCars++;
                updateCarCount();
    		}
    	} else {
    		if (numberCars < MAXIMUM_CARS) {
                numberCars++;
                updateCarCount();
    		}
    	}
    	updateButtonStatus(numberCars, MAXIMUM_CARS, addCarButton, removeCarButton);
    }

    // Methods that removes a Traffic Light
    private void removeCar() {
    	if (isRunning) {
            if (numberCars > 1) {
                Car carToRemove = cars.remove(cars.size() - 1);
                Platform.runLater(() -> {
                	pointPane.getChildren().removeAll(carToRemove, carToRemove.getCarLabel());
                    updateCarTables();
                });
                carToRemove.stop();
                numberCars--;
                updateCarCount();
            }
    	} else {
            if (numberCars > 1) {
                numberCars--;
                updateCarCount();
            }
    	}
    	updateButtonStatus(numberCars, MAXIMUM_CARS, addCarButton, removeCarButton);
    }

    // Method that enables and disables the add and remove buttons - generalized
    private void updateButtonStatus(int currentCount, int maxCount, Button increase, Button decrease) {
    	if (currentCount == maxCount) {
    		increase.setDisable(true);
    	} else {
    		increase.setDisable(false);
    	}
    	if (currentCount == 1) {
    		decrease.setDisable(true);
    	} else {
    		decrease.setDisable(false);
    	}
    		
    }

    // Method that updates the UI for running Traffic Lights
    private void updateTrafficLightCount() {
        trafficLightCountLabel.setText("Select the number of traffic lights: " + numberTrafficLights);
    }
    
    // Method that updates the UI for running Cars
    private void updateCarCount() {
    	carCountLabel.setText("Select the number of cars: " + numberCars);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
