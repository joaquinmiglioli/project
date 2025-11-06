package fines;


import cars.Car;
import java.time.Instant;


//clase tipo Fine para multas de estacionamiento


public class ParkingFine extends Fine {


    private final int toleranceSec;
    private final int parkedSec;


    private static final double BASE_AMOUNT = 30000.0;
    private static final int    BASE_POINTS = 2;


    public ParkingFine(Instant when, String deviceId, String photoUrl, Car car,
                       int toleranceSec, int parkedSec) {
        super(0L, when, FineType.PARKING, 0.0, 0, deviceId, photoUrl, car);
        this.toleranceSec = toleranceSec;
        this.parkedSec = parkedSec;
    }


    @Override
    public void compute() {         //implementa compute() en base al tiempo de estacionamiento excedido
        double amount = BASE_AMOUNT;
        int points = BASE_POINTS;


        if (parkedSec > toleranceSec && toleranceSec > 0) {
            int extra = parkedSec - toleranceSec;
            int blocks5min = extra / 300;       // cada 5 min
            amount += 2000.0 * blocks5min;      // $2000 por bloque
        }
        setAmount(round2(amount));
        setScoringPoints(points);
    }


    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
