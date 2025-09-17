package Fines;

/** Multa emitida (instancia concreta de un tipo). */
public class Fine {
    private FineType type;
    private double amount;      // puede venir de type.getAmount() + ajustes
    private int scoringPoints;  // idem
    private String licensePlate;
    private String deviceId;    // quién la originó (RAD-1, PK-3, INT-5, etc.)
    private String dateTime;    // string simple por ahora

    public Fine() {}

    public Fine(FineType type, double amount, int scoringPoints,
                String licensePlate, String deviceId, String dateTime) {
        this.type = type;
        this.amount = amount;
        this.scoringPoints = scoringPoints;
        this.licensePlate = licensePlate;
        this.deviceId = deviceId;
        this.dateTime = dateTime;
    }

    public FineType getType() {
        return type;
    }
    public void setType(FineType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getScoringPoints() {
        return scoringPoints;
    }
    public void setScoringPoints(int scoringPoints) {
        this.scoringPoints = scoringPoints;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}