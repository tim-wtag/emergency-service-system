package com.emergency.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.config.DatabaseManager;
import com.emergency.model.EmergencyIncident;

public class IncidentRepository {
    private static final Logger logger = LoggerFactory.getLogger(IncidentRepository.class);

    private static final String COL_STATUS = "status";
    private static final String COL_INCIDENT_ID = "incident_id";
    private static final String COL_DESCRIPTION = "description";

    public EmergencyIncident save(EmergencyIncident incident) {
        String sql = "INSERT INTO t_incident (description, reported_timestamp, status) VALUES (?, CURRENT_TIMESTAMP, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { COL_INCIDENT_ID })) {

            pstmt.setString(1, incident.getDescription());
            pstmt.setString(2, incident.getStatus().name().toLowerCase());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        incident.setId(generatedKeys.getObject(1, java.util.UUID.class));
                    }
                }
            } else {
                logger.warn("Warning: Saving incident failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error saving parent incident to the database:", e);
        }

        return incident;
    }

    public Map<String, Integer> getStatusCounts() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as total FROM t_incident GROUP BY status";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString(COL_STATUS);
                if (status != null) {
                    statusCounts.put(status.toLowerCase(), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving status counts from the database:", e);
        }

        return statusCounts;
    }

    public List<String> getActiveIncidentsFormatted() {
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status != 'resolved'";
        return fetchAndFormatIncidents(sql, "Active");
    }

    public List<String> getResolvedIncidentsFormatted() {
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status = 'resolved'";
        return fetchAndFormatIncidents(sql, "Resolved");
    }

    /**
     * Helper method: Executes the provided SQL and maps the result set into a formatted string.
     * Prevents code duplication between active and resolved list generation.
     */
    private List<String> fetchAndFormatIncidents(String sql, String label) {
        List<String> formattedIncidents = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.util.UUID id = rs.getObject(COL_INCIDENT_ID, java.util.UUID.class);
                String description = rs.getString(COL_DESCRIPTION);
                String status = rs.getString(COL_STATUS);

                formattedIncidents.add(String.format("%s Incident:[ID: %s], %s, Status: %s",
                        label, id.toString(), description, status.toUpperCase()));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving {} incidents from the database:", label, e);
        }

        return formattedIncidents;
    }

    public String getIncidentStatus(UUID id) {
        String sql = "SELECT status FROM t_incident WHERE incident_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(COL_STATUS).toLowerCase();
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving incident status for ID: {}", id, e);
        }
        return null;
    }

    public boolean syncIncidentStatus(java.util.UUID incidentId) {
        String countSql = "SELECT emergency_status, COUNT(*) as cnt FROM t_emergency_dispatches WHERE incident_id = ? GROUP BY emergency_status";
        String updateSql = "UPDATE t_incident SET status = ?, resolved_timestamp = ? WHERE incident_id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            int total = 0;
            int resolvedCount = 0;
            int dispatchedCount = 0;

            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setObject(1, incidentId);
                try (ResultSet rs = countStmt.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("emergency_status");
                        int count = rs.getInt("cnt");
                        total += count;
                        
                        if ("resolved".equalsIgnoreCase(status)) {
                            resolvedCount += count;
                        } else if ("dispatched".equalsIgnoreCase(status)) {
                            dispatchedCount += count;
                        }
                    }
                }
            }

            if (total == 0) return false; 

            String newStatus;
            java.sql.Timestamp resolvedTimestamp = null;

            if (resolvedCount == total) {
                newStatus = "resolved";
                resolvedTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            } else if (dispatchedCount > 0 || resolvedCount > 0) {
                newStatus = "dispatched";
            } else {
                newStatus = "pending";
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newStatus);
                updateStmt.setTimestamp(2, resolvedTimestamp); 
                updateStmt.setObject(3, incidentId);
                return updateStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            logger.error("Error syncing incident status for ID: {}", incidentId, e);
        }
        return false;
    }
}