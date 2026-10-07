package com.emergency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.emergency.exception.EmptyAlertException;
import com.emergency.model.CoastGuardEmergency;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.FireEmergency;
import com.emergency.model.MedicalEmergency;
import com.emergency.model.PoliceEmergency;
import com.emergency.model.UnknownEmergency;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;
import com.emergency.service.TriageService;

class TriageServiceTest {

    private TriageService triage;
    private int incidentSaveCount;
    private int emergencySaveCount;

    @BeforeEach
    void setUp() {
        incidentSaveCount = 0;
        emergencySaveCount = 0;

        IncidentRepository stubIncidentRepo = new IncidentRepository() {
            @Override
            public EmergencyIncident save(EmergencyIncident incident) {
                incidentSaveCount++;
                return incident;
            }
        };

        EmergencyRepository stubEmergencyRepo = new EmergencyRepository() {
            @Override
            public EmergencyDispatch save(EmergencyDispatch emergency) {
                emergencySaveCount++;
                return emergency;
            }
        };
        triage = new TriageService(stubIncidentRepo, stubEmergencyRepo);
    }

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(EmptyAlertException.class, () -> triage.parseAndSave(null));
        assertEquals(0, incidentSaveCount, "Should not attempt to save on null input");
    }

    @Test
    void shouldThrowExceptionWhenInputIsEmpty() {
        assertThrows(EmptyAlertException.class, () -> triage.parseAndSave(""));
        assertEquals(0, incidentSaveCount, "Should not attempt to save on empty input");
    }

    @Test
    void shouldThrowExceptionForWhitespaceInput() {
        assertThrows(EmptyAlertException.class, () -> triage.parseAndSave("  "));
        assertEquals(0, incidentSaveCount, "Should not attempt to save on whitespace input");
    }

    @Test
    void shouldCreateFireIncidentAndSave() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("fire");

        assertEquals(1, incidents.size());
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertEquals(1, incidentSaveCount, "Parent incident should be saved exactly once");
        assertEquals(1, emergencySaveCount, "Child emergency should be saved exactly once");
    }

    @Test
    void shouldCreateMedicalIncidentAndSave() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("bleed");

        assertEquals(1, incidents.size());
        assertInstanceOf(MedicalEmergency.class, incidents.get(0));
        assertEquals(1, incidentSaveCount);
        assertEquals(1, emergencySaveCount);
    }

    @Test
    void shouldCreatePoliceIncidentAndSave() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("crime");

        assertEquals(1, incidents.size());
        assertInstanceOf(PoliceEmergency.class, incidents.get(0));
        assertEquals(1, incidentSaveCount);
        assertEquals(1, emergencySaveCount);
    }

    @Test
    void shouldCreateCoastGuardIncidentAndSave() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("drowning");

        assertEquals(1, incidents.size());
        assertInstanceOf(CoastGuardEmergency.class, incidents.get(0));
        assertEquals(1, incidentSaveCount);
        assertEquals(1, emergencySaveCount);
    }

    @Test
    void shouldCreateUnknownIncidentAndSave() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("random unknown event");

        assertEquals(1, incidents.size(), "Should fall back to unknown if no keywords match");
        assertInstanceOf(UnknownEmergency.class, incidents.get(0));
        assertEquals(1, incidentSaveCount);
        assertEquals(1, emergencySaveCount);
    }

    @Test
    void shouldCreateMultipleIncidentsAndSaveThemAll() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("fire crime bleed theft");

        assertEquals(3, incidents.size(), "Should identify 3 distinct incidents");
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));
        assertInstanceOf(PoliceEmergency.class, incidents.get(2));

        assertEquals(1, incidentSaveCount, "Parent incident should be saved exactly once");
        assertEquals(3, emergencySaveCount, "Each of the 3 child emergencies should be saved");
    }

    @Test
    void shouldHandleMixedCaseKeywords() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("fIrE and bLeEd");

        assertEquals(2, incidents.size(), "Case should be ignored during keyword matching");
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));

        assertEquals(1, incidentSaveCount);
        assertEquals(2, emergencySaveCount);
    }

    @Test
    void shouldCreateAllIncidents() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("fire ambulance doctor crime theft drowning bleed");

        assertEquals(4, incidents.size(), "Should consolidate all keywords into exactly 4 incidents");
        assertInstanceOf(FireEmergency.class, incidents.get(0));
        assertInstanceOf(MedicalEmergency.class, incidents.get(1));
        assertInstanceOf(PoliceEmergency.class, incidents.get(2));
        assertInstanceOf(CoastGuardEmergency.class, incidents.get(3));

        assertEquals(1, incidentSaveCount);
        assertEquals(4, emergencySaveCount);
    }

    @Test
    void shouldNotAddDuplicateIncidentTypes() {
        List<EmergencyDispatch> incidents = triage.parseAndSave("fire fire fire");

        assertEquals(1, incidents.size(), "Should only create a single FireIncident even with multiple fire keywords");
        assertInstanceOf(FireEmergency.class, incidents.get(0));

        assertEquals(1, incidentSaveCount);
        assertEquals(1, emergencySaveCount, "Should only trigger save for the single distinct incident");
    }
}