package com.talentstream.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ApplicantCardViewDTO;
import com.talentstream.service.ApplicantCardService;

import javax.validation.Valid;

@CrossOrigin("*")
@RestController
@RequestMapping("/applicant-card")
public class ApplicantCardController {

    private final ApplicantCardService applicantCardService;

    public ApplicantCardController(ApplicantCardService service) {
        this.applicantCardService = service;
    }
    
    @GetMapping("/{applicantId}/getApplciantCard")
    public ResponseEntity<ApplicantCardViewDTO> getApplicantCard(@PathVariable long applicantId) {
        ApplicantCardViewDTO dto = applicantCardService.getApplicantCardView(applicantId);
        return ResponseEntity.ok(dto);
    }
    
    @PutMapping("/{applicantId}/updateApplicantCard")
    public ResponseEntity<?> updateApplicantCardView(
            @PathVariable long applicantId,
            @Valid @RequestBody ApplicantCardViewDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        String response = applicantCardService.updateApplicantCardView(applicantId, dto);
        return ResponseEntity.ok().body(response);
    }

}
