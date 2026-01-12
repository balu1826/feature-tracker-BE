package com.talentstream.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talentstream.dto.ProjectDetailsDTO;
import com.talentstream.service.ApplicantProjectService;

@RestController
@RequestMapping("/applicant-projects/{applicantId}")
public class ApplicantProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantProjectController.class);

    private final ApplicantProjectService projectService;

    public ApplicantProjectController(ApplicantProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/getApplicantProjects")
    public ResponseEntity<List<ProjectDetailsDTO>> getApplicantProjects(@PathVariable Long applicantId) {
        logger.info("Controller - Request received to fetch projects for applicantId={}", applicantId);
        List<ProjectDetailsDTO> projects = projectService.getApplicantProjects(applicantId);
        logger.debug("Controller - Found {} projects for applicantId={}", projects == null ? 0 : projects.size(), applicantId);
        return ResponseEntity.ok(projects);
    }
    

    @GetMapping("/getApplicantProjectById/{projectId}")
    public ResponseEntity<ProjectDetailsDTO> getApplicantProjectById(@PathVariable Long applicantId, @PathVariable Long projectId) {
        logger.info("Controller - Request received to fetch project by id: applicantId={}, projectId={}", applicantId, projectId);
        ProjectDetailsDTO project = projectService.getApplicantProjectById(applicantId, projectId);
        logger.debug("Controller - Found project: {} for applicantId={}, projectId={}", project == null ? "null" : "present", applicantId, projectId);
        return ResponseEntity.ok(project);
    }


    @PostMapping("/saveApplicantProject")
    public ResponseEntity<?> saveApplicantProject(
            @PathVariable Long applicantId,
            @Valid @RequestBody ProjectDetailsDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            logger.warn("Controller - Validation failed when saving project for applicantId={}: {}", applicantId, errors);
            return ResponseEntity.badRequest().body(errors);
        }
        logger.info("Controller - Request received to save project for applicantId={}", applicantId);
        String response = projectService.saveApplicantProject(applicantId, dto);
        logger.info("Controller - Save response for applicantId={}: {}", applicantId, response);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/updateApplicantProject/{projectId}")
    public ResponseEntity<?> updateApplicantProject(
            @PathVariable Long applicantId,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectDetailsDTO dto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            logger.warn("Controller - Validation failed when updating project applicantId={}, projectId={}: {}", applicantId, projectId, errors);
            return ResponseEntity.badRequest().body(errors);
        }
        logger.info("Controller - Request received to update project applicantId={}, projectId={}", applicantId, projectId);
        String response = projectService.updateApplicantProject(applicantId, projectId, dto);
        logger.info("Controller - Updated response for applicantId={}, projectId={}: {}", applicantId, projectId, response);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/deleteApplicantProject/{projectId}")
    public ResponseEntity<String> deleteApplicantProject(
            @PathVariable Long applicantId,
            @PathVariable Long projectId) {
        logger.info("Controller - Request received to delete project applicantId={}, projectId={}", applicantId, projectId);
        String response = projectService.deleteApplicantProject(applicantId, projectId);
        logger.info("Controller - Delete response for applicantId={}, projectId={}: {}", applicantId, projectId, response);
        return ResponseEntity.ok().body(response);
    }
}
