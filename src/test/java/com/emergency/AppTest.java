package com.emergency;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.emergency.controller.EmergencyCliController;


public class AppTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    public void setUp() {
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
    }

    @Test
    public void shouldProcessMedicalEmergencyAndExit() {

        String simulatedUserInput = "bleed\n2\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        assertDoesNotThrow(() -> App.main(new String[]{}));

        String capturedOutput = outputStreamCaptor.toString();

        assertTrue(capturedOutput.contains("[DISPATCH - MEDICAL] Paramedics deployed. Patients: 2"),
                "The output did not contain the expected medical dispatch message.");
    }

    @Test
    public void shouldProcessUnknownEmergencyAndDispatchOperator() {
        String simulatedUserInput = "hello\nyes\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        assertDoesNotThrow(() -> App.main(new String[]{}));

        String capturedOutput = outputStreamCaptor.toString();

        assertTrue(capturedOutput.contains("Is this a prank call?"),
            "Application did not ask the prank call question.");
        assertTrue(capturedOutput.contains("[DISPATCH - OPERATOR] Alert unclear. Forwarding raw description to human operator: true"),
            "The output did not contain the expected Operator dispatch message.");
    }

}
