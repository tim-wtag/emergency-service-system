package com.emergency;

import com.emergency.config.DatabaseManager;
import com.emergency.controller.EmergencyCliController;

public class App {

    public static void main(String[] args) {
        
        DatabaseManager.initDatabase();

        EmergencyCliController controller = new EmergencyCliController();
        controller.execution();
    }
}