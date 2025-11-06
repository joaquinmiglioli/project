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

    // ... (Getters/Setters y otros métodos sin cambios) ...
    public TrafficLight getPrincipalTrafficLight()  { return principalTrafficLight; }
    public TrafficLight getSecondaryTrafficLight()  { return secondaryTrafficLight; }
    public void setPrincipalStatus(TrafficLightStatus status){ principalTrafficLight.setPrincipalStatus(status); }
    public void setSecondaryStatus(TrafficLightStatus status){ secondaryTrafficLight.setSecondaryStatus(status); }
    @Override public void run() { /* el ciclo real lo maneja tu TrafficLightCycleService */ }
    @Override
    public String toString() {
        return "Principal=" + principalTrafficLight + " | Secondary=" + secondaryTrafficLight;
    }

    // --- ✅ INICIO: POLIMORFISMO DE MANTENIMIENTO (CON CONTEXTO) ---
    // (Estos son llamados por los botones de la UI)

    /**
     * Al fallar (desde la UI), además de setear el estado,
     * le pedimos al contexto (AppContext) que pause el ciclo de cascada.
     */
    @Override
    public void fail(IMaintenanceContext context) {
        super.fail(context); // Llama a Device.fail(context), que llama a Device.fail()
        if (context != null) {
            context.pauseTrafficLight(getDeviceId());
        }
    }

    /**
     * Al reparar (desde la UI), además de setear el estado,
     * le pedimos al contexto (AppContext) que reanude el ciclo de cascada.
     */
    @Override
    public void repair(IMaintenanceContext context) {
        super.repair(context); // Llama a Device.repair(context), que llama a Device.repair()
        if (context != null) {
            context.resumeTrafficLight(getDeviceId());
        }
    }

    /**
     * Al pasar a intermitente (desde la UI), además de setear el estado,
     * le pedimos al contexto (AppContext) que pause el ciclo de cascada.
     */
    @Override
    public void intermittent(IMaintenanceContext context) {
        super.intermittent(context); // Llama a Device.intermittent(context), que llama a Device.intermittent()
        if (context != null) {
            context.pauseTrafficLight(getDeviceId());
        }
    }
    // --- ✅ FIN: POLIMORFISMO DE MANTENIMIENTO ---

    // NOTA: Los métodos simples (fail(), repair(), intermittent()) no se
    // sobreescriben, por lo que se hereda la implementación base de Device.java
    // (que solo cambia el estado). ¡Perfecto para el simulador!
}
