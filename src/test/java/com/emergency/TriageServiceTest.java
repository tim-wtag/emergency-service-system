package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.FireEmergency;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.UnknownEmergency;
import com.emergency.service.TriageService;

class TriageServiceTest {
    
    private static TriageService triage;

    @BeforeAll
    static void setTriage() {
        triage = new TriageService();
    }

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(EmptyAlertException.class, () -> triage.parse(null));
    }

    @Test
    void shouldThrowExceptionWhenInputIsEmpty() {
        assertThrows(EmptyAlertException.class, () -> triage.parse(""));
    }

    @Test
    void shouldThrowExceptionForWhitespaceInput() {
        assertThrows(EmptyAlertException.class, () -> triage.parse("  "));
    }

    @Test
    void shouldCreateFireIncident() {
        List<EmergencyDispatch> incidents = triage.parse("fire");
        assertEquals(1, incidents.size());
        assertInstanceOf(FireEmergency.class, incidents.get(0));
    }

    @Test
    void shouldCreateMedicalIncident() {
        List<EmergencyDispatch> incidents = triage.parse("bleed");
        assertEquals(1, incidents.size());
        assertInstanceOf(MedicalEmergency.class, incidents.get(0));
    }

    @Test
    void shouldCreatePoliceIncident() {
        List<EmergencyDispatch> incidents = triage.parse("crime");
        assertEquals(1, incidents.size());
        assertInstanceOf(PoliceEmergency.class, incidents.get(0));
    }

    @Test
    void shouldCreateCoastGuardIncident() {
        List<EmergencyDispatch> incidents = triage.parse("drowning");
        assertEquals(1, incidents.size());
        assertInstanceOf(CoastGuardEmergency.class, incidents.get(0));
    }

    @Test
    void shouldCreateUnknownIncident() {
        List<EmergencyDispatch> incidents = triage.parse("hello");
        assertEquals(1, incidents.size());
        assertInstanceOf(UnknownEmergency.class, incidents.get(0));
    }

    @Test
    void shouldCreateMultipleIncidents() {
        List<EmergencyDispatch> incidents = triage.parse("fire crime bleed theft");
        
        assertEquals(3, incidents.size(), "Should not add duplicate incident types");
        
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));
        assertInstanceOf(PoliceEmergency.class, incidents.get(2));
    }

       @Test
    void shouldCreateMultipleIncidentsFireBlood() {
        List<EmergencyDispatch> incidents = triage.parse("il y a un fire chez moi et il y a du blood");
        
        assertEquals(2, incidents.size(), "Should not add duplicate incident types");
        
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));
    }

    @Test
    void shouldCreateAllIncidents() {
        List<EmergencyDispatch> incidents = triage.parse("fire ambulance doctor crime theft drowning bleed");
        
        assertEquals(4, incidents.size(), "Should consolidate all keywords into exactly 4 incidents");
        
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));
        assertInstanceOf(PoliceEmergency.class, incidents.get(2));
        assertInstanceOf(CoastGuardEmergency.class, incidents.get(3));
    }

    @Test
    void shouldNotAddDuplicateIncidentTypes() {
        List<EmergencyDispatch> incidents = triage.parse("fire fire fire");
        assertEquals(1, incidents.size(), "Should only create a single FireIncident even with multiple fire keywords");
        assertInstanceOf(FireEmergency.class, incidents.get(0));
    }
}