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

    // --- ✅ INICIO: POLIMORFISMO DE MANTENIMIENTO ---

    /**
     * Al fallar, (polimorfismo) además de setear el estado,
     * le pedimos al contexto (AppContext) que pause el ciclo de cascada.
     */
    @Override
    public void fail(IMaintenanceContext context) {
        super.fail(context); // Llama a Device.java -> setStatus(FAILURE)
        if (context != null) {
            context.pauseTrafficLight(getDeviceId());
        }
    }

    /**
     * Al reparar, (polimorfismo) además de setear el estado,
     * le pedimos al contexto (AppContext) que reanude el ciclo de cascada.
     */
    @Override
    public void repair(IMaintenanceContext context) {
        super.repair(context); // Llama a Device.java -> setStatus(NORMAL)
        if (context != null) {
            context.resumeTrafficLight(getDeviceId());
        }
    }

    /**
     * Al pasar a intermitente, (polimorfismo) además de setear el estado,
     * le pedimos al contexto (AppContext) que pause el ciclo de cascada.
     */
    @Override
    public void intermittent(IMaintenanceContext context) {
        super.intermittent(context); // Llama a Device.java -> setStatus(INTERMITTENT)
        if (context != null) {
            context.pauseTrafficLight(getDeviceId());
        }
    }
    // --- ✅ FIN: POLIMORFISMO DE MANTENIMIENTO ---
}
