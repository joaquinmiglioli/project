package fines;


import cars.Car;
import cars.CarService;
import db.FineDAO;


import java.sql.SQLException;
import com.example.demo.exceptions.DatabaseOperationException;


import java.time.Instant;
import java.util.Map;


public class SimpleFineIssuer implements FineIssuer {


    private final CarService carService;
    private final FineDAO fineDao;


    public SimpleFineIssuer(CarService carService, FineDAO fineDao) {
        this.carService = carService;
        this.fineDao = fineDao;
    }


    @Override
    public Fine issue(FineType type, String deviceId, String photoUrl, Map<String, Object> meta) { //recibe un FineType y segun esto, utiliza
        Car car = carService.randomCar();                                                          // polimorficamente el metodo compute()
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


        fine.compute();


        try {
            fineDao.insert(fine);
        } catch (SQLException e) {
            throw new DatabaseOperationException("No se pudo guardar la multa en DB", e);
        }
        return fine;
    }


    private static int asInt(Object o, int def) {
        if (o == null) return def;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception e) { return def; }
    }
}
