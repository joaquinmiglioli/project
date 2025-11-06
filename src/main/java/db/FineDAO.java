// db/FineDAO.java
package db;

import cars.Car;
import cars.CarBrand;
import cars.CarModel;
import fines.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*Métodos para la tabla fines.
*/

public class FineDAO {

    // SQL base para seleccionar multas con toda la info del auto.
    private static final String SELECT_FINE_WITH_CAR_SQL = """
        SELECT f.fineid, f.finedate, f.type, f.amount, f.scoringpoints, f.deviceid, f.photourl, f.barcode,
               c.carid, c."licensePlate", c.owner, c.address, c.colour,
               b.idbrand, b.name AS brandname,
               m.modelid, m.name AS modelname
        FROM fines f
        JOIN cars c ON c.carid = f.carid
        JOIN carmodels m ON m.modelid = c.carmodel
        JOIN carbrands b ON b.idbrand = c.carbrand
        """;


    // Inserta la multa, setea el fineId y  actualiza el barcode con el id ya generado.
    public void insert(Fine fine) throws SQLException {
        String insertSql = """
            INSERT INTO fines(finedate, type, amount, scoringpoints, deviceid, photourl, carid)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING fineid
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setTimestamp(1, Timestamp.from(fine.getFineDate()));
            ps.setString(2, fine.getType().name());
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(fine.getAmount()));
            ps.setInt(4, fine.getScoringPoints());
            ps.setString(5, fine.getDeviceId());
            ps.setString(6, fine.getPhotoUrl());
            ps.setLong(7, fine.getCar().getCarid());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                long id = rs.getLong(1);
                fine.setFineId(id);
            }
        }

        // calcular barcode 6+12 y actualizar
        String barcode = makeBarcode(fine.getFineId(), fine.getAmount());
        fine.setBarcode(barcode);

        String updSql = "UPDATE fines SET barcode = ? WHERE fineid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updSql)) {
            ps.setString(1, barcode);
            ps.setLong(2, fine.getFineId());
            ps.executeUpdate();
        }
    }

    //Lista todas (con JOINs para reconstruir el Car completo).
    public List<Fine> findAll(int limit) throws SQLException {
        String sql = SELECT_FINE_WITH_CAR_SQL + """
            ORDER BY f.finedate DESC
            LIMIT ?
            """;
        List<Fine> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 1000);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapFineWithCar(rs));
                }
            }
        }
        return out;
    }

    // Busca multas por patente(Reporte 3)
    public List<Fine> findByPlate(String plate) throws SQLException {
        String sql = SELECT_FINE_WITH_CAR_SQL + """
            WHERE c."licensePlate" = ?
            ORDER BY f.finedate DESC
            """;
        List<Fine> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plate.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapFineWithCar(rs));
                }
            }
        }
        return out;
    }


    //Borra ttodo y reinicia el autonumerico a 1
    public void deleteAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement()) {

            conn.setAutoCommit(false);

            st.executeUpdate("DELETE FROM fines");

            // 1) Intento para columnas SERIAL
            try {
                st.execute("SELECT setval(pg_get_serial_sequence('fines','fineid'), 1, false)");
            } catch (SQLException e) {
                // 2) Si no hay secuencia (columna es IDENTITY), reinicio el contador IDENTITY
                st.execute("ALTER TABLE fines ALTER COLUMN fineid RESTART WITH 1");
            }

            conn.commit();
        }
    }

    // helpers

    private Fine mapFineWithCar(ResultSet rs) throws SQLException {
        // Car
        CarBrand brand = new CarBrand(rs.getLong("idbrand"), rs.getString("brandname"));
        CarModel model = new CarModel(rs.getLong("modelid"), rs.getString("modelname"), brand);

        Car car = new Car();
        car.setCarid(rs.getLong("carid"));
        car.setPlate(rs.getString("licensePlate"));
        car.setOwner(rs.getString("owner"));
        car.setAddress(rs.getString("address"));
        car.setColour(rs.getString("colour"));
        car.setBrand(brand);
        car.setModel(model);

        // Fine (instanciamos subclase según type, pero como ya está calculada en DB
        // solo seteamos monto/puntos directamente y NO llamamos compute()).
        String type = rs.getString("type");
        Fine f;
        Instant fd = rs.getTimestamp("finedate").toInstant();
        String deviceId = rs.getString("deviceid");
        String photoUrl = rs.getString("photourl");

        switch (FineType.valueOf(type)) {
            case SPEEDING -> f = new SpeedingFine(fd, deviceId, photoUrl, car, 0, 0);
            case PARKING  -> f = new ParkingFine(fd, deviceId, photoUrl, car, 0, 0);
            default       -> f = new RedLightFine(fd, deviceId, photoUrl, car);
        }
        f.setFineId(rs.getLong("fineid"));
        f.setType(FineType.valueOf(type));
        f.setAmount(rs.getBigDecimal("amount").doubleValue());
        f.setScoringPoints(rs.getInt("scoringpoints"));
        f.setBarcode(rs.getString("barcode"));

        return f;
    }

    private static String makeBarcode(long id, double amount) {
        String id6 = String.format("%06d", id);
        long cents = Math.max(0, Math.round(amount * 100.0));
        String amt12 = String.format("%012d", cents);
        return id6 + amt12;
    }
}