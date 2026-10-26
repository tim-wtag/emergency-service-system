package com.emergency.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.Status;
import com.emergency.repository.EmergencyRepository;

import jakarta.transaction.Transactional;

@Service
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;

    public EmergencyService(EmergencyRepository emergencyRepository) {
        this.emergencyRepository = emergencyRepository;
    }

    public Map<String, java.util.List<EmergencyDispatch>> getEmergenciesGroupedByDepartment() {
        List<EmergencyDispatch> allEmergencies = emergencyRepository.findAll();
        
        return allEmergencies.stream()
                .collect(java.util.stream.Collectors.groupingBy(emergency -> emergency.getType().name()));
    }

    public Map<String, Integer> getEmergencyStatusCounts() {
        List<Object[]> results = emergencyRepository.countEmergenciesByStatus();

        return results.stream().collect(java.util.stream.Collectors.toMap(
                row -> ((Status) row[0]).name().toLowerCase(),
                row -> ((Number) row[1]).intValue()));
    }

    @Transactional
    public void dispatchEmergency(UUID emergencyId) {
        int rowsUpdated = emergencyRepository.markEmergencyAsDispatched(emergencyId);

        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Cannot dispatch: Emergency ID not found.");
        }
    }

    @Transactional
    public UUID resolveEmergency(UUID emergencyId) {
        int rowsUpdated = emergencyRepository.markEmergencyAsResolved(emergencyId);
        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Cannot resolve: Emergency ID not found.");
        }

        EmergencyDispatch dispatch = emergencyRepository.findById(emergencyId)
                // Fix: Use IllegalStateException since it should theoretically exist if rowsUpdated > 0
                .orElseThrow(() -> new IllegalStateException("Emergency not found after update."));

        EmergencyIncident incident = dispatch.getParentIncident();

        if (incident != null) {
            boolean hasUnresolvedDispatches = emergencyRepository
                    .existsByParentIncidentIdAndStatusNot(incident.getId(), Status.RESOLVED);

            if (!hasUnresolvedDispatches) {
                incident.setStatus(Status.RESOLVED);
            }

            return incident.getId();
        }

        return null;
    }
}
