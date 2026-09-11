package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

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
        List<EmergencyIncident> incidents = triage.parse("fire");
        assertEquals(1, incidents.size());
        assertInstanceOf(FireIncident.class, incidents.get(0));
    }

    @Test
    public void shouldCreateMedicalIncident() {
        List<EmergencyIncident> incidents = triage.parse("bleed");
        assertEquals(1, incidents.size());
        assertInstanceOf(MedicalIncident.class, incidents.get(0));
    }

    @Test
    public void shouldCreatePoliceIncident() {
        List<EmergencyIncident> incidents = triage.parse("crime");
        assertEquals(1, incidents.size());
        assertInstanceOf(PoliceIncident.class, incidents.get(0));
    }

    @Test
    public void shouldCreateCoastGuardIncident() {
        List<EmergencyIncident> incidents = triage.parse("drowning");
        assertEquals(1, incidents.size());
        assertInstanceOf(CoastGuardIncident.class, incidents.get(0));
    }

    @Test
    public void shouldCreateUnknownIncident() {
        List<EmergencyIncident> incidents = triage.parse("hello");
        assertEquals(1, incidents.size());
        assertInstanceOf(UnknownIncident.class, incidents.get(0));
    }

    @Test
    public void shouldCreateMultipleIncidents() {
        List<EmergencyIncident> incidents = triage.parse("fire crime bleed theft");
        
        assertEquals(3, incidents.size(), "Should not add duplicate incident types");
        
        assertInstanceOf(FireIncident.class, incidents.get(0));
        assertInstanceOf(MedicalIncident.class, incidents.get(1));
        assertInstanceOf(PoliceIncident.class, incidents.get(2));
    }

    @Test
    public void shouldCreateAllIncidents() {
        List<EmergencyIncident> incidents = triage.parse("fire ambulance doctor crime theft drowning bleed");
        
        assertEquals(4, incidents.size(), "Should consolidate all keywords into exactly 4 incidents");
        
        assertInstanceOf(FireIncident.class, incidents.get(0));
        assertInstanceOf(MedicalIncident.class, incidents.get(1));
        assertInstanceOf(PoliceIncident.class, incidents.get(2));
        assertInstanceOf(CoastGuardIncident.class, incidents.get(3));
    }

    @Test
    public void shouldNotAddDuplicateIncidentTypes() {
        List<EmergencyIncident> incidents = triage.parse("fire fire fire");
        assertEquals(1, incidents.size(), "Should only create a single FireIncident even with multiple fire keywords");
        assertInstanceOf(FireIncident.class, incidents.get(0));
    }
}