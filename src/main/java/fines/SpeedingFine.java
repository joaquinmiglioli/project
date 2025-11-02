package fines;

import cars.Car;
import java.time.Instant;

public class SpeedingFine extends Fine {

    private final int speed; // km/h medidos
    private final int limit; // km/h permitidos

    // Ajustá valores base a tu enunciado
    private static final double BASE_AMOUNT  = 50000.0;
    private static final int    BASE_POINTS  = 3;
    private static final double STEP_FACTOR  = 0.15;  // +15% por cada 10%
    private static final int    STEP_PERCENT = 10;    // escalón 10%

    public SpeedingFine(Instant when,  String deviceId, String photoUrl, Car car,
                        int speed, int limit) {
        super(0L, when, FineType.SPEEDING, 0.0, 0,  deviceId, photoUrl, car);
        this.speed = speed;
        this.limit = limit;
    }

    @Override
    public void compute() {
        double amount = BASE_AMOUNT;
        int points = BASE_POINTS;

        if (limit > 0 && speed > limit) {
            double excessPct = (speed - limit) * 100.0 / limit;       // ej 30%
            int steps = (int) Math.floor(excessPct / STEP_PERCENT);   // 0,1,2...
            for (int i = 0; i < steps; i++) {
                amount += amount * STEP_FACTOR; // recargo acumulativo
                points += 1;                    // +1 punto por cada 10%
            }
        }
        setAmount(round2(amount));
        setScoringPoints(points);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
