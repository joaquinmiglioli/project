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

    // ==== mantenimiento genérico (podés overridear en subclases si querés efectos extra) ====
    public void fail()        { setStatus(DeviceStatus.FAILURE); }
    public void repair()      { setStatus(DeviceStatus.NORMAL); }
    public void intermittent(){ setStatus(DeviceStatus.INTERMITTENT); }
}
