package com.emergency.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.Status;
import com.emergency.repository.EmergencyRepository;
import com.emergency.repository.IncidentRepository;

import jakarta.transaction.Transactional;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EmergencyRepository emergencyRepository;

    public IncidentService(IncidentRepository incidentRepository, EmergencyRepository emergencyRepository) {
        this.incidentRepository = incidentRepository;
        this.emergencyRepository = emergencyRepository;
    }

    private String formatIncident(String label, EmergencyIncident incident) {
        return String.format("%s Incident:[ID: %s], %s, Status: %s",
                label,
                incident.getId().toString(),
                incident.getDescription(),
                incident.getStatus().name());
    }

    public Map<String, Integer> getIncidentStatus() {
        List<Object[]> results = incidentRepository.countIncidentsByStatus();
        return results.stream().collect(
                Collectors.toMap(row -> ((Status) row[0]).name().toLowerCase(), row -> ((Number) row[1]).intValue()));
    }

   public List<String> getActiveIncidents() {
        return incidentRepository.findAllActiveIncidents().stream()
                .map(incident -> formatIncident("Active", incident))
                .toList(); 
    }

    public List<String> getResolvedIncident() {
        return incidentRepository.findByStatus(Status.RESOLVED).stream()
                .map(incident -> formatIncident("RESOLVED", incident))
                .toList(); 
    }

    public String getIncidentStatus(UUID incidentId){
        Status status = incidentRepository.findStatusById(incidentId);
        return status != null ? status.name().toLowerCase() : null;
    }

    @Transactional
    public boolean syncIncidentStatus(UUID incidentId){
        EmergencyIncident incident = incidentRepository.findById(incidentId).orElse(null);
        if(incident == null){
            return false;
        }

        int total = emergencyRepository.countByParentIncidentId(incidentId);
        if(total == 0){
            return false;
        }

        int resolvedCount = emergencyRepository.countByParentIncidentIdAndStatus(incidentId, Status.RESOLVED);
        int dispatchedCount = emergencyRepository.countByParentIncidentIdAndStatus(incidentId, Status.DISPATCHED);


        if (resolvedCount == total) {
            incident.setStatus(Status.RESOLVED);
            incident.setResolvedTime(LocalTime.now(ZoneId.systemDefault())); 
        } else if (dispatchedCount > 0 || resolvedCount > 0) {
            incident.setStatus(Status.DISPATCHED);
        } else {
            incident.setStatus(Status.PENDING);
        }

        incidentRepository.save(incident);
        return true;
    }
}
