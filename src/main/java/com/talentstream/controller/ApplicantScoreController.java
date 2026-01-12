package com.talentstream.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ApplicantScoringDetailsResponseDTO;
import com.talentstream.service.ApplicantScoreService;

@RestController
@RequestMapping("/applicant-scores")
public class ApplicantScoreController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantScoreController.class);

	@Autowired
	private ApplicantScoreService applicantScoreService;

	@GetMapping("/applicant/{applicantId}/getTotalScore")
	public ResponseEntity<?> getTotalScore(@PathVariable long applicantId) {
		logger.info("Request: getTotalScore for applicantId={}", applicantId);
		Integer totalScore = applicantScoreService.getTotalScore(applicantId);
		logger.info("Response for applicantId={} -> status={}", applicantId, totalScore);
		return ResponseEntity.ok("Total score for applicant ID " + applicantId + " is " + totalScore);
	}

	@GetMapping("/applicant/{applicantId}/getApplicantScoreDetails")
	public ResponseEntity<ApplicantScoringDetailsResponseDTO> getApplicantScoreDetails(@PathVariable long applicantId) {
		logger.info("Request: getApplicantScoreDetails for applicantId={}", applicantId);
		ApplicantScoringDetailsResponseDTO scoreDetails = applicantScoreService.getApplicantScoreDetails(applicantId);
		logger.info("Response for applicantId={} -> {}", applicantId, scoreDetails);
		return ResponseEntity.ok(scoreDetails);
	}

}
