package com.talentstream.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentstream.dto.FeedBackAnswerDto;
import com.talentstream.dto.FeedbackQuestionData;
import com.talentstream.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service component for validating applicant answers against form questions.
 * Handles all validation logic including:
 * - Answer count validation
 * - Question number validation
 * - Required field validation
 * - Answer type validation
 */
@Component
public class AnswerValidationService {
    private static final Logger logger = LoggerFactory.getLogger(AnswerValidationService.class);

    private final ObjectMapper objectMapper;

    public AnswerValidationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Validates a list of answers against form questions
     *
     * @param questionsJson JSON string containing list of FeedbackQuestionData
     * @param answers       List of FeedBackAnswerDto to validate
     * @throws CustomException if validation fails
     */
    public void validateAnswers(String questionsJson, List<FeedBackAnswerDto> answers) {
        try {
            List<FeedbackQuestionData> questions = objectMapper.readValue(questionsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FeedbackQuestionData.class));

            validateAnswerCount(questions, answers);
            validateAnswerContent(questions, answers);

        } catch (IOException ioe) {
            logger.error("Failed to parse form questions JSON: {}", ioe.getMessage(), ioe);
            throw new CustomException("Invalid form questions data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Validates that the number of answers matches the number of questions
     */
    private void validateAnswerCount(List<FeedbackQuestionData> questions, List<FeedBackAnswerDto> answers) {
        if (questions.size() != answers.size()) {
            logger.warn("Answer count mismatch: expected {} answers, got {}", questions.size(), answers.size());
            throw new CustomException("Answer count mismatch", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Validates the content of each answer
     */
    private void validateAnswerContent(List<FeedbackQuestionData> questions, List<FeedBackAnswerDto> answers) {
        Map<Integer, FeedbackQuestionData> questionMap = questions.stream()
                .collect(Collectors.toMap(FeedbackQuestionData::getQuestionNo, q -> q));

        for (FeedBackAnswerDto answer : answers) {
            validateSingleAnswer(answer, questionMap);
        }
    }

    /**
     * Validates a single answer against its corresponding question
     */
    private void validateSingleAnswer(FeedBackAnswerDto answer, Map<Integer, FeedbackQuestionData> questionMap) {
        Integer questionNo = answer.getQuestionNo();

        // Validate questionNo is provided and valid
        if (questionNo == null || questionNo <= 0) {
            logger.warn("Invalid question number: {}", questionNo);
            throw new CustomException("Invalid questionNo", HttpStatus.BAD_REQUEST);
        }

        // Validate question exists
        FeedbackQuestionData question = questionMap.get(questionNo);
        if (question == null) {
            logger.warn("Question not found with questionNo: {}", questionNo);
            throw new CustomException("Invalid questionNo", HttpStatus.BAD_REQUEST);
        }

        // Validate required answers are provided
        if (question.getIsRequired() && answer.getAnswer() == null) {
            logger.warn("Required answer missing for questionNo: {}", questionNo);
            throw new CustomException("Answer required", HttpStatus.BAD_REQUEST);
        }

        // Validate answer type matches question type
        validateAnswerType(question.getQuestionType(), answer.getAnswer());
    }

    /**
     * Validates that the answer value matches the expected type for the question
     */
    private void validateAnswerType(String type, Object answer) {
        if (answer == null) {
            return;
        }

        switch (type) {
            case "TEXT":
            case "TEXTAREA":
            case "EMAIL":
            case "PHONE":
            case "REVIEW":
            case "RADIO":
                if (!(answer instanceof String)) {
                    logger.warn("Answer for type {} must be string, got {}", type, answer.getClass().getSimpleName());
                    throw new CustomException("Answer must be text", HttpStatus.BAD_REQUEST);
                }
                break;

            case "NUMBER":
                if (!(answer instanceof Number)) {
                    logger.warn("Answer for type NUMBER must be number, got {}", answer.getClass().getSimpleName());
                    throw new CustomException("Answer must be number", HttpStatus.BAD_REQUEST);
                }
                break;

            case "CHECKBOX":
                if (!(answer instanceof List)) {
                    logger.warn("Answer for type CHECKBOX must be list, got {}", answer.getClass().getSimpleName());
                    throw new CustomException("Answer must be list", HttpStatus.BAD_REQUEST);
                }
                break;

            default:
                logger.warn("Unknown question type: {}", type);
                throw new CustomException("Unknown question type", HttpStatus.BAD_REQUEST);
        }
    }
}
