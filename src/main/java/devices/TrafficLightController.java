// devices/TrafficLightController.java
package devices;

public class TrafficLightController extends Device implements Runnable {
    private TrafficLight principalTrafficLight;
    private TrafficLight secondaryTrafficLight;

    public TrafficLightController() {}

    public TrafficLightController(String id, String address,
                                  String principalStreet, String principalOrientation, boolean principalP,
                                  String secondaryStreet, String secondaryOrientation, boolean principalS) {
        super(id, address);
        this.principalTrafficLight = new TrafficLight(principalStreet, principalOrientation, principalP);
        this.secondaryTrafficLight = new TrafficLight(secondaryStreet, secondaryOrientation, principalS);
    }

    public TrafficLight getPrincipalTrafficLight()  { return principalTrafficLight; }
    public TrafficLight getSecondaryTrafficLight()  { return secondaryTrafficLight; }

    public void setPrincipalStatus(TrafficLightStatus status){ principalTrafficLight.setPrincipalStatus(status); }
    public void setSecondaryStatus(TrafficLightStatus status){ secondaryTrafficLight.setSecondaryStatus(status); }

    @Override public void run() { /* el ciclo real lo maneja tu TrafficLightCycleService */ }

    @Override
    public String toString() {
        return "Principal=" + principalTrafficLight + " | Secondary=" + secondaryTrafficLight;
    }

    // Si quisieras efectos extra al fallar/reparar (ej, avisar a CentralState), podés overridear:
    // @Override public void fail()   { super.fail();   /* hook extra */ }
    // @Override public void repair() { super.repair(); /* hook extra */ }
}
