package com.emergency.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.Status;

@Repository
public interface IncidentRepository extends JpaRepository<EmergencyIncident, UUID> {

    @Query("SELECT i FROM EmergencyIncident i WHERE i.status != 'RESOLVED'")
    List<EmergencyIncident> findAllActiveIncidents();

    List<EmergencyIncident> findByStatus(Status status);

    @Query("SELECT i.status, COUNT(i) FROM EmergencyIncident i GROUP BY i.status")
    List<Object[]> countIncidentsByStatus();

    @Query("SELECT i.status FROM EmergencyIncident i WHERE i.id =: id")
    Status findStatusById(@Param("incidentId") UUID incidentId);

}