package com.emergency.repository;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyType;
import com.emergency.model.Status;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyRepository extends JpaRepository<EmergencyDispatch, UUID> {

    List<EmergencyDispatch> findByStatus(Status status);

    List<EmergencyDispatch> findByType(EmergencyType type);

    @Query("SELECT e.status, COUNT(e) FROM EmergencyDispatch e GROUP BY e.status")
    List<Object[]> countEmergenciesByStatus();

    @Modifying
    @Query("UPDATE EmergencyDispatch e SET e.status = 'DISPATCHED', e.dispatchedTime = CURRENT_TIME WHERE e.id = :emergencyId")
    int markEmergencyAsDispatched(@Param("emergency_id") UUID emergencyId);

    @Modifying
    @Query("UPDATE EmergencyDispatch e SET e.status = 'RESOLVED', e.resolvedTime = CURRENT_TIME WHERE e.id = :emergencyId")
    int markEmergencyAsResolved(@Param("emergencyId") UUID emergencyId);

    boolean existsByParentIncidentIdAndStatusNot(UUID incidentId, Status status);

    int countByParentIncidentId(UUID incidentId);

    int countByParentIncidentIdAndStatus(UUID incidentId, Status status);
}