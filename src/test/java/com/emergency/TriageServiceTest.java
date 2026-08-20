package com.emergency;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardIncident;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireIncident;
import com.emergency.model.MedicalIncident;
import com.emergency.model.PoliceIncident;
import com.emergency.model.UnknownIncident;
import com.emergency.service.TriageService;

public class TriageServiceTest {
    private static TriageService triage;

    @BeforeAll
    public static void setTriage() {
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

    @Test
    public void shouldThrowExceptionForWhitespaceInput() {
        assertThrows(EmptyAlertException.class, () -> triage.parse("  "));
    }

    @Test
    public void shouldCreateFireIncident() {
        EmergencyIncident[] incidents = triage.parse("fire");
        assertInstanceOf(FireIncident.class, incidents[0]);
    }

    @Test
    public void shouldCreateMedicalIncident() {
        EmergencyIncident[] incidents = triage.parse("bleed");
        assertInstanceOf(MedicalIncident.class, incidents[0]);
    }

    @Test
    public void shouldCreatePoliceIncident() {
        EmergencyIncident[] incidents = triage.parse("crime");
        assertInstanceOf(PoliceIncident.class, incidents[0]);
    }

    @Test
    public void shouldCreateCoastGuardIncident() {
        EmergencyIncident[] incidents = triage.parse("drowning");
        assertInstanceOf(CoastGuardIncident.class, incidents[0]);
    }

    @Test
    public void shouldCreateUnknownIncident() {
        EmergencyIncident[] incidents = triage.parse("hello");
        assertInstanceOf(UnknownIncident.class, incidents[0]);
    }

    @Test
    public void shouldCreateMultipleIncidents() {
        EmergencyIncident[] incidents = triage.parse("fire crime bleed theft");
        assertInstanceOf(FireIncident.class, incidents[0]);
        assertInstanceOf(MedicalIncident.class, incidents[1]);
        assertInstanceOf(PoliceIncident.class, incidents[2]);
    }

    @Test
    public void shouldCreateAllIncidents() {
        EmergencyIncident[] incidents = triage.parse("fire ambulance doctor crime theft drowning bleed");
        assertInstanceOf(FireIncident.class, incidents[0]);
        assertInstanceOf(MedicalIncident.class, incidents[1]);
        assertInstanceOf(PoliceIncident.class, incidents[2]);
        assertInstanceOf(CoastGuardIncident.class, incidents[3]);
    }

}
