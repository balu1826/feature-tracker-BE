package com.talentstream.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.FeedBackAnswerDto;
import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.FeedbackFormsApplicantAnswersRepository;
import com.talentstream.repository.FeedbackFormsRepository;
import com.talentstream.validation.FeedbackSubmissionValidator;
import com.talentstream.exception.CustomException;
import org.springframework.http.HttpStatus;
import com.talentstream.entity.FeedbackForms;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.FeedbackFormsApplicantAnswers;

@Service
public class FeedbackFormsApplicantAnswersService {
	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsApplicantAnswersService.class);

	private final FeedbackFormsApplicantAnswersRepository answersRepo;
	private final FeedbackFormsRepository formsRepo;
	private final ApplicantRepository applicantRepo;
	private final ObjectMapper objectMapper;
	private final FeedbackSubmissionValidator submissionValidator;

	public FeedbackFormsApplicantAnswersService(FeedbackFormsApplicantAnswersRepository answersRepo,
			FeedbackFormsRepository formsRepo, ApplicantRepository applicantRepo, ObjectMapper objectMapper,
			FeedbackSubmissionValidator submissionValidator) {
		this.answersRepo = answersRepo;
		this.formsRepo = formsRepo;
		this.applicantRepo = applicantRepo;
		this.objectMapper = objectMapper;
		this.submissionValidator = submissionValidator;
	}

	@Transactional
	public String submitFeedback(Long formId, Long applicantId, @Nonnull List<FeedBackAnswerDto> answers) {
		FeedbackForms form = formsRepo.findByIdAndIsActiveTrue(formId)
				.orElseThrow(() -> new CustomException("Form not found or inactive", HttpStatus.BAD_REQUEST));

		Applicant applicant = applicantRepo.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

		answersRepo.findByFormIdAndApplicantId(formId, applicantId).ifPresent(existing -> {
			throw new CustomException("Feedback already submitted for this form by the applicant",
					HttpStatus.BAD_REQUEST);
		});

		submissionValidator.validateSubmission(formId, form.getQuestions(), answers);

		try {
			String answersJson = objectMapper.writeValueAsString(answers);
			FeedbackFormsApplicantAnswers entity = new FeedbackFormsApplicantAnswers();
			entity.setForm(form);
			entity.setApplicant(applicant);
			entity.setAnswers(answersJson);
			answersRepo.save(entity);
			return "Feedback submitted successfully.";
		} catch (Exception ex) {
			logger.error("Failed to save answers: {}", ex.getMessage(), ex);
			throw new CustomException("Failed to save answers", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public FeedbackFormsResponseDTO getFormById(Long formId, Long applicantId) {
		logger.info("Fetching form by ID={} for applicantId={}", formId, applicantId);
		applicantRepo.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));
		FeedbackForms form = formsRepo.findById(formId)
				.orElseThrow(() -> new CustomException("Form not found", HttpStatus.NOT_FOUND));
		if (form.getIsActive() == false) {
			throw new CustomException("Form is inactive", HttpStatus.BAD_REQUEST);
		}
		try {
			FeedbackFormsResponseDTO dto = new FeedbackFormsResponseDTO();
			dto.setFormId(form.getId());
			dto.setRecruiterId(form.getRecruiter().getRecruiterId());
			dto.setMentorName(form.getMentorName());
			dto.setCollegeName(form.getCollegeName());
			dto.setFormName(form.getTitle());
			dto.setDescription(form.getDescription());
			dto.setIsActive(form.getIsActive());
			dto.setQuestions(form.getQuestions());
			return dto;
		} catch (Exception e) {
			logger.error("Error deserializing questions for formId={}: {}", formId, e.getMessage());
			throw new CustomException("Error processing questions JSON" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public List<FeedbackFormsResponseDTO> getAllForms(Long applicantId) {
		logger.info("Fetching all active forms for applicantId={}", applicantId);
		applicantRepo.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));
		try {
			List<FeedbackFormsResponseDTO> forms = formsRepo.findAllByIsActiveTrue();
			logger.info("Retrieved {} active forms", forms.size());
			return forms;
		} catch (Exception e) {
			logger.error("Error fetching all active forms: {}", e.getMessage(), e);
			throw new CustomException("Error fetching all active forms: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}