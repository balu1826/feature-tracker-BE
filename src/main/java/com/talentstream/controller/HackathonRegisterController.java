package com.talentstream.controller;

import com.talentstream.entity.HackathonRegister;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.HackathonRepository;
import com.talentstream.service.ApplicantScoreService;
import com.talentstream.service.HackathonRegisterService;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.HACKATHON_SCORE;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.REGISTER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hackathons")
public class HackathonRegisterController {
    private final HackathonRegisterService service;
    public HackathonRegisterController(HackathonRegisterService service) { this.service = service; }
    
    @Autowired
    public ApplicantRepository appRepo;
    
    @Autowired
    public HackathonRepository hackRepo;
    
    @Autowired
    public ApplicantScoreService applicantScoreService;
    
    @GetMapping("/{hackathonId}/getRegistrationStatus/{applicantId}")
    public ResponseEntity<?> getRegistraton(@PathVariable Long hackathonId, @PathVariable Long applicantId) {
    	if(hackathonId == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hackathon id should not be null");
    	}
    	if(applicantId == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("applicant id should not be null");
    	}
    	try {
    		HackathonRegister result = service.findByHackathonAndUser(hackathonId, applicantId);
    		return ResponseEntity.ok(result);
    	}
    	catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting registration: " + e.getMessage());
        }
    	
    }

    @PostMapping("/{hackathonId}/registerForHackathon/{applicantId}")
    public ResponseEntity<?> register(@PathVariable Long hackathonId, @PathVariable Long applicantId) {
    	if(hackathonId == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hackathon id should not be null");
    	}
    	if(applicantId == null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("applicant id should not be null");
    	}
        try {
            System.out.println("Registering applicant started");
            HackathonRegister saved = service.register(hackathonId, applicantId);
            applicantScoreService.updateApplicantScore(applicantId,HACKATHON_SCORE, REGISTER);
            System.out.println("Registering applicant completed");
            return ResponseEntity.ok("User registered successfully with id: " + saved.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while registering user: " + e.getMessage());
        }
    }

    @GetMapping("/{hackathonId}/getAllHackathonRegistrations")
    public ResponseEntity<?> list(@PathVariable Long hackathonId) {
        try {
            if (!hackRepo.existsById(hackathonId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("Hackathon not found with id: " + hackathonId);
            }

            List<HackathonRegister> registrations = service.listByHackathon(hackathonId);

            if (registrations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("No registrations present for hackathon with id: " + hackathonId);
            }

            return ResponseEntity.ok(registrations);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error while fetching registrations: " + e.getMessage());
        }
    }
    
    @GetMapping("/{applicantId}/getAllRegistrationStatus")
    public ResponseEntity<?> applicantRegisterList(@PathVariable Long applicantId) {
        try {
            if (!appRepo.existsById(applicantId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("Applicant not found with id: " + applicantId);
            }

            List<HackathonRegister> registrations = service.listByApplicant(applicantId);

            if (registrations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("No registrations present for applicant with id: " + applicantId);
            }

            return ResponseEntity.ok(registrations);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error while fetching registrations: " + e.getMessage());
        }
}


}
