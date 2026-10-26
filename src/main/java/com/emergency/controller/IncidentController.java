package com.emergency.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emergency.model.EmergencyDispatch;
import com.emergency.model.EmergencyIncident;
import com.emergency.model.Status;
import com.emergency.repository.EmergencyRepository;
import com.emergency.service.DispatchRouter;
import com.emergency.service.IncidentService;
import com.emergency.service.TranslationService;
import com.emergency.service.TriageService;

@RequestMapping
@RestController
public class IncidentController {

    private final IncidentService incidentService;
    private final EmergencyRepository emergencyRepository;
    private final TranslationService translationService;
    private final TriageService triageService;
    private final DispatchRouter dispatchRouter;

    public IncidentController(IncidentService incidentService, EmergencyRepository emergencyRepository, TranslationService translationService,
            TriageService triageService, DispatchRouter dispatchRouter) {
        this.incidentService = incidentService;
        this.emergencyRepository = emergencyRepository;
        this.translationService = translationService;
        this.triageService = triageService;
        this.dispatchRouter = dispatchRouter;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> findIncidentStatus() {
        return ResponseEntity.ok(incidentService.getIncidentStatus());
    }

    @PostMapping("/report")
    public ResponseEntity<String> reportIncident(@RequestBody EmergencyRequest request) {
        String translated = translationService.translateToEnglish(request.getDescription());
        
        List<EmergencyDispatch> dispatches = triageService.parse(translated);
        
        if (dispatches == null || dispatches.isEmpty()) {
            return ResponseEntity.badRequest().body("System prevented creation of an empty incident.");
        }

        for (EmergencyDispatch dispatch : dispatches) {
            triageService.applyDetails(dispatch, request); 
        }

        emergencyRepository.saveAll(dispatches);

        for (EmergencyDispatch dispatch : dispatches) {
            if (dispatch.getStatus() != Status.RESOLVED) { 
                dispatchRouter.routeAsync(dispatch);
            }
        }

        EmergencyIncident parentIncident = dispatches.get(0).getParentIncident();
        return ResponseEntity.ok(
            "Success: Parent Incident [ID: " + parentIncident.getId() + "] created with " + 
            dispatches.size() + " emergency unit(s) attached."
        );
    }
}
