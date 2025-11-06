// devices/Device.java
package devices;

import java.io.Serializable;

public abstract class Device implements Serializable {
    private String deviceId;
    private String address;
    private DeviceStatus status = DeviceStatus.UNKNOWN;

    protected Device() {}
    protected Device(String deviceId, String address) {
        this.deviceId = deviceId; this.address = address;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }

    // ==== MANTENIMIENTO (VERSIÓN CON SOBRECARGA) ====

    // --- 1. MÉTODOS SIMPLES (Para el Simulador) ---
    // (Solo cambian el estado interno)

    /** Pone el dispositivo en estado FAILURE. */
    public void fail() {
        setStatus(DeviceStatus.FAILURE);
    }

    /** Pone el dispositivo en estado NORMAL. */
    public void repair() {
        setStatus(DeviceStatus.NORMAL);
    }

    /** Pone el dispositivo en estado INTERMITTENT. */
    public void intermittent() {
        setStatus(DeviceStatus.INTERMITTENT);
    }


    // --- 2. MÉTODOS CON CONTEXTO (Para la UI / Botones) ---
    // (Llaman al método simple, y las subclases pueden sobreescribirlos
    // para añadir lógica extra, como pausar el ciclo)

    /** Pone el dispositivo en estado FAILURE (versión con contexto). */
    public void fail(IMaintenanceContext context) {
        this.fail(); // Llama al método simple
    }

    /** Pone el dispositivo en estado NORMAL (versión con contexto). */
    public void repair(IMaintenanceContext context) {
        this.repair(); // Llama al método simple
    }

    /** Pone el dispositivo en estado INTERMITTENT (versión con contexto). */
    public void intermittent(IMaintenanceContext context) {
        this.intermittent(); // Llama al método simple
    }
}
