package com.emergency.config;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:h2:~/emergency_service;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private DatabaseManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initDatabase() {
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream("schema.sql")) {
            
            if (is == null) {
                logger.error("Error: schema.sql file not found in classpath!");
                return;
            }

            String sqlScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            
            executeScript(sqlScript);

        } catch (IOException e) {
            logger.error("Failed to read schema.sql: {}", e.getMessage());
        }
    }

    private static void executeScript(String sqlScript) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlScript);
            logger.info("Database initialized successfully from schema.sql.");
            
        } catch (SQLException e) {
            logger.error("Failed to execute database script: {}", e.getMessage());
        }
    }
}