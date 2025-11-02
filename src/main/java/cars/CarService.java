package cars;

import db.CarDAO;

import java.sql.SQLException;

public class CarService {

    private final CarDAO carDAO;

    public CarService(CarDAO carDAO) {
        this.carDAO = carDAO;
    }

    /** Directamente un auto random desde DB. */
    public Car randomCar() {
        try {
            return carDAO.findRandom()
                    .orElseThrow(() -> new IllegalStateException("No hay autos en la base de datos"));
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando autos en DB", e);
        }
    }
}