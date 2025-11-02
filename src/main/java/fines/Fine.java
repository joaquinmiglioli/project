package fines;

import cars.Car;

import java.time.Instant;

public abstract class Fine {

    private long fineId;              // correlativo (lo asignás al emitir/guardar)
    private Instant fineDate;         // fecha/hora de emisión
    private FineType type;            // SPEEDING / PARKING / RED_LIGHT
    private double amount;            // $ final
    private int scoringPoints;        // puntos a descontar
    private String deviceId;          // id del dispositivo que originó la infracción
    private String photoUrl;          // evidencia (si aplica)
    private String barcode;           // 6 dígitos id + 12 de importe en centavos (opcional)

    private Car car;                  // vehículo asociado

    public Fine() { }

    public Fine(long fineId,
                Instant fineDate,
                FineType type,
                double amount,
                int scoringPoints,
                String deviceId,
                String photoUrl,
                Car car) {
        this.fineId = fineId;
        this.fineDate = fineDate;
        this.type = type;
        this.amount = amount;
        this.scoringPoints = scoringPoints;
        this.deviceId = deviceId;
        this.photoUrl = photoUrl;
        this.car = car;
    }

    /** Cada subclase define su cálculo (setea amount y scoringPoints). */
    public abstract void compute();

    // --- Getters/Setters ---
    public long getFineId() { return fineId; }
    public void setFineId(long fineId) { this.fineId = fineId; }

    public Instant getFineDate() { return fineDate; }
    public void setFineDate(Instant fineDate) { this.fineDate = fineDate; }

    public FineType getType() { return type; }
    public void setType(FineType type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getScoringPoints() { return scoringPoints; }
    public void setScoringPoints(int scoringPoints) { this.scoringPoints = scoringPoints; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
}