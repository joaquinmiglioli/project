package db;

import cars.CarBrand;
import cars.CarModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Métodos para la tabla carmodels de la base de  datos.

public class CarModelDAO {

    private final CarBrandDAO brandDAO = new CarBrandDAO();

    public Optional<CarModel> findById(long modelid) throws SQLException {
        String sql = "SELECT modelid, name, carbrand FROM carmodels WHERE modelid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, modelid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long brandId = rs.getLong("carbrand");
                    CarBrand brand = brandDAO.findById(brandId).orElse(null);
                    CarModel m = new CarModel(rs.getLong("modelid"), rs.getString("name"), brand);
                    return Optional.of(m);
                }
            }
        }
        return Optional.empty();
    }

    public List<CarModel> findByBrand(long carbrand) throws SQLException {
        String sql = "SELECT modelid, name, carbrand FROM carmodels WHERE carbrand = ? ORDER BY name";
        List<CarModel> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, carbrand);
            try (ResultSet rs = ps.executeQuery()) {
                CarBrand brand = brandDAO.findById(carbrand).orElse(null);
                while (rs.next()) {
                    out.add(new CarModel(rs.getLong("modelid"), rs.getString("name"), brand));
                }
            }
        }
        return out;
    }

    public Optional<CarModel> findByBrandAndName(long brandId, String name) throws SQLException {
        String sql = "SELECT modelid, name, carbrand FROM carmodels WHERE carbrand = ? AND UPPER(name) = UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, brandId);
            ps.setString(2, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarBrand brand = brandDAO.findById(brandId).orElse(null);
                    CarModel m = new CarModel(rs.getLong("modelid"), rs.getString("name"), brand);
                    return Optional.of(m);
                }
            }
        }
        return Optional.empty();
    }

    // CRUD opcional
    public void insert(long brandId, String name) throws SQLException {
        String sql = """
            INSERT INTO carmodels(modelid, name, carbrand)
            VALUES ((SELECT COALESCE(MAX(modelid)+1,1) FROM carmodels), ?, ?)
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setLong(2, brandId);
            ps.executeUpdate();
        }
    }

    public void update(long modelid, String newName, long newBrandId) throws SQLException {
        String sql = "UPDATE carmodels SET name = ?, carbrand = ? WHERE modelid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setLong(2, newBrandId);
            ps.setLong(3, modelid);
            ps.executeUpdate();
        }
    }

    public void delete(long modelid) throws SQLException {
        String sql = "DELETE FROM carmodels WHERE modelid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, modelid);
            ps.executeUpdate();
        }
    }
}
