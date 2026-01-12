package com.talentstream.controller;


import com.talentstream.dto.AnalyticsEventRequest;
import com.talentstream.service.FeatureUsageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class FeatureAnalyticsController {

    private final FeatureUsageService featureUsageService;

    public FeatureAnalyticsController(FeatureUsageService featureUsageService) {
        this.featureUsageService = featureUsageService;
    }

    @PostMapping("/event")
    public ResponseEntity<String> recordFeatureEvent(
            @RequestBody AnalyticsEventRequest request) {

    	featureUsageService.recordFeatureEvent(request);

        return ResponseEntity.ok("Event recorded successfully");
    }
}