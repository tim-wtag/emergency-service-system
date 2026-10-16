package com.emergency.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import com.emergency.model.EmergencyIncident;

public class IncidentRepository extends AbstractRepository<EmergencyIncident> {

    private static final String COL_STATUS = "status";
    private static final String COL_INCIDENT_ID = "incident_id";
    private static final String COL_DESCRIPTION = "description";

    @Override
    public EmergencyIncident save(EmergencyIncident incident) {
        String sql = "INSERT INTO t_incident (incident_id, description, reported_time, incident_status) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
        this.executeInsertSQL(sql, incident.getId(), incident.getDescription(), incident.getStatus().name().toLowerCase());

        return incident;
    }

    @Override
    protected String getTableName() {
        return "t_incident";
    }

    public Map<String, Integer> getIncidentStatusCounts() {
        return getStatusCounts("incident_status"); 
    }

    public List<String> getActiveIncidentsFormatted() {
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status != 'resolved'";
        return fetchAndFormatIncidents(sql, "Active");
    }

    public List<String> getResolvedIncidentsFormatted() {
        String sql = "SELECT incident_id, description, status FROM t_incident WHERE status = 'resolved'";
        return fetchAndFormatIncidents(sql, "Resolved");
    }

private List<String> fetchAndFormatIncidents(String sql, String label) {
    List<String> result = executeQuery(sql, rs -> {
        List<String> formattedIncidents = new ArrayList<>();
        while (rs.next()) {
            UUID id = rs.getObject(COL_INCIDENT_ID, UUID.class);
            String description = rs.getString(COL_DESCRIPTION);
            String status = rs.getString(COL_STATUS);

            formattedIncidents.add(String.format("%s Incident:[ID: %s], %s, Status: %s",
                    label, id.toString(), description, status.toUpperCase()));
        }
        return formattedIncidents;
    });

    return result != null ? result : new ArrayList<>();
}

public String getIncidentStatus(UUID id) {
    String sql = "SELECT status FROM t_incident WHERE incident_id = ?";

    return executeQuery(sql, rs -> {
        if (rs.next()) {
            return rs.getString(COL_STATUS).toLowerCase();
        }
        return null;
    }, id);
}

public boolean syncIncidentStatus(UUID incidentId) {
    String countSql = "SELECT emergency_status, COUNT(*) as cnt FROM t_emergency_dispatches WHERE incident_id = ? GROUP BY emergency_status";
    String updateSql = "UPDATE t_incident SET status = ?, resolved_timestamp = ? WHERE incident_id = ?";

    int[] counts = executeQuery(countSql, rs -> {
        int total = 0;
        int  resolved = 0;
        int  dispatched = 0;
        while (rs.next()) {
            String status = rs.getString("emergency_status");
            int count = rs.getInt("cnt");
            total += count;
            
            if ("resolved".equalsIgnoreCase(status)) {
                resolved += count;
            } else if ("dispatched".equalsIgnoreCase(status)) {
                dispatched += count;
            }
        }
        return new int[]{total, resolved, dispatched};
    }, incidentId);

    if (counts == null || counts[0] == 0) {
        return false;
    }

    int total = counts[0];
    int resolvedCount = counts[1];
    int dispatchedCount = counts[2];

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

    int rowsUpdated = executeInsertSQL(updateSql, newStatus, resolvedTimestamp, incidentId);
    return rowsUpdated > 0;
}
}