package com.emergency;

import com.emergency.config.DatabaseManager;
import com.emergency.controller.EmergencyCliController;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.tools.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        
        try {
            Console.main(args);
        } catch (SQLException e) {
            logger.error("Failed to start H2 console", e);
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isValid(5)) {
                logger.info("Successfully connected to the H2 database using DatabaseManager!");
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to the database.", e);
        }

        EmergencyCliController controller = new EmergencyCliController();
        controller.execution();
    }
}