
package com.talentstream.controller;

import java.util.HashMap;
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

import com.talentstream.dto.PersonalDetailsDTO;
import com.talentstream.service.ApplicantPersonalService;

@CrossOrigin("*")
@RestController
@RequestMapping("/applicant-personal")
public class ApplicantPersonalController {

    private final ApplicantPersonalService applicantPersonalservice;

    public ApplicantPersonalController(ApplicantPersonalService service) {
        this.applicantPersonalservice = service;
    }

    @GetMapping("/{applicantId}/getApplicantPersonalDetails")
    public ResponseEntity<PersonalDetailsDTO> getApplicantPersonalDetails(@PathVariable long applicantId) {
        return ResponseEntity.ok(applicantPersonalservice.getApplicantPersonalDetails(applicantId));
    }
    
   

    @PutMapping("/{applicantId}/updateApplicantPersonalDetails")
    public ResponseEntity<?> updateApplicantPersonalDetails(@PathVariable long applicantId,
                                    @Valid @RequestBody PersonalDetailsDTO dto,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        String message = applicantPersonalservice.updateApplicantPersonalDetails(applicantId, dto);

        return ResponseEntity.ok().body(message);
    }

}
