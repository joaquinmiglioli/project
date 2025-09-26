package Fines;

import java.util.Objects;

/** Tipo de infracción paramétrico (catálogo). */
public class FineType {
    /** Código corto para lógica ("SPEEDING", "ILLEGAL_PARKING", "RED_LIGHT"). */
    private String code;
    /** Descripción human-friendly. */
    private String description;
    private double amount;
    private int scoringPoints;

    public FineType() {}

    public FineType(String code, String description, double amount, int scoringPoints) {
        this.code = code;
        this.description = description;
        this.amount = amount;
        this.scoringPoints = scoringPoints;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    @Override public String toString() {
        return code + " - " + description;
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FineType)) return false;
        FineType fineType = (FineType) o;
        return Objects.equals(code, fineType.code);
        //hola
    }
    @Override public int hashCode() {
        return Objects.hash(code);
    }
}