package com.emergency;

import com.emergency.controller.EmergencyCliController;

public class App {
    public static void main(String[] args) {
        EmergencyCliController controller = new EmergencyCliController();
        controller.execution();
    }
}
