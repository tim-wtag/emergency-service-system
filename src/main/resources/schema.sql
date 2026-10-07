CREATE TABLE IF NOT EXISTS t_incident (
    incident_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    description VARCHAR(500),
    reported_time TIMESTAMP,
    resolved_time TIMESTAMP,
    incident_status VARCHAR(20) NOT NULL CHECK (incident_status IN ('pending', 'dispatched', 'resolved'))
);


CREATE TABLE IF NOT EXISTS t_emergency_dispatches (
    emergency_id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    comment VARCHAR(500),
    emergency_type VARCHAR(20) NOT NULL CHECK (emergency_type IN ('fire', 'medical', 'police', 'coastal', 'unknown')),
    emergency_status VARCHAR(20) NOT NULL CHECK (emergency_status IN ('pending', 'dispatched', 'resolved')),
    dispatched_time TIMESTAMP,
    incident_id UUID,
    CONSTRAINT fk_incident 
        FOREIGN KEY (incident_id) 
        REFERENCES t_incident (incident_id) 
        ON DELETE CASCADE
);
