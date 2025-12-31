package projet_bibliotheque.src.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bibliotheque_bd";
    private static final String USERNAME = "Guela Signey Lionel";
    private static final String PASSWORD = "s2403a2402";
    private static final String DRIVER = "org.postgresql.Driver";

    private static Connection connection;
    private static Properties properties;

    static {
        properties = new Properties();
        properties.setProperty("user", USERNAME);
        properties.setProperty("password", PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Connecting to the database...");
            connection = DriverManager.getConnection(URL, properties);
        }
        return connection;
    }
}