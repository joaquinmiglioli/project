package devices;

import java.io.Serializable;

public class Device implements Serializable {
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

    //Manteniemiento (version con sobrecarga)

    /*1) metodos simples (para el simulador)
    (solo cambian el estado interno)*/

    //Pone el dispositivo en estado FAILURE
    public void fail() {
        setStatus(DeviceStatus.FAILURE);
    }

    //Pone el dispositivo en estado NORMAL
    public void repair() {
        setStatus(DeviceStatus.NORMAL);
    }

    //Pone el dispositivo en estado INTERMITTENT
    public void intermittent() {
        setStatus(DeviceStatus.INTERMITTENT);
    }


    /*2)Metodos con contexto (para la UI/botones)
       (llaman al metodo simple y las subclases pueden sobreescribirlos para aniadir logica extra, como pausar el ciclo*/

    //Pone el dispositivo en estado FAILURE (versión con contexto)
    public void fail(IMaintenanceContext context) {
        this.fail();  //Llama al metodo simple
    }

    //Pone el dispositivo en estado NORMAL (versión con contexto)
    public void repair(IMaintenanceContext context) {
        this.repair(); //Llama al metodo simple
    }

    //Pone el dispositivo en estado INTERMITTENT (versión con contexto)
    public void intermittent(IMaintenanceContext context) {
        this.intermittent(); //Llama al metodo simple
    }
}
