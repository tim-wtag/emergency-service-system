package com.emergency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;
import com.emergency.service.TriageService;

public class TriageServiceTest {
    private static TriageService triage;

    @BeforeAll
    public static void setTriage(){
        triage = new TriageService();
    }

    @Test
    public void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(EmptyAlertException.class, () -> triage.parse(null));
    }

    @Test
    public void shouldThrowExceptionWhenInputIsEmpty() {
        assertThrows(EmptyAlertException.class, () -> triage.parse(""));
    }

    // @Test
    // public void shouldReturnFireIncidentAndHazmatValueFalse(){
    //     assertEquals(new FireIncident("fire", false), triage.parse("fire") );
    // }
}
