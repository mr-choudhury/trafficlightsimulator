import javafx.application.Platform;
import javafx.scene.control.Label;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* 
* This class will provide the current system time display updated in the UI once every second.  
* 
* Platform/compiler: Java Build 18.0.2.1/Eclipse IDE 4.24.0

* 
* @version 04/19/2024
* @author Muhammad Choudhury
*/
public class CurrentTime {
    private final Label timeLabel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public CurrentTime(Label timeLabel) {
        this.timeLabel = timeLabel;
        start();
    }

    private void start() {
        scheduler.scheduleAtFixedRate(this::updateTime, 0, 1, TimeUnit.SECONDS);
    }

    private void updateTime() {
        Platform.runLater(() -> timeLabel.setText("Current System Time: " + dateFormat.format(new Date())));
    }

    // Method that terminated the thread safely
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
