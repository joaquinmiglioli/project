package cars;

import db.CarDAO;

import java.sql.SQLException;

/*Un servicio simple que provee lógica de negocio relacionada  con autos. Su único metodo, randomCar(), pide un auto al azar al CarDAO.
 */

public class CarService {

    private final CarDAO carDAO;

    public CarService(CarDAO carDAO) {
        this.carDAO = carDAO;
    }

    // Directamente un auto random desde DB
    public Car randomCar() {
        try {
            return carDAO.findRandom()
                    .orElseThrow(() -> new IllegalStateException("No cars found in DB"));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching random car from DB", e);
        }
    }
}