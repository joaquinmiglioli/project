package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*Clase utilitaria que provee la conexión a la base de datos PostgreSQL con los datos de URL, usuario y contraseña*/

public class DBConnection {

    private static final String JDBC_URL  = "jdbc:postgresql://localhost/monitoringcenter";
    private static final String USERNAME  = "postgres";
    private static final String PASSWORD  = "mati123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
