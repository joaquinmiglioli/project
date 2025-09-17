package Fines;

/** Tipo particular de infracción por velocidad (si querés sobrecargar montos). */
public class Speeding extends FineType {
    private double tenPercentExcessSurcharge;

    public Speeding() {}

    public Speeding(String code, String description, double amount, int scoringPoints,
                    double tenPercentExcessSurcharge) {
        super(code, description, amount, scoringPoints);
        this.tenPercentExcessSurcharge = tenPercentExcessSurcharge;
    }

    public double getTenPercentExcessSurcharge() {
        return tenPercentExcessSurcharge; }
    public void setTenPercentExcessSurcharge(double v) {
        this.tenPercentExcessSurcharge = v;
    }
}