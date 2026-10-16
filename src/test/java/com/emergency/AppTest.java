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

public class AppTest {

    private final InputStream originalSystemIn = System.in;
    private final PrintStream originalSystemOut = System.out;
    private final PrintStream originalSystemErr = System.err;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        outputStreamCaptor = new ByteArrayOutputStream();
        PrintStream captureStream = new PrintStream(outputStreamCaptor);
        
        // Capture both out and err since SLF4J usually writes to System.err
        System.setOut(captureStream);
        System.setErr(captureStream);
    }

    @AfterEach
    void tearDown() {
        // Restore all original streams to avoid affecting other test classes
        System.setIn(originalSystemIn);
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);
    }

    @Test
    void shouldProcessMedicalEmergencyAndExit() {
        String simulatedUserInput = "bleed\n2\nexit\n";
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        assertDoesNotThrow(() -> App.main(new String[]{}));

        String capturedOutput = outputStreamCaptor.toString();

        assertTrue(capturedOutput.contains("[DISPATCH - MEDICAL] Paramedics deployed. Patients: 2"),
                "The output did not contain the expected medical dispatch message.");
    }

    @Test
    void shouldProcessMultipleEmergencyAndExit() {
        String simulatedUserInput = "fire and bleed\nfalse\n2\nexit\n";
        
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        assertDoesNotThrow(() -> App.main(new String[]{}));

        String capturedOutput = outputStreamCaptor.toString();

        assertTrue(capturedOutput.contains("[DISPATCH - FIRE] Routing engines. Hazmat: false"),
                "The output did not contain the expected fire dispatch message.");

        assertTrue(capturedOutput.contains("[DISPATCH - MEDICAL] Paramedics deployed. Patients: 2"),
                "The output did not contain the expected medical dispatch message.");
    }
    
    @Test
    void shouldProcessUnknownEmergencyAndDispatchOperator() {
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