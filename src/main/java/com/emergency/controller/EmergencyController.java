package com.emergency.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emergency.model.EmergencyDispatch;
import com.emergency.service.EmergencyService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/emergencies")
public class EmergencyController {
    private final EmergencyService emergencyService;

    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getEmergencyStatus() {
        return ResponseEntity.ok(emergencyService.getEmergencyStatusCounts());
    }

    @GetMapping("/departments")
    public ResponseEntity<Map<String, List<EmergencyDispatch>>> getEmergenciesByDepartment() {
        return ResponseEntity.ok(emergencyService.getEmergenciesGroupedByDepartment());
    }

    @PutMapping("/{id}/dispatch")
    public ResponseEntity<String> dispatchEmergency(@PathVariable("id") UUID emergencyId) {
        try {
            emergencyService.dispatchEmergency(emergencyId);
            return ResponseEntity.ok("Success: Emergency unit" + emergencyId + "has been marked as DISPATCHED.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<String> resolveEmergency(@PathVariable("id") UUID emergencyId) {
        try {
            UUID parentIncidentId = emergencyService.resolveEmergency(emergencyId);
            String message = "Success: Emergency unit " + emergencyId + " marked as RESOLVED.";

            if (parentIncidentId != null) {
                message += " Parent Incident updated: " + parentIncidentId;
            }
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
