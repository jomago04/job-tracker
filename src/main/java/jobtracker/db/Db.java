package jobtracker.db;

import java.sql.Connection;
import java.sql.DriverManager;

public final class Db {

    private static final String url = System.getenv("JOBTRACKER_DB_URL");
    private static final String user = System.getenv("JOBTRACKER_DB_USER");
    private static final String password = System.getenv("JOBTRACKER_DB_PASSWORD");

    static {
        if (url == null || user == null || password == null) {
            throw new RuntimeException(
                "Missing DB environment variables. " +
                "Expected JOBTRACKER_DB_URL, JOBTRACKER_DB_USER, JOBTRACKER_DB_PASSWORD"
            );
        }
    }

    private Db() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
}