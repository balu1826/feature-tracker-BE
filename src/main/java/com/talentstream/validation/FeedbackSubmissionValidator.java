package com.talentstream.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.FeedBackAnswerDto;
import com.talentstream.dto.FeedbackQuestionData;
import com.talentstream.exception.CustomException;

@Component
public class FeedbackSubmissionValidator {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackSubmissionValidator.class);

	// Regular expressions for validation
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{10,}$");

	private final ObjectMapper objectMapper;

	public FeedbackSubmissionValidator(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void validateSubmission(Long formId, String questionsJson, @Nonnull List<FeedBackAnswerDto> answers) {
		logger.debug("validateSubmission start: formId={}, questionsJsonLength={}, answersSize={}", formId,
				questionsJson == null ? 0 : questionsJson.length(), answers == null ? 0 : answers.size());

		List<String> phase1Errors = new ArrayList<>();
		List<String> phase2Errors = new ArrayList<>();

		// Parse questions JSON
		List<FeedbackQuestionData> questions;
		try {
			questions = objectMapper.readValue(questionsJson,
					objectMapper.getTypeFactory().constructCollectionType(List.class, FeedbackQuestionData.class));
		} catch (IOException e) {
			logger.error("Failed to parse questions JSON for formId={}", formId, e);
			throw new CustomException("Invalid form questions data", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 1. Validate question structure based on ValidQuestionValidator rules
		for (FeedbackQuestionData question : questions) {
			phase1Errors.addAll(validateQuestionStructure(question));
		}

		// 2. Answer count validation
		if (questions.size() != answers.size()) {
			phase1Errors.add("The number of answers provided does not match the number of questions. " + "Expected "
					+ questions.size() + " answers, but received " + answers.size() + ".");

			logger.warn("Answer count mismatch for formId={}: expected {}, got {}", formId, questions.size(),
					answers.size());
		}

		Map<Integer, FeedbackQuestionData> questionMap = questions.stream()
				.collect(Collectors.toMap(FeedbackQuestionData::getQuestionNo, q -> q));

		// 3. Null + Duplicate questionNo
		Set<Integer> seen = new HashSet<>();

		for (int i = 0; i < answers.size(); i++) {
			FeedBackAnswerDto a = answers.get(i);
			Integer qno = a.getQuestionNo();

			if (qno == null) {
				phase1Errors.add("questionNo is required for answer at index " + (i + 1));
				logger.warn("answers[{}].questionNo is null", i + 1);
				continue;
			}

			if (!seen.add(qno)) {
				phase1Errors.add("questionNo " + qno + " is duplicated at answer index " + (i + 1));
				logger.warn("Duplicate questionNo {} at answers[{}]", qno, i + 1);
			}
		}

		// 4. questionNo exists in form
		for (int i = 0; i < answers.size(); i++) {
			FeedBackAnswerDto a = answers.get(i);
			Integer qno = a.getQuestionNo();

			if (qno != null && !questionMap.containsKey(qno)) {
				phase1Errors.add("questionNo " + qno + " does not exist in this form at answer index " + (i + 1));
				logger.warn("answers[{}].questionNo {} does not exist in form {}", i + 1, qno, formId);
			}
		}

		// 5. Required field validation (PHASE 1)
		Map<Integer, Object> answerByQno = answers.stream().filter(a -> a.getQuestionNo() != null)
				.filter(a -> a.getAnswer() != null)
				.collect(Collectors.toMap(FeedBackAnswerDto::getQuestionNo, FeedBackAnswerDto::getAnswer, (a, b) -> a));

		for (FeedbackQuestionData q : questions) {
			if (Boolean.TRUE.equals(q.getIsRequired())) {
				Object ans = answerByQno.get(q.getQuestionNo());

				if (ans == null || (ans instanceof String && ((String) ans).trim().isEmpty())
						|| (ans instanceof Collection && ((Collection<?>) ans).isEmpty())) {
					phase1Errors.add("Answer required for questionNo: " + q.getQuestionNo());
					logger.warn("Required answer missing for questionNo {} in form {}", q.getQuestionNo(), formId);
				}
			}
		}

		// THROW PHASE 1 ERRORS IF ANY
		if (!phase1Errors.isEmpty()) {
			logger.warn("Phase 1 validation failed for formId={}: errors={}", formId, phase1Errors);
			throw new CustomException(phase1Errors.toString(), HttpStatus.BAD_REQUEST);
		}

		// 6. Answer type & value validation (PHASE 2 - only if phase 1 passes)
		for (int i = 0; i < answers.size(); i++) {
			FeedBackAnswerDto a = answers.get(i);
			Integer qno = a.getQuestionNo();

			if (qno == null || !questionMap.containsKey(qno)) {
				continue;
			}

			FeedbackQuestionData q = questionMap.get(qno);
			
			// Skip validation if answer is null (already caught in phase 1 for required fields)
			if (a.getAnswer() == null) {
				continue;
			}
			
			// Skip validation for empty answers for non-required fields
			if (a.getAnswer() instanceof String && ((String) a.getAnswer()).trim().isEmpty() 
					&& !Boolean.TRUE.equals(q.getIsRequired())) {
				continue;
			}
			
			// Skip validation for empty collections for non-required fields
			if (a.getAnswer() instanceof Collection && ((Collection<?>) a.getAnswer()).isEmpty() 
					&& !Boolean.TRUE.equals(q.getIsRequired())) {
				continue;
			}
			
			phase2Errors.addAll(validateAnswerForQuestion(q, a.getAnswer(), i));
		}

		// THROW PHASE 2 ERRORS IF ANY
		if (!phase2Errors.isEmpty()) {
			logger.warn("Phase 2 validation failed for formId={}: errors={}", formId, phase2Errors);
			throw new CustomException(phase2Errors.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	// Validate question structure based on ValidQuestionValidator rules
	private List<String> validateQuestionStructure(FeedbackQuestionData q) {
		List<String> errors = new ArrayList<>();

		if (q == null || q.getQuestionType() == null) {
			return errors;
		}

		String type = q.getQuestionType().toUpperCase();
		List<String> options = q.getOptions();

		switch (type) {
		case "RADIO":
		case "CHECKBOX":
			if (options == null || options.size() < 2) {
				errors.add("QuestionNo " + q.getQuestionNo() + ": For type " + type
						+ ", options must contain at least 2 entries");
				logger.debug("Invalid options for RADIO/CHECKBOX questionNo {}: options={} ", q.getQuestionNo(),
						options);
			}
			break;

		case "REVIEW":
			if (options == null || options.size() < 1) {
				errors.add("QuestionNo " + q.getQuestionNo() + ": For type REVIEW, "
						+ "options must contain at least 1 entry");
				logger.debug("Invalid options for REVIEW questionNo {}: options={}", q.getQuestionNo(), options);
			}
			break;

		case "NUMBER":
		case "TEXTAREA":
		case "EMAIL":
		case "TEXT":
		case "PHONE":
			// For these types, options should not be provided
			if (options != null && !options.isEmpty()) {
				errors.add("QuestionNo " + q.getQuestionNo() + ": Options are only allowed for "
						+ "RADIO, CHECKBOX or REVIEW types");
				logger.debug("Extraneous options for questionNo {} of type {}: options={}", q.getQuestionNo(), type,
						options);
			}
			break;
		}

		return errors;
	}

	// Answer Validation Helpers (NO THROWS)
	private List<String> validateAnswerForQuestion(FeedbackQuestionData q, Object answer, int index) {
		logger.debug("Validating answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);
		List<String> errors = new ArrayList<>();

		String type = q.getQuestionType();
		if (type == null) {
			errors.add("questionType missing for questionNo " + q.getQuestionNo() + " at answer index " + (index + 1));
			return errors;
		}

		String normalized = type.trim().toUpperCase();

		switch (normalized) {
		case "TEXT":
		case "TEXTAREA":
			validateText(q, answer, index, errors);
			break;

		case "EMAIL":
			validateEmail(q, answer, index, errors);
			break;

		case "PHONE":
			validatePhone(q, answer, index, errors);
			break;

		case "RADIO":
			validateRadio(q, answer, index, errors);
			break;

		case "CHECKBOX":
			validateCheckbox(q, answer, index, errors);
			break;

		case "REVIEW":
			validateReview(q, answer, index, errors);
			break;

		case "NUMBER":
			validateNumber(q, answer, index, errors);
			break;

		default:
			// For unknown types, treat as TEXT
			validateText(q, answer, index, errors);
		}

		return errors;
	}

	// TEXT & TEXTAREA validation
	private void validateText(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating TEXT answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (!(answer instanceof String)) {
			errors.add("Invalid answer type for TEXT question (questionNo=" + q.getQuestionNo()
					+ "). Expected a string value.");
			return;
		}

		String s = ((String) answer).trim();
		// Empty validation already done in phase 1 for required fields
		// No additional validation needed for TEXT type
	}

	// EMAIL validation
	private void validateEmail(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating EMAIL answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (!(answer instanceof String)) {
			errors.add("Invalid answer type for EMAIL question (questionNo=" + q.getQuestionNo()
					+ "). Expected a string value.");
			return;
		}

		String email = ((String) answer).trim();

		if (email.isEmpty()) {
			return; // Already handled in phase 1 for required fields
		}

		if (!EMAIL_PATTERN.matcher(email).matches()) {
			errors.add("Invalid email format for EMAIL question (questionNo=" + q.getQuestionNo() + ").");
		}
	}

	// PHONE validation
	private void validatePhone(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating PHONE answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (!(answer instanceof String)) {
			errors.add("Invalid answer type for PHONE question (questionNo=" + q.getQuestionNo()
					+ "). Expected a string value.");
			return;
		}

		String phone = ((String) answer).trim();

		if (phone.isEmpty()) {
			return; // Already handled in phase 1 for required fields
		}

		if (!PHONE_PATTERN.matcher(phone).matches()) {
			errors.add("Invalid phone number format for PHONE question (questionNo=" + q.getQuestionNo() + ").");
		}
	}

	// RADIO validation (single choice)
	private void validateRadio(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating RADIO answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (!(answer instanceof String)) {
			errors.add("Invalid answer type for RADIO question (questionNo=" + q.getQuestionNo()
					+ "). Expected a single option.");
			return;
		}

		String option = ((String) answer).trim();

		if (option.isEmpty()) {
			return; // Already handled in phase 1 for required fields
		}

		if (q.getOptions() == null || !q.getOptions().contains(option)) {
			errors.add("Invalid option selected for RADIO question (questionNo=" + q.getQuestionNo()
					+ "). Allowed options: " + q.getOptions());
		}
	}

	// CHECKBOX validation (multiple choice)
	@SuppressWarnings("unchecked")
	private void validateCheckbox(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating CHECKBOX answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (!(answer instanceof List)) {
			errors.add("Invalid answer type for CHECKBOX question (questionNo=" + q.getQuestionNo()
					+ "). Expected a list of options.");
			return;
		}

		List<Object> selectedOptions = (List<Object>) answer;

		if (selectedOptions.isEmpty()) {
			return; // Already handled in phase 1 for required fields
		}

		for (Object option : selectedOptions) {
			if (!(option instanceof String) || q.getOptions() == null || !q.getOptions().contains(option)) {
				errors.add("Invalid option selected for CHECKBOX question (questionNo=" + q.getQuestionNo()
						+ "). Allowed options: " + q.getOptions());
				break;
			}
		}
	}

	// REVIEW validation - options define the valid values
	private void validateReview(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating REVIEW answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		// REVIEW type must have options defined
		List<String> options = q.getOptions();
		if (options == null || options.isEmpty()) {
			errors.add("REVIEW question must have predefined options (questionNo=" + q.getQuestionNo() + ").");
			return;
		}

		// REVIEW answer can be option value or index
		if (answer instanceof String) {
			String answerStr = ((String) answer).trim();

			if (answerStr.isEmpty()) {
				return; // Already handled in phase 1 for required fields
			}

			if (!options.contains(answerStr)) {
				try {
					int indexVal = Integer.parseInt(answerStr);
					if (indexVal < 1 || indexVal > options.size()) {
						errors.add("Invalid answer for REVIEW question (questionNo=" + q.getQuestionNo()
								+ "). Allowed values: " + options);
					}
				} catch (NumberFormatException e) {
					errors.add("Invalid answer for REVIEW question (questionNo=" + q.getQuestionNo()
							+ "). Allowed values: " + options);
					logger.debug("Invalid REVIEW index for questionNo {} value={}", q.getQuestionNo(), answerStr);
				}
			}
		} else if (answer instanceof Number) {
			int indexVal = ((Number) answer).intValue();
			if (indexVal < 1 || indexVal > options.size()) {
				errors.add("Invalid answer for REVIEW question (questionNo=" + q.getQuestionNo()
						+ "). Allowed range: 1-" + options.size());
			}
		} else {
			errors.add("Invalid answer type for REVIEW question (questionNo=" + q.getQuestionNo()
					+ "). Expected string or number.");
		}
	}

	// NUMBER validation
	private void validateNumber(FeedbackQuestionData q, Object answer, int index, List<String> errors) {
		logger.debug("Validating NUMBER answer for questionNo {} at answers[{}]", q.getQuestionNo(), index + 1);

		if (answer == null) {
			return; // Already handled in phase 1
		}

		if (answer instanceof Number) {
			return; // valid
		}

		if (answer instanceof String) {
			String numberStr = ((String) answer).trim();

			if (numberStr.isEmpty()) {
				return; // Already handled in phase 1 for required fields
			}

			try {
				Double.parseDouble(numberStr);
			} catch (NumberFormatException e) {
				logger.debug("Invalid NUMBER format for questionNo {} value={}", q.getQuestionNo(), numberStr);
				errors.add("Invalid number format for NUMBER question (questionNo=" + q.getQuestionNo() + ").");
			}
		} else {
			errors.add("Invalid answer type for NUMBER question (questionNo=" + q.getQuestionNo()
					+ "). Expected a numeric value.");
		}
	}
}