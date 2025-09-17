package Fines;

import java.util.Objects;

/** Ubicación simple (lat/lon) en grados decimales. */
public class Location {
    private double latitude;
    private double longitude;

    public Location() {}
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override public String toString() {
        return latitude + "," + longitude;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location that = (Location) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0;
    }
    @Override public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}