package com.talentstream.controller;


import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.service.ApplicantSummaryService;

@CrossOrigin("*")
@RestController
@RequestMapping("/applicant-summary")
public class ApplicantSummaryController {

    private final ApplicantSummaryService summaryService;

    public ApplicantSummaryController(ApplicantSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping("/{applicantId}/getApplicantSummary")
    public ResponseEntity<String> getApplicantSummary(@PathVariable long applicantId) {
        return ResponseEntity.ok(summaryService.getApplicantSummary(applicantId));
    }

    @PutMapping("/{applicantId}/updateApplicantSummary")
    public ResponseEntity<?> updateApplicantSummary(@PathVariable long applicantId, @RequestBody Map<String, String> requestBody) {
        String req = requestBody.get("summary");
        summaryService.updateApplicantSummary(applicantId, req);
        return ResponseEntity.ok().body("Summary updated successfully");
    }
}
