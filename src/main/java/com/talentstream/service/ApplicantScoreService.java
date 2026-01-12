package com.talentstream.service;

import static com.talentstream.util.ActivityConstantsUtils.ActivityName.*;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.*;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.talentstream.dto.ApplicantScoringDetailsResponseDTO;
import com.talentstream.dto.BadgeScoreDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantScore;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.ApplicantScoreRepository;
import com.talentstream.repository.ScoringRulesRepository;

@Service
public class ApplicantScoreService {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantScoreService.class);

	private ApplicantScoreRepository applicantScoreRepository;
	private ApplicantProfileRepository applicantProfileRepository;
	private ScoringRulesRepository scoringRulesRepository;

	private ApplicantRepository applicantRepository;

	public ApplicantScoreService(ApplicantScoreRepository applicantScoreRepository,
			ApplicantProfileRepository applicantProfileRepository, ScoringRulesRepository scoringRulesRepository,
			ApplicantRepository applicantRepository) {
		this.applicantRepository = applicantRepository;
		this.applicantScoreRepository = applicantScoreRepository;
		this.applicantProfileRepository = applicantProfileRepository;
		this.scoringRulesRepository = scoringRulesRepository;
	}

	public Integer getTotalScore(long applicantId) {
		try {
			ApplicantProfile applicant = applicantProfileRepository.findByApplicantId(applicantId);
			if (applicant == null) {
				throw new CustomException("Applicant not found with id: " + applicantId, HttpStatus.NOT_FOUND);
			}
			Integer totalScore = applicantScoreRepository.findTotalScoreByApplicantId(applicantId);
			if (totalScore == null) {
				throw new CustomException("Applicant score not found for applicant id: " + applicantId,
						HttpStatus.NOT_FOUND);
			}
			return totalScore;
		} catch (Exception e) {
			logger.error("Error retrieving total score for applicantId {}: {}", applicantId, e.getMessage(), e);
			throw new CustomException("Error retrieving total score for applicant id: " + applicantId,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Async
	public void updateApplicantScore(Long applicantId, String activityName, String activityDetail) {

		logger.info("Starting score update for applicantId {} (activity: {}, detail: {})", applicantId, activityName,
				activityDetail);
		ApplicantProfile applicant = applicantProfileRepository.findByApplicantId(applicantId);
		if (applicant == null) {
			throw new CustomException("Applicant not found with id: " + applicantId, HttpStatus.NOT_FOUND);
		}
		Integer points = scoringRulesRepository.findPointsByActivityNameAndDetail(activityName, activityDetail);
		if (points == null) {
			throw new CustomException(
					"Scoring rule not found for activity: " + activityName + " with detail: " + activityDetail,
					HttpStatus.NOT_FOUND);
		}
		try {
			ApplicantScore applicantScore = applicantScoreRepository.findByApplicantId(applicantId)
					.orElseGet(() -> createNewApplicantScore(applicantId));

			updateSpecificScore(applicantScore, activityName, points);
			applicantScore.setTotal_score(applicantScore.getTotal_score() + points);
			updateLevelAndBadge(applicantScore);
			applicantScore.setLastUpdated(LocalDateTime.now());
			applicantScoreRepository.save(applicantScore);

			logger.info("Updated applicant score for applicantId {} (activity: {}, detail: {})", applicantId,
					activityName, activityDetail);

		} catch (Exception e) {
			logger.error("Error updating applicant score for applicantId {}: {}", applicantId, e.getMessage(), e);
			throw new CustomException("Error updating applicant score for applicant id: " + applicantId,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ApplicantScore createNewApplicantScore(Long applicantId) {
		try {
			logger.info("Creating new applicant score for applicantId {}", applicantId);
			Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(
					() -> new CustomException("Applicant not found with id: " + applicantId, HttpStatus.NOT_FOUND));
			ApplicantScore newScore = new ApplicantScore();
			newScore.setApplicant(applicant);
			logger.info("New applicant score created for applicant ID: {}", applicantId);
			return newScore;

		} catch (Exception e) {
			logger.error("Error creating new applicant score for applicantId {}: {}", applicantId, e.getMessage(), e);
			throw new CustomException("Error creating new applicant score for applicant id: " + applicantId,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void updateSpecificScore(ApplicantScore applicantScore, String activityName, Integer points) {
		try {
			logger.info("Updating specific score for activity: {} by {} points", activityName, points);
			switch (activityName.toLowerCase()) {
			case HACKATHON_SCORE:
				logger.info("Updating hackathon score");
				applicantScore.setHackathon_score(applicantScore.getHackathon_score() + points);
				break;
			case MENTOR_CONNECT_SCORE:
				logger.info("Updating mentor connect score");
				applicantScore.setMentor_connect_score(applicantScore.getMentor_connect_score() + points);
				break;
			case SKILL_TEST_SCORE:
				logger.info("Updating skill test score");
				applicantScore.setSkill_test_score(applicantScore.getSkill_test_score() + points);
				break;
			case VIDEO_WATCH_SCORE:
				logger.info("Updating video watch score");
				applicantScore.setVideo_watch_score(applicantScore.getVideo_watch_score() + points);
				break;
			case TECHNICAL_TEST_SCORE:
				logger.info("Updating technical test score");
				applicantScore.setTechnical_test_score(applicantScore.getTechnical_test_score() + points);
				break;
			case APTITUDE_TEST_SCORE:
				logger.info("Updating aptitude test score");
				applicantScore.setAptitude_test_score(applicantScore.getAptitude_test_score() + points);
				break;
			default:
				logger.error("Unknown activity type: {}", activityName);
				throw new CustomException("Unknown activity type: " + activityName, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error("Error updating specific score: {}", e.getMessage(), e);
			throw new CustomException("Error updating specific score for activity: " + activityName,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void updateLevelAndBadge(ApplicantScore applicantScore) {
		try {
			logger.info("Updating level and Badge for applicant score");
			int totalScore = applicantScore.getTotal_score();

			// LEVEL LOGIC
			if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(LEVEL_ALLOTMENT_SCORE,
					EXPERT_LEVEL)) {
				logger.info("Applicant has reached EXPERT_LEVEL");
				applicantScore.setLevel(EXPERT_LEVEL);
			} else if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(LEVEL_ALLOTMENT_SCORE,
					ADVANCED_LEVEL)) {
				logger.info("Applicant has reached ADVANCED_LEVEL");
				applicantScore.setLevel(ADVANCED_LEVEL);
			} else if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(LEVEL_ALLOTMENT_SCORE,
					INTERMEDIATE_LEVEL)) {
				logger.info("Applicant has reached INTERMEDIATE_LEVEL");
				applicantScore.setLevel(INTERMEDIATE_LEVEL);
			} else {
				logger.info("Applicant is at BEGINNER level");
				applicantScore.setLevel("BEGINNER");
			}

			// Badge LOGIC
			if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(BADGE_ALLOTMENT_SCORE, GOLD)) {
				logger.info("Applicant has been awarded GOLD Badge");
				applicantScore.setBadge(GOLD);
			} else if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(BADGE_ALLOTMENT_SCORE,
					SILVER)) {
				logger.info("Applicant has been awarded SILVER Badge");
				applicantScore.setBadge(SILVER);
			} else if (totalScore >= scoringRulesRepository.findPointsByActivityNameAndDetail(BADGE_ALLOTMENT_SCORE,
					BRONZE)) {
				logger.info("Applicant has been awarded BRONZE Badge");
				applicantScore.setBadge(BRONZE);
			} else {
				logger.info("Applicant is at NEWCOMER Badge");
				applicantScore.setBadge("NEWCOMER");
			}

		} catch (Exception e) {
			logger.error("Error updating level and Badge: {}", e.getMessage(), e);
			throw new CustomException("Error updating level and Badge for applicant score",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ApplicantScoringDetailsResponseDTO getApplicantScoreDetails(long applicantId) {

		ApplicantProfile applicant = applicantProfileRepository.findByApplicantId(applicantId);

		if (applicant == null) {
			throw new CustomException("Applicant not found with id: " + applicantId, HttpStatus.NOT_FOUND);
		}

		ApplicantScore applicantScore = applicantScoreRepository.findByApplicantId(applicantId)
				.orElseThrow(() -> new CustomException("Applicant score not found for applicant id: " + applicantId,
						HttpStatus.NOT_FOUND));

		List<BadgeScoreDTO> badgeScores = scoringRulesRepository.findAllBadgeScores();
		if (badgeScores == null) {
			throw new CustomException("Badges not found", HttpStatus.NOT_FOUND);
		}

		try {
			return new ApplicantScoringDetailsResponseDTO(applicantScore.getTotal_score(), applicantScore.getLevel(),
					applicantScore.getBadge(), badgeScores);

		} catch (Exception e) {
			logger.error("Error retrieving score details for applicantId {}", applicantId, e);
			throw new CustomException("Error retrieving score details for applicant id: " + applicantId,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
