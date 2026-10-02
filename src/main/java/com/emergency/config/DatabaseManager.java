package com.emergency.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    
   // The base URL from your screenshot, plus the schema and auto-server flag
    private static final String DB_URL = "jdbc:h2:~/emergency_service;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    
    // Fix 1: Dynamically read the password from environment variables. 
    // It defaults to an empty string to keep your local H2 working.
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    // Fix 2: Add a private constructor to hide the implicit public one.
    private DatabaseManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}