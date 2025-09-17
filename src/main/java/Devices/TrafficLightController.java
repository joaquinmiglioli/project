package Devices;

public class TrafficLightController extends Device implements Fines.FineIssuer, Runnable {
    private TrafficLight principalTrafficLight;
    private TrafficLight secondaryTrafficLight;

    public TrafficLightController() {}

    public TrafficLightController(String principalStreet, String principalOrientation, boolean principalP,
                                  String secondaryStreet, String secondaryOrientation, boolean principalS) {
        this.principalTrafficLight = new TrafficLight(principalStreet, principalOrientation, principalP);
        this.secondaryTrafficLight = new TrafficLight(secondaryStreet, secondaryOrientation, principalS);
    }

    public TrafficLight getPrincipalTrafficLight() {
        return principalTrafficLight; }
    public TrafficLight getSecondaryTrafficLight() {
        return secondaryTrafficLight; }

    public void setPrincipalStatus(TrafficLightStatus status){ principalTrafficLight.setPrincipalStatus(status); }
    public void setSecondaryStatus(TrafficLightStatus status){ secondaryTrafficLight.setSecondaryStatus(status); }

    @Override public void run() { /* ciclo/timeline si lo querés mover del Controller más adelante */ }
    @Override public void generatesFine() { /* si más tarde querés emitir desde acá */ }

    @Override public String toString() {
        return "Principal TrafficLight=" + principalTrafficLight + "    Secondary TrafficLight=" + secondaryTrafficLight;
    }
}