package com.talentstream.service;

import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.PASS;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.SKILL_TEST_SCORE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.talentstream.dto.ApplicantSkillBadgeDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantSkillBadge;
import com.talentstream.entity.ApplicantSkills;
import com.talentstream.entity.SkillBadge;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.ApplicantSkillBadgeRepository;
import com.talentstream.repository.SkillBadgeRepository;

@Service
public class SkillBadgeService {

	private static final Logger logger = LoggerFactory.getLogger(SkillBadgeService.class);

	@Autowired
	private SkillBadgeRepository skillBadgeRepository;

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private ApplicantSkillBadgeRepository applicantSkillBadgeRepository;
	@Autowired
	private ApplicantProfileRepository applicantProfileRepository;

	@Autowired
	private ApplicantScoreService applicantScoreService;

	@Transactional
	public ResponseEntity<String> saveApplicantSkillBadge(Long applicantId, String skillBadgeName, String status) {
		logger.debug("saveApplicantSkillBadge - entry applicantId={}, skillBadgeName={}, status={}", applicantId, skillBadgeName, status);

		// Fetch applicant
		Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(
			() -> new CustomException("Applicant not found with ID: " + applicantId, HttpStatus.NOT_FOUND));

		// Fetch skill badge
		SkillBadge skillBadge = skillBadgeRepository.findByName(skillBadgeName);
		if (skillBadge == null) {
		    logger.warn("saveApplicantSkillBadge - skill badge not found: {} for applicantId={}", skillBadgeName, applicantId);
		    throw new CustomException(("SkillBadge with name '" + skillBadgeName + "' not found."),
			    HttpStatus.NOT_FOUND);
		}

		// Check existing record
		ApplicantSkillBadge existingRecord = applicantSkillBadgeRepository.findByApplicantIdAndSkillBadgeId(applicantId,
				skillBadge.getId());

		// If already passed, user cannot take any action
		if (existingRecord != null && "PASSED".equalsIgnoreCase(existingRecord.getStatus())) {
		    logger.warn("saveApplicantSkillBadge - applicantId={} attempted to retake already PASSED badge={}", applicantId, skillBadgeName);
		    throw new CustomException(("Cannot retake skill badge '" + skillBadgeName
			    + "'. Applicant has already PASSED this skill badge."), HttpStatus.CONFLICT);
		}

		// Update score if passed
		if ("PASSED".equalsIgnoreCase(status)) {
			logger.info("saveApplicantSkillBadge - applicantId={} PASSED badge={}, updating score", applicantId, skillBadgeName);
			applicantScoreService.updateApplicantScore(applicantId, SKILL_TEST_SCORE, PASS);
		}
		try {

			// Update or create the applicant skill badge record
			if (existingRecord != null && "FAILED".equalsIgnoreCase(existingRecord.getStatus())) {
				existingRecord.setStatus(status);
				existingRecord.setTestTaken(LocalDateTime.now());
				applicantSkillBadgeRepository.save(existingRecord);
				logger.info("saveApplicantSkillBadge - updated existing ApplicantSkillBadge for applicantId={}, badge={}", applicantId, skillBadgeName);
			} else {
				ApplicantSkillBadge applicantSkillBadge = new ApplicantSkillBadge();
				applicantSkillBadge.setApplicant(applicant);
				applicantSkillBadge.setSkillBadge(skillBadge);
				applicantSkillBadge.setStatus(status);
				applicantSkillBadge.setTestTaken(LocalDateTime.now());
				applicantSkillBadgeRepository.save(applicantSkillBadge);
				logger.info("saveApplicantSkillBadge - created ApplicantSkillBadge for applicantId={}, badge={}", applicantId, skillBadgeName);
			}

			logger.debug("saveApplicantSkillBadge - completed for applicantId={}, badge={}", applicantId, skillBadgeName);
			return ResponseEntity.ok("ApplicantSkillBadge saved successfully");
		} catch (Exception ex) {
			logger.error("saveApplicantSkillBadge - unexpected error for applicantId={}, badge={}: {}", applicantId, skillBadgeName, ex.getMessage(), ex);
			throw new CustomException("An unexpected error occurred: " + ex.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<ApplicantSkillBadgeDTO> getApplicantSkillBadges(Long id, String status) {
		logger.debug("getApplicantSkillBadges - entry applicantId={}, status={}", id, status);

		// Find applicant skills based on applicant ID
		List<ApplicantSkillBadge> applicantSkills = applicantSkillBadgeRepository.findByApplicantIdAndFlagAdded(id);
		logger.debug("getApplicantSkillBadges - found {} applicant skill badges for applicantId={}", applicantSkills == null ? 0 : applicantSkills.size(), id);

		// Find applicant profile, and handle any potential exception
		ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(id);
		if (applicantProfile == null) {
			logger.warn("getApplicantSkillBadges - applicant profile not found for applicantId={}", id);
			throw new CustomException("Applicant profile not found for applicant id: " + id, HttpStatus.NOT_FOUND);
		}

		// Get all required skills from the applicant profile
		Set<ApplicantSkills> allSkills = new TreeSet<>(
				Comparator.comparing(ApplicantSkills::getSkillName, String.CASE_INSENSITIVE_ORDER)
		);
		allSkills.addAll(applicantProfile.getSkillsRequired());
		logger.debug("getApplicantSkillBadges - applicant has {} required skills", allSkills.size());

		// Remove skills from allSkills that match with applicant skill badges
		for (ApplicantSkillBadge applicantSkill : applicantSkills) {
			allSkills.removeIf(skill -> skill.getSkillName().equalsIgnoreCase(applicantSkill.getSkillBadge().getName()));
		}
		logger.debug("getApplicantSkillBadges - remaining required skills after filtering: {}", allSkills.size());

		// Create and populate DTO
		ApplicantSkillBadgeDTO applicantSkillBadgeDTO = new ApplicantSkillBadgeDTO();

		// 1. PASSED badges - already in correct order from query
		List<ApplicantSkillBadge> passedList = applicantSkills.stream()
				.filter(b -> "PASSED".equalsIgnoreCase(b.getStatus())).collect(Collectors.toList());
		logger.debug("getApplicantSkillBadges - passed badges count={}", passedList.size());

		if ("PASSED".equalsIgnoreCase(status)) {
			applicantSkillBadgeDTO.setApplicantSkillBadges(passedList);
			logger.debug("getApplicantSkillBadges - returning PASSED list only for applicantId={}", id);
			return ResponseEntity.ok(applicantSkillBadgeDTO);
		}

		// 1. FAILED badges - Set them to ascending order oldest should come first
		List<ApplicantSkillBadge> failedList = applicantSkills.stream()
				.filter(b -> "FAILED".equalsIgnoreCase(b.getStatus()))
				.sorted(Comparator.comparing(ApplicantSkillBadge::getTestTaken)) // ASCENDING (oldest first)
				.collect(Collectors.toList());
		logger.debug("getApplicantSkillBadges - failed badges count={}", failedList.size());

		// Combine both lists
		List<ApplicantSkillBadge> finalSkillBadges = new ArrayList<>();
		finalSkillBadges.addAll(passedList);
		finalSkillBadges.addAll(failedList);

		// Set sorted list to DTO
		applicantSkillBadgeDTO.setApplicantSkillBadges(finalSkillBadges);
		applicantSkillBadgeDTO.setSkillsRequired(allSkills); // Set the required skills
		logger.debug("getApplicantSkillBadges - returning total {} badges and {} required skills for applicantId={}", finalSkillBadges.size(), allSkills.size(), id);

		// Return the response entity with the populated DTO
		return ResponseEntity.ok(applicantSkillBadgeDTO);
	}

}
