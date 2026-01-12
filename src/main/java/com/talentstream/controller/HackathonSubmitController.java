package com.talentstream.controller;

import com.talentstream.dto.HackathonSubmitRequestDTO;
import com.talentstream.entity.HackathonSubmit;
import com.talentstream.service.ApplicantScoreService;
import com.talentstream.service.HackathonSubmitService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.HACKATHON_SCORE;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.SUBMIT;


import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons/{hackathonId}")
@CrossOrigin
public class HackathonSubmitController {
    private final HackathonSubmitService service;
    @Autowired
	private ApplicantScoreService applicantScoreService;
    public HackathonSubmitController(HackathonSubmitService service) { this.service = service; }

    
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@PathVariable Long hackathonId, @Valid @RequestBody HackathonSubmitRequestDTO r, BindingResult result) {
    	 if (result.hasErrors()) {
 	        StringBuilder errors = new StringBuilder();
 	        result.getFieldErrors().forEach(err -> {
 	            errors.append(err.getField())
 	                  .append(" - ")
 	                  .append(err.getDefaultMessage())
 	                  .append(System.lineSeparator());
 	        });
 	        return ResponseEntity.badRequest().body(errors.toString());
 	    }
    	 try {
            System.out.println("Submitting response started");
 	        HackathonSubmit saved = service.submit(hackathonId, r);
            applicantScoreService.updateApplicantScore(r.getUserId(), HACKATHON_SCORE, SUBMIT);
 	        System.out.println("Submitting response completed");
 	        return ResponseEntity.status(HttpStatus.CREATED)
 	                .body("submitted resonse successfully with id " + saved.getId());
 	    } catch (IllegalArgumentException e) {
 	        return ResponseEntity.badRequest().body(e.getMessage());
 	    } catch (EntityNotFoundException e) {
 	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
 	    }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while submitting the response: " + e.getMessage());
}
    }

    @GetMapping("/getAllSubmissionsByHackathonId")
    public ResponseEntity<?> list(@PathVariable Long hackathonId) {
        try {
            List<HackathonSubmit> submissions = service.listByHackathon(hackathonId);
            if (submissions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("No submissions found for hackathon ID: " + hackathonId);
            }
            return ResponseEntity.ok(submissions);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving submissions: " + ex.getMessage());
        }
    }
}
