
package devices;

import java.time.Instant;
import java.util.UUID;

public class SecurityWarning {

    private final String id;         // UUID del evento
    private String deviceId;         // id de la cámara
    private Instant timestamp;       // cuándo ocurrió
    private ServiceType serviceType; // qué servicio se notificó
    private String imagePath;        // imagen mostrada en el overlay (si aplica)
    private String note;             // nota opcional del operador

    public SecurityWarning() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public SecurityWarning(String deviceId, ServiceType serviceType,
                           String imagePath, String note) {
        this();
        this.deviceId = deviceId;
        this.serviceType = serviceType;
        this.imagePath = imagePath;
        this.note = note;
    }

    // Getters/Setters
    public String getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}