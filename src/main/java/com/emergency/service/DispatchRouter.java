package com.emergency.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.model.EmergencyIncident;
import com.emergency.model.IncidentStatus;

public class DispatchRouter {
    private static final Logger logger = LoggerFactory.getLogger(DispatchRouter.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void route(EmergencyIncident incident) {
        switch (incident.getType()) {
            case FIRE -> new FireStation().dispatch(incident);
            case MEDICAL -> new AmbulanceSquad().dispatch(incident);
            case POLICE -> new PoliceStation().dispatch(incident);
            case COASTAL -> new CoastGuard().dispatch(incident);
            case UNKNOWN -> new HumanOperatorStation().dispatch(incident);
            default -> throw new IllegalStateException("Unexpected type: " + incident.getType());
        }
    }

    public void routeAsync(EmergencyIncident incident){
        if(incident == null){
            return;
        }

        executor.submit(() -> {
            try{
                Thread.sleep(3000);

                route(incident);

                incident.setStatus(IncidentStatus.DISPATCHED);

                logger.info("Incident successfully routed and dispatched: ", incident.getId(), incident.getType());
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        });
    }

    public void shutdown(){
        executor.shutdown();
        try{
            if(!executor.awaitTermination(3, TimeUnit.SECONDS)){
                executor.shutdownNow();
            }
        }
        catch(InterruptedException e){
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
