// src/main/java/com/talentstream/controller/ApplicantController.java
package com.talentstream.controller;

import com.talentstream.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/applicant")
@CrossOrigin // adjust CORS config as per your security setup
public class ApplicantController {

    private final ApplicantService applicantService;

    @Autowired
    public ApplicantController(ApplicantService applicantService) {
        this.applicantService = applicantService;
    }

    @GetMapping("/{id}/tour-seen")
    public ResponseEntity<Map<String, Boolean>> getTourSeen(@PathVariable("id") Long id) {
        boolean seen = applicantService.getTourSeen(id);
        return ResponseEntity.ok(Collections.singletonMap("seen", seen));
    }

    @PostMapping("/{id}/tour-seen")
    public ResponseEntity<Void> markTourSeen(@PathVariable("id") Long id) {
        applicantService.markTourSeen(id);
        return ResponseEntity.ok().build();
    }
}
