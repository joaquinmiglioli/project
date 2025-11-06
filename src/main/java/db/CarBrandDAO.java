package db;

import cars.CarBrand;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Métodos para la tabla carbrands.

public class CarBrandDAO {

    public Optional<CarBrand> findById(long idbrand) throws SQLException {
        String sql = "SELECT idbrand, name FROM carbrands WHERE idbrand = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idbrand);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarBrand b = new CarBrand(rs.getLong("idbrand"), rs.getString("name"));
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<CarBrand> findByName(String name) throws SQLException {
        String sql = "SELECT idbrand, name FROM carbrands WHERE UPPER(name) = UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CarBrand b = new CarBrand(rs.getLong("idbrand"), rs.getString("name"));
                    return Optional.of(b);
                }
            }
        }
        return Optional.empty();
    }

    public List<CarBrand> findAll() throws SQLException {
        String sql = "SELECT idbrand, name FROM carbrands ORDER BY name";
        List<CarBrand> out = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new CarBrand(rs.getLong("idbrand"), rs.getString("name")));
            }
        }
        return out;
    }

    // CRUD opcional (si después querés insertar/actualizar marcas):
    public void insert(String name) throws SQLException {
        String sql = "INSERT INTO carbrands(idbrand, name) VALUES ((SELECT COALESCE(MAX(idbrand)+1,1) FROM carbrands), ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public void update(long idbrand, String newName) throws SQLException {
        String sql = "UPDATE carbrands SET name = ? WHERE idbrand = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setLong(2, idbrand);
            ps.executeUpdate();
        }
    }

    public void delete(long idbrand) throws SQLException {
        String sql = "DELETE FROM carbrands WHERE idbrand = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idbrand);
            ps.executeUpdate();
        }
    }
}
