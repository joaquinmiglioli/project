package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

   //Ajusten la contraseña a su propia base de datos
    private static final String JDBC_URL  = "jdbc:postgresql://localhost:5432/monitoringcenter";
    private static final String USERNAME  = "postgres";
    private static final String PASSWORD  = "mati123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
