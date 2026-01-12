package com.talentstream.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.service.ApplicantKeySkillsService;

@RestController
@CrossOrigin("*")
@RequestMapping("/applicantprofile")
public class ApplicantKeySkillsController {

    private final ApplicantKeySkillsService service;
    private static final Logger logger = LoggerFactory.getLogger(ApplicantKeySkillsController.class);


    public ApplicantKeySkillsController(ApplicantKeySkillsService service) {
        this.service = service;
    }

    // GET list for the card
    @GetMapping("/{applicantId}/skills")
    public ResponseEntity<List<String>> getApplicantSkills(@PathVariable long applicantId) {
        logger.debug("Receiving request to get applicant skills for applicantId={}", applicantId);
        List<String> skills = service.getSkills(applicantId);
        logger.debug("Returning {} skills for applicantId={}", skills == null ? 0 : skills.size(), applicantId);
        return ResponseEntity.ok(skills);
    }

    // PUT replaces the set (idempotent)
    @PutMapping("/{applicantId}/skills")
    public ResponseEntity<?> replaceApplicantSkills(@PathVariable long applicantId,  @RequestBody Map<String, List<String>> body) {
        List<String> skills = body == null ? null : body.get("skills");
        logger.debug("Receiving request to replace applicant skills for applicantId={}, skillCount={}", applicantId, skills == null ? 0 : skills.size());
        String result = service.replaceApplicantSkills(applicantId, skills);
        logger.info("Replaced applicant skills for applicantId={}, result={}", applicantId, result);
        return ResponseEntity.ok().body(result);
    }
}
