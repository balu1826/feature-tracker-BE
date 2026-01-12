package com.talentstream.controller;

import com.talentstream.dto.HackathonCreateRequestDTO;
import com.talentstream.dto.HackathonUpdateDTO;
import com.talentstream.entity.Hackathon;
import com.talentstream.repository.JobRecruiterRepository;
import com.talentstream.service.ApplicantScoreService;
import com.talentstream.service.HackathonRegisterService;
import com.talentstream.service.HackathonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("/recruiter/hackathons")
@CrossOrigin
public class HackathonRecruiterController {
	private final HackathonService service;

	@Autowired
	private JobRecruiterRepository recruiterRepo;

	public HackathonRecruiterController(HackathonService service) {
		this.service = service;
	}

	@Autowired
	private HackathonRegisterService rService;

	@Autowired
	public ApplicantScoreService applicantScoreService;

	@PostMapping("/createHackathon")
	public ResponseEntity<?> createHackathon(@Valid @RequestBody HackathonCreateRequestDTO r, BindingResult result) {
		if (result.hasErrors()) {
			StringBuilder errors = new StringBuilder();
			result.getFieldErrors().forEach(err -> {
				errors.append(err.getField()).append(" - ").append(err.getDefaultMessage())
						.append(System.lineSeparator());
			});
			return ResponseEntity.badRequest().body(errors.toString());
		}

		try {
			Hackathon saved = service.createHackathon(r);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Hackathon created successfully with id " + saved.getId());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("getAllCreadtedHackathons/{recruiterId}")
	public ResponseEntity<?> getAll(@PathVariable Long recruiterId) {
		boolean recruiterExists = recruiterRepo.existsById(recruiterId);
		if (!recruiterExists) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No recruiter present with the id: " + recruiterId);
		}

		List<Hackathon> hackathons = service.getAllByCreaterId(recruiterId);
		if (hackathons.isEmpty()) {
			return ResponseEntity.ok("Recruiter with id " + recruiterId + " not created any hackathons");
		}

		return ResponseEntity.ok(hackathons);
	}

	@PutMapping("update/{hackathonId}")
	public ResponseEntity<?> updateHackathon(@PathVariable Long hackathonId, @Valid @RequestBody HackathonUpdateDTO r) {
		try {
			if (r.getRecruiterId() == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("recruiter id should not be null");
			}
			Hackathon updated = service.updateHackathon(hackathonId, r);
			return ResponseEntity.ok(updated);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error while updating hackathon: " + e.getMessage());
		}
	}

	@DeleteMapping("delete/{hackathonId}")
	public ResponseEntity<?> delete(@PathVariable Long hackathonId) {
		try {
			boolean deleted = service.delete(hackathonId);

			if (deleted) {
				return ResponseEntity.ok("Hackathon deleted successfully with id: " + hackathonId);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hackathon found with id: " + hackathonId);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error while deleting hackathon: " + e.getMessage());
		}
	}

	@GetMapping("/winsCount/{winnerId}")
	public ResponseEntity<?> getWinCount(@PathVariable Long winnerId) {
		try {
			Long count = service.getWinCountByUser(winnerId);
			if (count == null || count == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("message", "No wins found for the given winnerId", "count", 0));
			}
			return ResponseEntity.ok(Map.of("count", count));
		} catch (ResponseStatusException ex) {
			return ResponseEntity.status(ex.getStatus())
					.body(Map.of("error", ex.getReason(), "status", ex.getStatus().value()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to fetch win count", "details", e.getMessage()));
		}
	}

	@GetMapping("/registerCount/{userId}")
	public ResponseEntity<?> getWRegisterCount(@PathVariable Long userId) {
		try {
			Long count = rService.getRegistrationCountByUser(userId);
			if (count == null || count == 0) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Map.of("message", "No registrations found for the given UserId", "count", 0));
			}
			return ResponseEntity.ok(Map.of("count", count));
		} catch (ResponseStatusException ex) {
			return ResponseEntity.status(ex.getStatus())
					.body(Map.of("error", ex.getReason(), "status", ex.getStatus().value()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Failed to fetch register count", "details", e.getMessage()));
		}
	}

	@PostMapping("/{hackathonId}/declare-winner/{winnerId}")
	public ResponseEntity<?> declareWinner(@PathVariable Long hackathonId, @PathVariable Long winnerId) {
		try {
			System.out.println("Declaring winner started");
			Hackathon updatedHackathon = service.declareWinner(hackathonId, winnerId);
			service.addPointsToWinnerAndParticipants(hackathonId, winnerId);
			System.out.println("Declaring winner completed");
			return ResponseEntity.ok("Winner announced successfully with ID: " + updatedHackathon.getWinner());
		} catch (EntityNotFoundException ex) {
			return ResponseEntity.status(404).body(ex.getMessage());
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(400).body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(500).body("An unexpected error occurred: " + ex.getMessage());
		}
	}
}
