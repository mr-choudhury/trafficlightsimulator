# trafficlightsimulator
A Java GUI application simulating real-time traffic conditions at intersections using multithreading for clocks, traffic lights, and car movements.

## Project Overview
This Java project, developed for a traffic congestion mitigation company, involves the creation of a Java GUI application that simulates traffic conditions at three major intersections. The project leverages Java Swing for the GUI, event handlers, listeners, and Java concurrency features such as threads.


## Project Components
CurrentTime.java: Manages the display of current timestamps at one-second intervals.
TrafficLight.java: Represents a traffic light at an intersection with red, yellow, and green signals.
Car.java: Models the behavior of cars, including their position (X, Y coordinates) and speed as they move through intersections.
PointPane.java: Serves as a graphical panel to display cars and their movement.
TrafficLightSimulator.java: Coordinates the overall simulation, integrating the traffic lights, cars, and timestamp updates.


## Key Features
Real-Time Clock: Displays the current time in one-second intervals.
Traffic Light Simulation: Shows the status of traffic lights at three intersections, updating in real time.
Car Movement: Tracks and displays the position and speed of up to three cars as they move through the intersections.
Concurrency: Each component (clock, traffic lights, cars) runs in its own thread, ensuring smooth and concurrent updates.
User Controls: Buttons to start, pause, stop, and resume the simulation, providing full control over the simulation's execution.
Scalability: The GUI allows for the addition of more cars and intersections in real time.


## Assumptions
Cars travel in a straight line, assuming Y = 0.
The distance between each traffic light is 1000 meters.
Cars stop instantly for red lights and continue through yellow and green lights without deceleration.


## How to Run
Compilation: Compile all Java files using a Java compiler. For example:
javac *.java


Execution: Run the main simulation class:
java TrafficLightSimulator


## Lessons Learned
Mastery of Java FX for creating interactive GUI components.
Understanding of Java concurrency and threading.
Practical experience in modeling real-world problems using Java.


## Assumptions and Limitations
Simplified physics with instant stops and no deceleration.
Straight-line travel only (Y = 0).
Fixed distance between traffic lights.
Maximum cars = 20.
Maximum Traffic Lights = 9.


## Conclusion
This project demonstrates strong proficiency in Java programming, particularly in GUI development and concurrency. The detailed simulation and interactive components showcase the ability to design and implement complex software solutions effectively.


