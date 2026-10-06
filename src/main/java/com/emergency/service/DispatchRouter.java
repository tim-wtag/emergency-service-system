package com.emergency.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.model.EmergencyDispatch;

public class DispatchRouter {
    private static final Logger logger = LoggerFactory.getLogger(DispatchRouter.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    public void route(EmergencyDispatch emergency) {
        switch (emergency.getType()) {
            case FIRE -> new FireStation().dispatch(emergency);
            case MEDICAL -> new AmbulanceSquad().dispatch(emergency);
            case POLICE -> new PoliceStation().dispatch(emergency);
            case COASTAL -> new CoastGuard().dispatch(emergency);
            case UNKNOWN -> new HumanOperatorStation().dispatch(emergency);
            default -> throw new IllegalStateException("Unexpected type: " + emergency.getType());
        }
    }

    public void routeAsync(EmergencyDispatch incident) {
        if (incident == null) {
            return;
        }

        executor.submit(() -> {
            try {
                Thread.sleep(3000);

                route(incident);

                logger.info("Incident successfully routed and dispatched: {} {}", incident.getId(), incident.getType());
            } catch (InterruptedException e) {
                logger.info("Routing interrupted for incident {} during system shutdown.", incident.getId(), e);
                Thread.currentThread().interrupt();
            }
        });
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown(); 
            try {
                if (!executor.awaitTermination(4, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); 
                }
            } catch (InterruptedException e) {
                logger.error("testing", e);
                Thread.currentThread().interrupt();
            } 
            finally{
                executor.shutdownNow();
            }
        }
    }

}
