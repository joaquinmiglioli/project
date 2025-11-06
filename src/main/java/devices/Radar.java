package devices;

public class Radar extends Device {
    private int speedLimit;

    public Radar() {}
    public Radar(String id, String address, int limit) {
        super(id, address);
        this.speedLimit = limit;
    }

    public int getSpeedLimit() { return speedLimit; }
    public void setSpeedLimit(int speedLimit) { this.speedLimit = speedLimit; }
}
