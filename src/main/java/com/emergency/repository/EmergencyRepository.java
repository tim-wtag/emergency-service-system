package com.emergency.repository;

import com.emergency.config.DatabaseManager;
import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.EmergencyType;
import com.emergency.model.FireEmergency;
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
    EmergencyDispatch emergencyDispatch;

    public EmergencyDispatch save(EmergencyDispatch emergency) {
        String sql = "INSERT INTO t_emergency_dispatches (emergency_id, incident_id, emergency_name, status, dispatched_timestamp) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, emergency.getId());

            if (emergency.getParentIncident() != null) {
                pstmt.setObject(2, emergency.getParentIncident().getId());
            } else {
                pstmt.setObject(2, null);
            }

            pstmt.setString(3, emergency.getComment());
            pstmt.setString(4, emergency.getStatus().name().toLowerCase());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("Saving emergency dispatch failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error saving dispatch to the database:", e);
        }

        return emergency;
    }

   public List<EmergencyDispatch> findAll() {
    List<EmergencyDispatch> emergencies = new ArrayList<>();
    String sql = "SELECT emergency_id, incident_id, emergency_name, status FROM t_emergency_dispatches";

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            // 1. Extract raw data from the ResultSet
            UUID emergencyId = rs.getObject("emergency_id", UUID.class);
            UUID incidentId = rs.getObject("incident_id", UUID.class);
            String emergencyName = rs.getString("emergency_name");
            String statusString = rs.getString("status");

            // 2. Instantiate the dispatch object (Note: see 'abstract' warning above)
            // Assuming you remove 'abstract' from the class, or use a concrete subclass here.
            EmergencyDispatch dispatch = new EmergencyDispatch(emergencyName, null); 
            
            // 3. Set the fields
            dispatch.setId(emergencyId);
            dispatch.setStatus(Status.valueOf(statusString.toUpperCase()));
            
            // 4. Handle the foreign key relationship
            if (incidentId != null) {
                // Option A: Just set the ID to avoid querying the database again
                EmergencyIncident incident = new EmergencyIncident("Loaded from dispatch");
                incident.setId(incidentId);
                dispatch.setParentIncident(incident);
                
                // Option B: If you need the full incident details, you would call 
                // incidentRepository.findById(incidentId) here instead.
            }

            // 5. Add to the list
            emergencies.add(dispatch);
        }

    } catch (SQLException e) {
        logger.error("Error retrieving emergency dispatches from the database:", e);
    }

    return emergencies;
}

    public boolean markEmergencyAsDispatched(UUID emergencyId) {
        String sql = "UPDATE t_emergency_dispatches SET emergency_status = 'dispatched', dispatched_timestamp = CURRENT_TIMESTAMP WHERE emergency_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, emergencyId);
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

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
        // Querying the dispatches table instead of the incident table
        String sql = "SELECT status, COUNT(*) as total FROM t_emergency_dispatches GROUP BY status";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("total");

                if (status != null) {
                    statusCounts.put(status.toLowerCase(), count);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving dispatch status counts from the database:", e);
        }

        return statusCounts;
    }

   public List<EmergencyDispatch> getPendingEmergencies() {
        List<EmergencyDispatch> emergencies = new ArrayList<>();
        String sql = "SELECT emergency_id, emergency_name FROM t_emergency_dispatches WHERE status = 'pending'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                emergencyDispatch = new EmergencyDispatch(null, null);
                
                // Use the setter to apply the name/description
                emergencyDispatch.setComment(rs.getString("emergency_name"));
                
                emergencyDispatch.setId(rs.getObject("emergency_id", java.util.UUID.class));
                emergencyDispatch.setStatus(Status.PENDING); 

                emergencies.add(emergencyDispatch);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving pending emergencies:", e);
        }

        return emergencies;
    }

    public List<EmergencyDispatch> getDispatchedEmergencies() {
        List<EmergencyDispatch> emergencies = new ArrayList<>();
        
        String sql = "SELECT emergency_id, emergency_name FROM t_emergency_dispatches WHERE status = 'dispatched'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
        
                EmergencyDispatch dispatch = new EmergencyDispatch(null, null);
                
                dispatch.setComment(rs.getString("emergency_name"));
                dispatch.setId(rs.getObject("emergency_id", java.util.UUID.class));
                
                dispatch.setStatus(Status.DISPATCHED); 

                emergencies.add(dispatch);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving dispatched emergencies:", e);
        }

        return emergencies;
    }

    public List<EmergencyDispatch> getResolvedEmergencies() {
        List<EmergencyDispatch> emergencies = new ArrayList<>();
        
        String sql = "SELECT emergency_id, emergency_name FROM t_emergency_dispatches WHERE status = 'resolved'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                EmergencyDispatch dispatch = new EmergencyDispatch(null, null);
                
                dispatch.setComment(rs.getString("emergency_name"));
                dispatch.setId(rs.getObject("emergency_id", java.util.UUID.class));
                
                dispatch.setStatus(Status.RESOLVED); 

                emergencies.add(dispatch);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving dispatched emergencies:", e);
        }

        return emergencies;
    }
}