package fines;

import cars.Car;
import java.time.Instant;

public class RedLightFine extends Fine {

    private static final double BASE_AMOUNT = 80000.0;
    private static final int    BASE_POINTS = 5;

    public RedLightFine(Instant when, String deviceId, String photoUrl, Car car) {
        super(0L, when, FineType.RED_LIGHT, 0.0, 0, deviceId, photoUrl, car);
    }

    @Override
    public void compute() {
        setAmount(BASE_AMOUNT);
        setScoringPoints(BASE_POINTS);
    }
}
