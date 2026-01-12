package com.talentstream.controller;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.FeedBackAnswerDto;
import com.talentstream.service.FeedbackFormsApplicantAnswersService;

@RestController
@RequestMapping("/api/feedback-forms/applicant/{applicantId}")
public class FeedbackFormsApplicantAnswersController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsApplicantAnswersController.class);
	private final FeedbackFormsApplicantAnswersService answersService;

	public FeedbackFormsApplicantAnswersController(FeedbackFormsApplicantAnswersService answersService) {
		this.answersService = answersService;
	}

	@PostMapping("/submitFeedback/{formId}")
	public ResponseEntity<?> submitFeedback(@PathVariable Long formId, @PathVariable Long applicantId,
			@RequestBody @Nonnull List<FeedBackAnswerDto> answers) {

		logger.info("Submitting feedback answers | formId={} applicantId={} answerCount={}", formId, applicantId,
				answers != null ? answers.size() : 0);

		String saved = answersService.submitFeedback(formId, applicantId, answers);

		return ResponseEntity.ok().body(saved);
	}

	@GetMapping("/getFormById/{formId}")
	public ResponseEntity<?> getFormById(@PathVariable Long applicantId, @PathVariable Long formId) {
		logger.info("Retrieving form by ID | formId={}", formId);
		return ResponseEntity.ok().body(answersService.getFormById(formId, applicantId));
	}

	@GetMapping("/getAllForms")
	public ResponseEntity<?> getAllForms(@PathVariable Long applicantId) {
		logger.info("Retrieving all forms for applicantId={}", applicantId);
		return ResponseEntity.ok().body(answersService.getAllForms(applicantId));
	}
}
