package com.talentstream.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.talentstream.dto.FeedbackFormsDto;
import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.service.FeedbackFormsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/feedback-forms/recruiter/{recruiterId}")
public class FeedbackFormsController {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsController.class);
    private final FeedbackFormsService feedbackFormsService;

    //constructor
    public FeedbackFormsController(FeedbackFormsService feedbackFormsService) {
        this.feedbackFormsService = feedbackFormsService;
    }
	
    @PostMapping("/createFeedbackForm")
    public ResponseEntity<?> createFeedbackForm(
            @PathVariable Long recruiterId,
            @Valid @RequestBody FeedbackFormsDto feedbackFormDto,
            BindingResult bindingResult) {
        
        logger.info("Received request to create feedback form for recruiter ID: {}", recruiterId);
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            logger.warn("Validation failed for feedback form creation: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
        
        String response = feedbackFormsService.createFeedbackForm(recruiterId, feedbackFormDto);
        logger.info("Successfully created feedback form for recruiter ID: {}", recruiterId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getFeedBackFormById/{formId}")
    public ResponseEntity<?> getFeedbackForm(
            @PathVariable Long recruiterId,
            @PathVariable Long formId) {
        
        logger.info("Fetching feedback form with ID: {} for recruiter ID: {}", formId, recruiterId);
        FeedbackFormsResponseDTO response = feedbackFormsService.getFeedbackFormById(formId, recruiterId);
        logger.debug("Retrieved feedback form: {}", response);
        return ResponseEntity.ok(response);
    }

   @GetMapping("/getAllFeedbackForms")
   public ResponseEntity<?> getAllFeedbackForms(@PathVariable Long recruiterId) {
       logger.info("Fetching all feedback forms for recruiter ID: {}", recruiterId);
       List<FeedbackFormsResponseDTO> response = feedbackFormsService.getAllFeedbackForms(recruiterId);
       logger.debug("Retrieved {} feedback forms", response.size());
       return ResponseEntity.ok(response);
   }

   @PutMapping("/updateFeedbackForm/{formId}")
   public ResponseEntity<?> updateFeedbackForm(
           @PathVariable Long recruiterId,
           @PathVariable Long formId,
           @Valid @RequestBody FeedbackFormsDto feedbackFormDto,
           BindingResult bindingResult) {
       
       logger.info("Updating feedback form with ID: {} for recruiter ID: {}", formId, recruiterId);
       
       if (bindingResult.hasErrors()) {
           Map<String, String> errors = new HashMap<>();
           bindingResult.getFieldErrors().forEach(error ->
                   errors.put(error.getField(), error.getDefaultMessage()));
           logger.warn("Validation failed for feedback form update: {}", errors);
           return ResponseEntity.badRequest().body(errors);
       }

       String response = feedbackFormsService.updateFeedbackForm(formId, recruiterId, feedbackFormDto);
       logger.info("Successfully updated feedback form with ID: {}", formId);
       return ResponseEntity.ok(response);
   }

   @DeleteMapping("/deleteFeedbackForm/{formId}")
   public ResponseEntity<?> deleteFeedbackForm(
           @PathVariable Long recruiterId,
           @PathVariable Long formId) {
       
       logger.info("Deleting feedback form with ID: {} for recruiter ID: {}", formId, recruiterId);
       String response = feedbackFormsService.deleteFeedbackForm(formId, recruiterId);
       logger.info("Successfully deleted feedback form with ID: {}", formId);
       return ResponseEntity.ok(response);
   }
}