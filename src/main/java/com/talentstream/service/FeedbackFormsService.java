package com.talentstream.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.FeedbackFormsDto;
import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.dto.FeedbackQuestionData;
import com.talentstream.entity.FeedbackForms;
import com.talentstream.entity.JobRecruiter;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.FeedbackFormsApplicantAnswersRepository;
import com.talentstream.repository.FeedbackFormsRepository;
import com.talentstream.repository.JobRecruiterRepository;

@Service
public class FeedbackFormsService {
	private static final Logger logger = LoggerFactory.getLogger(FeedbackFormsService.class);
	private final ObjectMapper objectMapper;
	private final FeedbackFormsRepository feedbackFormsRepository;
	private final JobRecruiterRepository recruiterRepository;
	private final FeedbackFormsApplicantAnswersRepository answersRepo;

	public FeedbackFormsService(ObjectMapper objectMapper, FeedbackFormsRepository feedbackFormsRepository,
			JobRecruiterRepository recruiterRepository, FeedbackFormsApplicantAnswersRepository answersRepo) {
		this.objectMapper = objectMapper;
		this.feedbackFormsRepository = feedbackFormsRepository;
		this.recruiterRepository = recruiterRepository;
		this.answersRepo = answersRepo;
	}

	public String createFeedbackForm(Long recruiterId, FeedbackFormsDto dto) {
		logger.info("createFeedbackForm called for recruiterId={}", recruiterId);
		JobRecruiter recruiter = recruiterRepository.findById(recruiterId)
				.orElseThrow(() -> new CustomException("Recruiter not found", HttpStatus.NOT_FOUND));

		FeedbackForms form = new FeedbackForms();
		form.setRecruiter(recruiter);
		return saveOrUpdateForm(form, dto, true);
	}

	public String updateFeedbackForm(Long formId, Long recruiterId, FeedbackFormsDto feedbackFormDto) {
		logger.info("updateFeedbackForm called for formId={}", formId);
		FeedbackForms form = feedbackFormsRepository.findByIdAndRecruiterId(formId, recruiterId);
		if (form == null) {
			logger.warn("Feedback form not found for formId={} and recruiterId={}", formId, recruiterId);
			throw new CustomException(
					"Feedback form not found for formId=" + formId + " and recruiterId=" + recruiterId,
					HttpStatus.NOT_FOUND);
		}
		return saveOrUpdateForm(form, feedbackFormDto, false);
	}

	private String saveOrUpdateForm(FeedbackForms form, FeedbackFormsDto dto, boolean isNew) {
		logger.debug("saveOrUpdateForm called for form (title={}), isNew={}", dto.getFormName(), isNew);
		form.setMentorName(dto.getMentorName());
		form.setCollegeName(dto.getCollegeName());
		form.setTitle(dto.getFormName());
		form.setDescription(dto.getDescription());
		form.setIsActive(dto.getIsActive());
		assignQuestionNumbers(form, dto, isNew);

		try {
			String questionsJson = objectMapper.writeValueAsString(dto.getQuestions());
			form.setQuestions(questionsJson);
		} catch (Exception e) {
			logger.error("Error serializing questions for form (title={}) : {}", dto.getFormName(), e.getMessage());
			throw new CustomException("Error processing questions JSON" + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			feedbackFormsRepository.save(form);
		} catch (Exception e) {
			logger.error("Error saving feedback form (title={}) : {}", dto.getFormName(), e.getMessage());
			throw new CustomException("Error saving feedback form: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return isNew ? "Feedback form created successfully" : "Feedback form updated successfully";
	}

	private void assignQuestionNumbers(FeedbackForms form, FeedbackFormsDto dto, boolean isNew) {
		int nextQuestionNo = 1;

		if (!isNew && form.getQuestions() != null) {
			try {
				List<FeedbackQuestionData> existingQuestions = objectMapper.readValue(form.getQuestions(),
						objectMapper.getTypeFactory().constructCollectionType(List.class, FeedbackQuestionData.class));

				Set<Integer> existingNos = existingQuestions.stream().map(FeedbackQuestionData::getQuestionNo)
						.filter(n -> n != null).collect(Collectors.toSet());

				if (!existingNos.isEmpty()) {
					nextQuestionNo = existingNos.stream().max(Integer::compareTo).orElse(0) + 1;
				}

			} catch (Exception e) {
				logger.error("Error reading existing questions", e);
				throw new CustomException("Error processing existing questions", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		// Assign numbers to NEW questions only
		for (FeedbackQuestionData question : dto.getQuestions()) {
			if (question.getQuestionNo() == null) {
				question.setQuestionNo(nextQuestionNo++);
			}
		}
	}

	public FeedbackFormsResponseDTO getFeedbackFormById(Long formId, Long recruiterId) {
		logger.info("getFeedbackFormById called for formId={}", formId);
		FeedbackForms form = feedbackFormsRepository.findByIdAndRecruiterId(formId, recruiterId);
		if (form == null) {
			logger.warn("Feedback form not found for formId={} and recruiterId={}", formId, recruiterId);
			throw new CustomException(
					"Feedback form not found for formId=" + formId + " and recruiterId=" + recruiterId,
					HttpStatus.NOT_FOUND);
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

	public List<FeedbackFormsResponseDTO> getAllFeedbackForms(Long recruiterId) {
		logger.info("getAllFeedbackForms called");
		List<FeedbackForms> forms = feedbackFormsRepository.findByRecruiterId(recruiterId);
		if (forms.isEmpty()) {
			logger.warn("No feedback forms found for recruiterId={}", recruiterId);
			throw new CustomException("No feedback forms found for recruiterId=" + recruiterId, HttpStatus.NOT_FOUND);
		}

		List<FeedbackFormsResponseDTO> response = new ArrayList<>();
		for (FeedbackForms form : forms) {
			FeedbackFormsResponseDTO dto = new FeedbackFormsResponseDTO(form.getMentorName(), form.getCollegeName(),
					form.getTitle(), form.getIsActive());
			response.add(dto);
		}
		logger.debug("getAllFeedbackForms returning {} items", response.size());
		return response;
	}

	@Transactional
	public String deleteFeedbackForm(Long formId, Long recruiterId) {
		logger.info("deleteFeedbackForm called for formId={} and recruiterId={}", formId, recruiterId);
		FeedbackForms form = feedbackFormsRepository.findByIdAndRecruiterId(formId, recruiterId);
		if (form == null) {
			logger.warn("Feedback form not found for formId={} and recruiterId={}", formId, recruiterId);
			throw new CustomException(
					"Feedback form not found for formId=" + formId + " and recruiterId=" + recruiterId,
					HttpStatus.NOT_FOUND);
		}
		try {
			logger.debug("Deleting feedback form submissions id={}", formId);
			answersRepo.deleteByFormId(formId);
			logger.debug("Deleting feedback form id={}", formId);
			feedbackFormsRepository.deleteById(formId);
			logger.debug("Deleted feedback form id={}", formId);
			return "Feedback form deleted successfully";
		} catch (Exception ex) {
			logger.warn("Attempted to delete non-existent feedback form id={}", formId);
			throw new CustomException("Error deleting feedback form of id=" + formId + ", " + ex.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

}
