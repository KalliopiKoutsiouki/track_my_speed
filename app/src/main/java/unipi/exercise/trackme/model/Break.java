package unipi.exercise.trackme.model;

import java.io.Serializable;

public class Break implements Serializable {
    private final double longitude;
    private final double latitude;
    private final String timestamp;
    private final int speedChange;

    public Break(double longitude, double latitude, String timestamp, int speedChange) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.speedChange = speedChange;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getSpeedChange() {
        return speedChange;
    }
}