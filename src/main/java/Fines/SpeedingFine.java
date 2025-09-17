package Fines;

/** Multa concreta de velocidad (guarda velocidades medidas). */
public class SpeedingFine extends Fine {
    private double carSpeed;
    private double speedLimit;

    public SpeedingFine() {}

    public SpeedingFine(FineType type, double amount, int points, String plate,
                        String deviceId, String dateTime, double carSpeed, double speedLimit) {
        super(type, amount, points, plate, deviceId, dateTime);
        this.carSpeed = carSpeed;
        this.speedLimit = speedLimit;
    }

    public double getCarSpeed() {
        return carSpeed;
    }
    public void setCarSpeed(double carSpeed) {
        this.carSpeed = carSpeed;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }
    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }
}