package Devices;

public class ParkingCamera extends Device implements Fines.FineIssuer {
    private int toleranceTime; // segundos

    public int getToleranceTime() {
        return toleranceTime; }
    public void setToleranceTime(int toleranceTime) {
        this.toleranceTime = toleranceTime; }

    @Override public void generatesFine() { /* dominio a futuro */ }
}