package Devices;

public class Radar extends Device implements Fines.FineIssuer {
    private int speedLimit; // entero más práctico para límites en km/h

    public int getSpeedLimit() {
        return speedLimit; }
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit; }

    @Override public void generatesFine() { /* dominio a futuro */ }
}