// devices/Device.java
package devices;

import java.io.Serializable;

public abstract class Device implements Serializable {
    private String deviceId;            // "RAD-1", "PK-3", "INT-5", "CAM-2"
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

    // ==== MANTENIMIENTO (AHORA CON CONTEXTO) ====
    // El 'context' es (usualmente) null o ignorado por dispositivos simples.
    // Las subclases (TrafficLightController) sobreescribirán esto.

    /** Pone el dispositivo en estado FAILURE. */
    public void fail(IMaintenanceContext context) {
        setStatus(DeviceStatus.FAILURE);
    }

    /** Pone el dispositivo en estado NORMAL. */
    public void repair(IMaintenanceContext context) {
        setStatus(DeviceStatus.NORMAL);
    }

    /** Pone el dispositivo en estado INTERMITTENT. */
    public void intermittent(IMaintenanceContext context) {
        setStatus(DeviceStatus.INTERMITTENT);
    }
}
