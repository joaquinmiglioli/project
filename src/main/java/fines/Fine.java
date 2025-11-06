package fines;


import cars.Car;


import java.time.Instant;


//define las propiedades comunes de todas las multas


public abstract class Fine {


    private long fineId;
    private Instant fineDate;
    private FineType type;
    private double amount;
    private int scoringPoints;
    private String deviceId;
    private String photoUrl;
    private String barcode;
    private Car car;


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


    public abstract void compute();  //fuerza a todas las clases hijas a implementarlo para tener su propio calculo de multa


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
