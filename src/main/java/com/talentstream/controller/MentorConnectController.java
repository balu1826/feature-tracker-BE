package com.talentstream.controller;

import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.REGISTER;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.MENTOR_CONNECT_SCORE;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.MentorConnectDTO;
import com.talentstream.dto.MentorConnectRequestDTO;
import com.talentstream.service.ApplicantScoreService;
import com.talentstream.service.MentorConnectService;

@RestController
@RequestMapping("/api/mentor-connect")
public class MentorConnectController {

    private final MentorConnectService service;

    private static final Logger logger = LoggerFactory.getLogger(MentorConnectService.class);
    
    public ApplicantScoreService applicantScoreService;

    public MentorConnectController(MentorConnectService service, ApplicantScoreService applicantScoreService) {
        this.service = service;
        this.applicantScoreService = applicantScoreService;
    }
 
    //Get all upcoming and ongoing Meetings
    @GetMapping("/getAllMeetings")
    public ResponseEntity<?> getOngoingAndUpcomingMeetings() {
        logger.info("(Controller) Received request to - getOngoingAndUpcomingMeetings called");
        Map<String, List<MentorConnectDTO>> mentorConnects = service.getOngoingAndUpcomingMeetings();
         logger.info("(Controller) Completed request to - getOngoingAndUpcomingMeetings called");
        return ResponseEntity.ok(mentorConnects);
    }

    //Get a specific meeting by ID
    @GetMapping("/getMeetingById/{id}")
    public ResponseEntity<?> getMeetingById(@PathVariable Long id) {
        logger.info("(Controller) Received request to - getMeetingById called with id={}", id);
        MentorConnectDTO mentorConnect = service.getMeetingById(id);
         logger.info("(Controller) Completed request to - getMeetingById called with id={}", id);
        return ResponseEntity.ok(mentorConnect);
    }

    // update images (Admin use)
    @PatchMapping("/{id}/assets")
    public ResponseEntity<?> updateAssets(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> payload) {
        String banner = payload != null ? payload.get("bannerImageUrl") : null;
        String profile = payload != null ? payload.get("mentorProfileUrl") : null;
        logger.info("(Controller) Received request to - updateAssets called with id={}", id);
        String response = service.updateAssets(id, banner, profile);
         logger.info("(Controller) Completed request to - updateAssets called with id={}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/recruiter/createMentorConnect")
    public ResponseEntity<?> createMentorConnect(@Valid @RequestBody MentorConnectRequestDTO request,BindingResult result) {
        logger.info("(Controller) Received request to - createMentorConnect called");
		if (result.hasErrors()) {
			StringBuilder errors = new StringBuilder();
			result.getFieldErrors().forEach(err -> errors.append(err.getField()).append(" - ")
					.append(err.getDefaultMessage()).append(System.lineSeparator()));
            logger.error("(Controller) Validation errors in createMentorConnect: {}", errors.toString());
			return ResponseEntity.badRequest().body(errors.toString());
		}
         String response = service.createMentorConnect(request);
         logger.info("(Controller) Completed request to - createMentorConnect called");
        return ResponseEntity.ok(response);
    }

    //Register for a Mentor Connect session
    @PostMapping(value = "/registerMentorConnect/{mentorConnectId}/applicant/{applicantId}")
    public ResponseEntity<?> registerMentorConnect(@PathVariable Long mentorConnectId, @PathVariable Long applicantId) {
        logger.info("(Controller) Received request to - registerMentorConnect called with mentorConnectId={} and applicantId={}", mentorConnectId, applicantId);
         String response = service.registerMentorConnect(mentorConnectId, applicantId);
         applicantScoreService.updateApplicantScore(applicantId, MENTOR_CONNECT_SCORE, REGISTER);
         logger.info("(Controller) Completed request to - registerMentorConnect called with mentorConnectId={} and applicantId={}", mentorConnectId, applicantId);
        return ResponseEntity.ok(response);
    }

    //get all registrations for a Mentor Connect session
    @GetMapping(value = "/applicant/getAllRegisteredMentorConnects/{applicantId}")
    public ResponseEntity<?> getAllRegisteredMentorConnects(@PathVariable Long applicantId) {
        logger.info("(Controller) Received request to - getAllRegisteredMentorConnects called with applicantId={}", applicantId);
         List<Long> registeredMentorConnects = service.getAllRegisteredMentorConnects(applicantId);
         logger.info("(Controller) Completed request to - getAllRegisteredMentorConnects called with applicantId={}", applicantId);
        return ResponseEntity.ok().body("Registered Mentor connect Ids: " +registeredMentorConnects);
    }
}
