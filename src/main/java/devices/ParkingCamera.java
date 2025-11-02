// devices/ParkingCamera.java
package devices;

public class ParkingCamera extends Device {
    private int toleranceSec;

    public ParkingCamera() {}
    public ParkingCamera(String id, String address, int toleranceSec) {
        super(id, address);
        this.toleranceSec = toleranceSec;
    }

    public int getToleranceSec() { return toleranceSec; }
    public void setToleranceSec(int toleranceSec) { this.toleranceSec = toleranceSec; }
}
