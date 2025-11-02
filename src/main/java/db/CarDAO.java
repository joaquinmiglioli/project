package db;

import cars.Car;
import cars.CarBrand;
import cars.CarModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDAO {

    private final CarBrandDAO brandDAO = new CarBrandDAO();
    private final CarModelDAO modelDAO = new CarModelDAO();

    public Optional<Car> findById(long carid) throws SQLException {
        String sql = """
            SELECT carid, carbrand, carmodel, "licensePlate", owner, address, colour
            FROM cars
            WHERE carid = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, carid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapCar(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Car> findByPlate(String plate) throws SQLException {
        String sql = """
            SELECT carid, carbrand, carmodel, "licensePlate", owner, address, colour
            FROM cars
            WHERE UPPER("licensePlate") = UPPER(?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapCar(rs));
            }
        }
        return Optional.empty();
    }

    /** Auto al azar (PG): ORDER BY random(). */
    public Optional<Car> findRandom() throws SQLException {
        String sql = """
            SELECT carid, carbrand, carmodel, "licensePlate", owner, address, colour
            FROM cars
            ORDER BY random()
            LIMIT 1
            """;
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return Optional.of(mapCar(rs));
        }
        return Optional.empty();
    }

    public List<Car> findAll() throws SQLException {
        String sql = """
            SELECT carid, carbrand, carmodel, "licensePlate", owner, address, colour
            FROM cars
            ORDER BY carid
            """;
        List<Car> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(mapCar(rs));
        }
        return out;
    }

    // Inserts/updates si los querés (con BIGSERIAL, usamos RETURNING para recuperar el id):
    public long insert(long brandId, long modelId, String plate, String owner, String address, String colour) throws SQLException {
        String sql = """
            INSERT INTO cars(carbrand, carmodel, "licensePlate", owner, address, colour)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING carid
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, brandId);
            ps.setLong(2, modelId);
            ps.setString(3, plate);
            ps.setString(4, owner);
            ps.setString(5, address);
            ps.setString(6, colour);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public void update(long carid, long brandId, long modelId, String plate, String owner, String address, String colour) throws SQLException {
        String sql = """
            UPDATE cars
            SET carbrand = ?, carmodel = ?, "licensePlate" = ?, owner = ?, address = ?, colour = ?
            WHERE carid = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, brandId);
            ps.setLong(2, modelId);
            ps.setString(3, plate);
            ps.setString(4, owner);
            ps.setString(5, address);
            ps.setString(6, colour);
            ps.setLong(7, carid);
            ps.executeUpdate();
        }
    }

    public void delete(long carid) throws SQLException {
        String sql = "DELETE FROM cars WHERE carid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, carid);
            ps.executeUpdate();
        }
    }

    // --- mapeo helper ---
    private Car mapCar(ResultSet rs) throws SQLException {
        long brandId = rs.getLong("carbrand");
        long modelId = rs.getLong("carmodel");

        CarBrand brand = brandDAO.findById(brandId).orElse(null);
        CarModel model = modelDAO.findById(modelId).orElse(null);

        Car c = new Car();
        c.setCarid(rs.getLong("carid"));
        c.setPlate(rs.getString("licensePlate"));
        c.setOwner(rs.getString("owner"));
        c.setAddress(rs.getString("address"));
        c.setColour(rs.getString("colour"));
        c.setBrand(brand);
        c.setModel(model);
        return c;
    }
}
