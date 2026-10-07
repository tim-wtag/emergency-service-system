package com.emergency.repository;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyType;
import com.emergency.model.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmergencyRepository extends AbstractRepository<EmergencyDispatch> {

    @Override
    protected String getTableName() {
        return "t_emergency_dispatches";
    }

    @Override
    public EmergencyDispatch save(EmergencyDispatch emergency) {
        String sql = "INSERT INTO t_emergency_dispatches (emergency_id, comment, emergency_type, emergency_status, incident_id) VALUES (?, ?, ?, ?, ?)";
        this.executeInsertSQL(sql,
                emergency.getId(), emergency.getComment(), emergency.getType().name().toLowerCase(),
                emergency.getStatus().name().toLowerCase(), emergency.getParentIncident().getId());

        return emergency;
    }

    public boolean markEmergencyAsDispatched(UUID emergencyId) {
        String sql = "UPDATE t_emergency_dispatches SET emergency_status = 'dispatched', dispatched_time = CURRENT_TIMESTAMP WHERE emergency_id = ?";
        
        int rowsUpdated = executeInsertSQL(sql, emergencyId);
        return rowsUpdated > 0;
    }

    public UUID markEmergencyAsResolved(UUID emergencyId) {
        String resolveDispatchSql = "UPDATE t_emergency_dispatches SET emergency_status = 'resolved' WHERE emergency_id = ?";
        String getIncidentIdSql = "SELECT incident_id FROM t_emergency_dispatches WHERE emergency_id = ?";

        int rowsUpdated = executeInsertSQL(resolveDispatchSql, emergencyId);
        if (rowsUpdated == 0) {
            return null;
        }

        return executeQuery(getIncidentIdSql, rs -> {
            if (rs.next()) {
                return rs.getObject("incident_id", UUID.class);
            }
            return null;
        }, emergencyId);
    }

    public Map<String, Integer> getEmergencyStatusCounts() {
        return getStatusCounts("emergency_status");
    }

   public List<EmergencyDispatch> getEmergenciesByStatus(Status status) {
        return fetchEmergenciesByStatus(status.name().toLowerCase(), status);
    }

    public List<EmergencyDispatch> getEmergenciesByType(EmergencyType type) {
        return fetchEmergenciesByType(type.name().toLowerCase(), type);
    }

    private List<EmergencyDispatch> fetchEmergenciesByStatus(String dbStatus, Status forcedStatus) {
        String sql = "SELECT emergency_id, comment FROM t_emergency_dispatches WHERE emergency_status = ?";

        List<EmergencyDispatch> result = executeQuery(sql, rs -> {
            List<EmergencyDispatch> emergencies = new ArrayList<>();
            while (rs.next()) {
                EmergencyDispatch dispatch = new EmergencyDispatch(null, null);
                dispatch.setComment(rs.getString("comment"));
                dispatch.setId(rs.getObject("emergency_id", UUID.class));
                dispatch.setStatus(forcedStatus);
                emergencies.add(dispatch);
            }
            return emergencies;
        }, dbStatus);

        return result != null ? result : new ArrayList<>();
    }

    private List<EmergencyDispatch> fetchEmergenciesByType(String dbType, EmergencyType forcedType) {
        String sql = "SELECT emergency_id, comment, emergency_status FROM t_emergency_dispatches WHERE emergency_type = ?";

        List<EmergencyDispatch> result = executeQuery(sql, rs -> {
            List<EmergencyDispatch> emergencies = new ArrayList<>();
            while (rs.next()) {
                EmergencyDispatch dispatch = new EmergencyDispatch(forcedType, null);
                dispatch.setComment(rs.getString("comment"));
                dispatch.setId(rs.getObject("emergency_id", UUID.class));

                String statusValue = rs.getString("emergency_status");
                if (statusValue != null) {
                    dispatch.setStatus(Status.valueOf(statusValue.toUpperCase()));
                }

                emergencies.add(dispatch);
            }
            return emergencies;
        }, dbType);

        return result != null ? result : new ArrayList<>();
    }
}