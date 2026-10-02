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
        String sql = "INSERT INTO t_incident (incident_id, description, reported_timestamp, status) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, incident.getId());
            pstmt.setString(2, incident.getDescription());
            pstmt.setString(3, incident.getStatus().name().toLowerCase());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
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
                int count = rs.getInt("total");

                if (status != null) {
                    statusCounts.put(status.toLowerCase(), count);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving status counts from the database:", e);
        }

        return statusCounts;
    }

    public List<String> getActiveIncidentsFormatted() {
        List<String> activeIncidents = new ArrayList<>();
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status != 'resolved'";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.util.UUID id = rs.getObject(COL_INCIDENT_ID, java.util.UUID.class);
                String description = rs.getString(COL_DESCRIPTION);
                String status = rs.getString(COL_STATUS);

                activeIncidents.add(String.format("Active Incident:[ID: %s], %s, Status: %s",
                        id.toString(), description, status.toUpperCase()));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving active incidents from the database:", e);
        }

        return activeIncidents;
    }

    public List<String> getResolvedIncidentsFormatted() {
        List<String> resolvedIncidents = new ArrayList<>();
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status = 'resolved'";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                java.util.UUID id = rs.getObject(COL_INCIDENT_ID, java.util.UUID.class);
                String description = rs.getString(COL_DESCRIPTION);
                String status = rs.getString(COL_STATUS);

                resolvedIncidents.add(String.format("Resolved Incident:[ID: %s], %s, Status: %s",
                        id.toString(), description, status.toUpperCase()));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving resolved incidents from the database", e);
        }

        return resolvedIncidents;
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

    public boolean resolveIfAllEmergenciesResolved(java.util.UUID incidentId) {
        String checkRemainingSql = "SELECT COUNT(*) FROM t_emergency_dispatches WHERE incident_id = ? AND emergency_status != 'resolved'";
        String resolveIncidentSql = "UPDATE t_incident SET status = 'resolved', resolved_timestamp = CURRENT_TIMESTAMP WHERE incident_id = ?";

        try (Connection conn = DatabaseManager.getConnection()) {

            boolean allResolved = false;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkRemainingSql)) {
                checkStmt.setObject(1, incidentId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        allResolved = true;
                    }
                }
            }

            if (allResolved) {
                try (PreparedStatement incidentStmt = conn.prepareStatement(resolveIncidentSql)) {
                    incidentStmt.setObject(1, incidentId);
                    int affectedRows = incidentStmt.executeUpdate();
                    return affectedRows > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error checking and resolving incident ID: {}", incidentId, e);
        }
        return false;
    }
}
