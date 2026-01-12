package com.talentstream.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.EducationDetailsDTO;
import com.talentstream.service.ApplicantEducationService;

@CrossOrigin("*")
@RestController
@RequestMapping("/applicant-education")
public class ApplicantEducationController {

    private final ApplicantEducationService applicantEducationService;

    public ApplicantEducationController(ApplicantEducationService service) {
        this.applicantEducationService = service;
    }

    @GetMapping("/{applicantId}/getApplciantEducationDetails")
    public ResponseEntity<EducationDetailsDTO> get(@PathVariable Long applicantId) {
        return ResponseEntity.ok(applicantEducationService.getApplicantEducationDetails(applicantId));
    }

    @PutMapping("/{applicantId}/updateApplciantEducationDetails")
    public ResponseEntity<?> put(@PathVariable Long applicantId,
                                 @Valid @RequestBody EducationDetailsDTO body,
                                 BindingResult binding) {
        if (binding.hasErrors()) {
            Map<String, String> errors = new LinkedHashMap<>();
            binding.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        applicantEducationService.updateApplicantEducationDetails(applicantId, body);
        return ResponseEntity.ok("{\"message\":\"Education details saved successfully\"}");
    }
}
