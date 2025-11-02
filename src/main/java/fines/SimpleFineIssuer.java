package fines;

import cars.Car;
import cars.CarService;
import db.FineDAO;

import java.time.Instant;
import java.util.Map;

public class SimpleFineIssuer implements FineIssuer {

    private final CarService carService;
    private final FineDAO fineDao;

    // Inyectá ambos por constructor (desde AppContext / donde armes los servicios)
    public SimpleFineIssuer(CarService carService, FineDAO fineDao) {
        this.carService = carService;
        this.fineDao = fineDao;
    }

    @Override
    public Fine issue(FineType type, String deviceId, String photoUrl, Map<String, Object> meta) {
        Car car = carService.randomCar(); // auto random desde DB

        // construir subclase
        Fine fine;
        Instant now = Instant.now();
        switch (type) {
            case SPEEDING -> {
                int speed = asInt(meta.get("speed"), 0);
                int limit = asInt(meta.get("limit"), 0);
                fine = new SpeedingFine(now, deviceId, photoUrl, car, speed, limit);
            }
            case PARKING -> {
                int toleranceSec = asInt(meta.get("toleranceSec"), 0);
                int parkedSec    = asInt(meta.get("parkedSec"), 0);
                fine = new ParkingFine(now, deviceId, photoUrl, car, toleranceSec, parkedSec);
            }
            default -> {
                fine = new RedLightFine(now, deviceId, photoUrl, car);
            }
        }

        // cálculo polimórfico
        fine.compute();

        // Persistencia: el DAO hace INSERT, obtiene fineid y actualiza barcode
        try {
            fineDao.insert(fine);           // <- te setea fineId y barcode en el mismo objeto
        } catch (Exception e) {
            // convertimos a unchecked para no contaminar la firma
            throw new RuntimeException("No se pudo guardar la multa en DB", e);
        }

        // NO generes id/barcode acá; ya lo dejó listo el DAO
        return fine;
    }

    private static int asInt(Object o, int def) {
        if (o == null) return def;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return def; }
    }
}