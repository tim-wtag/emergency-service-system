package com.emergency.repository;

import com.emergency.config.DatabaseManager;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyType;
import com.emergency.model.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmergencyRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyRepository.class);

    public EmergencyDispatch save(EmergencyDispatch emergency) {
        String sql = "INSERT INTO t_emergency_dispatches (emergency_id, emergency_name, incident_type, emergency_status, dispatched_timestamp, incident_id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, emergency.getId());
            pstmt.setString(2, emergency.getComment());
            
            pstmt.setString(3, emergency.getType().name().toLowerCase()); 
            
            pstmt.setString(4, emergency.getStatus().name().toLowerCase());

            if (emergency.getParentIncident() != null) {
                pstmt.setObject(5, emergency.getParentIncident().getId());
            } else {
                pstmt.setObject(5, null);
            }

            if (pstmt.executeUpdate() == 0) {
                logger.warn("Saving emergency dispatch failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error saving dispatch to the database:", e);
        }

        return emergency;
    }

    public boolean markEmergencyAsDispatched(UUID emergencyId) {
        String sql = "UPDATE t_emergency_dispatches SET emergency_status = 'dispatched', dispatched_timestamp = CURRENT_TIMESTAMP WHERE emergency_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, emergencyId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating emergency status to dispatched for ID: {}", emergencyId, e);
            return false;
        }
    }

    public java.util.UUID markEmergencyAsResolved(java.util.UUID emergencyId) {
        String resolveDispatchSql = "UPDATE t_emergency_dispatches SET emergency_status = 'resolved' WHERE emergency_id = ?";
        String getIncidentIdSql = "SELECT incident_id FROM t_emergency_dispatches WHERE emergency_id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {

            try (PreparedStatement dispatchStmt = conn.prepareStatement(resolveDispatchSql)) {
                dispatchStmt.setObject(1, emergencyId);
                if (dispatchStmt.executeUpdate() == 0) {
                    return null;
                }
            }

            try (PreparedStatement getParentStmt = conn.prepareStatement(getIncidentIdSql)) {
                getParentStmt.setObject(1, emergencyId);
                try (ResultSet rs = getParentStmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getObject("incident_id", java.util.UUID.class);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Database error during emergency resolution for ID: {}", emergencyId, e);
        }
        return null;
    }

    public Map<String, Integer> getStatusCounts() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as total FROM t_emergency_dispatches GROUP BY status";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                if (status != null) {
                    statusCounts.put(status.toLowerCase(), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving dispatch status counts from the database:", e);
        }

        return statusCounts;
    }

    public List<EmergencyDispatch> getPendingEmergencies() {
        return fetchEmergenciesByStatus("pending", Status.PENDING);
    }

    public List<EmergencyDispatch> getDispatchedEmergencies() {
        return fetchEmergenciesByStatus("dispatched", Status.DISPATCHED);
    }

    public List<EmergencyDispatch> getResolvedEmergencies() {
        return fetchEmergenciesByStatus("resolved", Status.RESOLVED);
    }

    public List<EmergencyDispatch> getFireEmergencies() {
        return fetchEmergenciesByType("fire", EmergencyType.FIRE);
    }

    public List<EmergencyDispatch> getMedicalEmergencies() {
        return fetchEmergenciesByType("medical", EmergencyType.MEDICAL);
    }

    public List<EmergencyDispatch> getPoliceEmergencies() {
        return fetchEmergenciesByType("police", EmergencyType.POLICE);
    }

    public List<EmergencyDispatch> getCoastGuardEmergencies() {
        return fetchEmergenciesByType("coastal", EmergencyType.COASTAL);
    }

    public List<EmergencyDispatch> getUnknownEmergencies() {
        return fetchEmergenciesByType("unknown", EmergencyType.UNKNOWN);
    }

    private List<EmergencyDispatch> fetchEmergenciesByStatus(String dbStatus, Status forcedStatus) {
        List<EmergencyDispatch> emergencies = new ArrayList<>();
        String sql = "SELECT emergency_id, emergency_name FROM t_emergency_dispatches WHERE status = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dbStatus);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmergencyDispatch dispatch = new EmergencyDispatch(null, null);
                    dispatch.setComment(rs.getString("emergency_name"));
                    dispatch.setId(rs.getObject("emergency_id", java.util.UUID.class));
                    dispatch.setStatus(forcedStatus);
                    emergencies.add(dispatch);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving {} emergencies:", dbStatus, e);
        }

        return emergencies;
    }

    private List<EmergencyDispatch> fetchEmergenciesByType(String dbType, EmergencyType forcedType) {
        List<EmergencyDispatch> emergencies = new ArrayList<>();
        String sql = "SELECT emergency_id, emergency_name, status FROM t_emergency_dispatches WHERE type = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dbType);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmergencyDispatch dispatch = new EmergencyDispatch(forcedType, null);
                    dispatch.setComment(rs.getString("emergency_name"));
                    dispatch.setId(rs.getObject("emergency_id", java.util.UUID.class));

                    String dbStatus = rs.getString("status");
                    if (dbStatus != null) {
                        dispatch.setStatus(Status.valueOf(dbStatus.toUpperCase()));
                    }

                    emergencies.add(dispatch);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving {} emergencies:", dbType, e);
        }

        return emergencies;
    }
}