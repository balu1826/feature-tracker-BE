package com.talentstream.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ApplicantSkillBadgeDTO;
import com.talentstream.dto.ApplicantSkillBadgeRequestDTO;
import com.talentstream.service.SkillBadgeService;

@RestController
@RequestMapping("/skill-badges")
public class SkillBadgeController {

    private static final Logger logger = LoggerFactory.getLogger(SkillBadgeController.class);

    @Autowired
    private SkillBadgeService skillBadgeService;
    
    @PostMapping("/save")
    public ResponseEntity<?> saveSkillBadge( @Valid @RequestBody ApplicantSkillBadgeRequestDTO request, BindingResult bindingResult) {
        logger.debug("Request received to saveSkillBadge - applicantId={}, badgeName={}, status={}",
            request == null ? null : request.getApplicantId(),
            request == null ? null : request.getSkillBadgeName(),
            request == null ? null : request.getStatus());

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
            logger.warn("saveSkillBadge - validation failed: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        ResponseEntity<String> result = skillBadgeService.saveApplicantSkillBadge(
            request.getApplicantId(),
            request.getSkillBadgeName(),
            request.getStatus()
        );
        logger.info(" saveSkillBadge - completed for applicantId={}, result={}", request.getApplicantId(), result.getStatusCode());
        return result;
    }
    
    @GetMapping("/{id}/skill-badges")
    public ResponseEntity<ApplicantSkillBadgeDTO> getApplicantSkillBadges(@PathVariable Long id,@RequestParam(defaultValue = "ALL") String status) {
        logger.debug("Request received to getApplicantSkillBadges - applicantId={}, status={}", id, status);
        ResponseEntity<ApplicantSkillBadgeDTO> response = skillBadgeService.getApplicantSkillBadges(id,status);
        logger.debug("Response sent from getApplicantSkillBadges - applicantId={}, statusCode={}", id, response == null ? null : response.getStatusCode());
        return response;
    }
    
    
}
