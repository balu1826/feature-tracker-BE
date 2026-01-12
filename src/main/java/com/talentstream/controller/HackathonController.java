package com.talentstream.controller;

import com.talentstream.entity.Hackathon;
import com.talentstream.service.HackathonService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/hackathons")
@CrossOrigin
public class HackathonController {
	private final HackathonService service;

	public HackathonController(HackathonService service) {
		this.service = service;
	}

	@GetMapping("/getAllHackathons")
	public ResponseEntity<?> list() {
	    List<Hackathon> hackathons = service.getAll();

	    if (hackathons.isEmpty()) {
	        return ResponseEntity.ok("There are no hackathons to show");
	    }

	    return ResponseEntity.ok(hackathons);
	}


	@GetMapping("/getHackathonDetails/{hackathonId}/{candidateOrRecruiterId}")
	public ResponseEntity<?> get(@PathVariable Long hackathonId, @PathVariable Long candidateOrRecruiterId) {
	    try {
	        if (candidateOrRecruiterId == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                                 .body("candidate or recruiter id is required");
	        }

	        Optional<Hackathon> hackathonOpt = service.get(hackathonId, candidateOrRecruiterId);

	        if (hackathonOpt.isPresent()) {
	            return ResponseEntity.ok(hackathonOpt.get());
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                                 .body("No hackathon found with id: " + hackathonId);
	        }
	    } catch (EntityNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(ex.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error while fetching hackathon: " + e.getMessage());
	    }
	}

	
	 @GetMapping("/active")
	    public ResponseEntity<?> getActiveHackathons() {
	        List<Hackathon> activeHackathons = service.getActiveHackathons();

	        if (activeHackathons.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No active hackathons found.");
	        }

	        return ResponseEntity.ok(activeHackathons);
	    }
	 
	 @GetMapping("/upcoming")
	    public ResponseEntity<?> getUpcomingHackathons() {
	        List<Hackathon> activeHackathons = service.getUpcomingHackathons();

	        if (activeHackathons.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No Upcoming hackathons found.");
	        }

	        return ResponseEntity.ok(activeHackathons);
	    }
	 
	 @GetMapping("/completed")
	    public ResponseEntity<?> getCompletedHackathons() {
	        List<Hackathon> activeHackathons = service.getCompletedHackathons();

	        if (activeHackathons.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No Completed hackathons found.");
	        }

	        return ResponseEntity.ok(activeHackathons);
	    }
	
	@GetMapping("/recommended/{applicantId}")
    public ResponseEntity<?> getRecommendedHackathons(@PathVariable Long applicantId) {
        try {
            List<Hackathon> recommended = service.getRecommendedHackathons(applicantId);

            if (recommended.isEmpty()) {
                return ResponseEntity.ok("There are no matching hackathons for applicant ID: " + applicantId);
            }

            return ResponseEntity.ok(recommended);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
	
	@GetMapping("/getApplicantRegisteredHackathons/{applicantId}")
    public ResponseEntity<?> getRegisteredHackathons(@PathVariable Long applicantId) {
        try {
            List<Hackathon> hackathons = service.getRegisteredHackathons(applicantId);

            if (hackathons.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No registered hackathons found for applicant id: " + applicantId);
            }

            return ResponseEntity.ok(hackathons);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching registered hackathons: " + e.getMessage());
        }
    }
	
}
