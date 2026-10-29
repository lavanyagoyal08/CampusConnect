package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection — Thread-safe Singleton JDBC connection manager.
 * Reads db.properties from the classpath or project root, with sensible fallbacks.
 */
public class DBConnection {

    private static Connection connection;
    private static Properties dbProps;

    private DBConnection() {}

    private static synchronized void loadProperties() {
        if (dbProps != null) return;
        dbProps = new Properties();

        // 1. Try classpath resource
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                dbProps.load(is);
                return;
            }
        } catch (Exception ignored) {}

        // 2. Try file in working directory
        File localFile = new File("db.properties");
        if (localFile.exists()) {
            try (FileInputStream fis = new FileInputStream(localFile)) {
                dbProps.load(fis);
                return;
            } catch (Exception ignored) {}
        }

        // 3. Fallback defaults
        dbProps.setProperty("db.url", "jdbc:mysql://localhost:3306/campusconnect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        dbProps.setProperty("db.user", "root");
        dbProps.setProperty("db.password", "user@19");
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            loadProperties();
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                    dbProps.getProperty("db.url"),
                    dbProps.getProperty("db.user"),
                    dbProps.getProperty("db.password")
                );
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found in classpath: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new SQLException("Database connection error: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
