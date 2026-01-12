package com.talentstream.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantTest;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.ApplicantTestRepository;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.APTITUDE_TEST_SCORE;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.TECHNICAL_TEST_SCORE;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.PASS;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantTestService {

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private ApplicantTestRepository applicantTestRepository;

	@Autowired
	private ApplicantScoreService applicantScoreService;

	public ApplicantTest saveTest(ApplicantTest test, Long applicantId) {
		// Fetch the applicant
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new RuntimeException("Applicant not found"));

		// Set the applicant to the test
		test.setApplicant(applicant);
		System.out.println("Test name:"+test.getTestName()+" "+test.getTestStatus());

		// Check if the test with the same name already exists for this applicant
		Optional<ApplicantTest> existingTest = applicantTestRepository.findByApplicantIdAndTestName(applicantId,
				test.getTestName());
		
		if ("P".equals(test.getTestStatus())) {
			addScoreToApplicantForTestPass(applicantId, test.getTestName(),test.getTestStatus());			
		}

		if (existingTest.isPresent()) {
			// Update the existing test
			ApplicantTest testToUpdate = existingTest.get();
			testToUpdate.setTestScore(test.getTestScore());
			testToUpdate.setTestStatus(test.getTestStatus());
			testToUpdate.setTestDateTime(LocalDateTime.now());
			return applicantTestRepository.save(testToUpdate);
		} else {
			return applicantTestRepository.save(test);
		}
	}

	public List<ApplicantTest> getTestsByApplicantId(Long applicantId) {
		return applicantTestRepository.findByApplicantId(applicantId);
	}

	public Optional<ApplicantTest> getTestById(Long id) {
		return applicantTestRepository.findById(id);
	}

	@Async
	public void addScoreToApplicantForTestPass(Long applicantId, String testName, String testStatus) {
		switch (testName) {
			case "General Aptitude Test":
				applicantScoreService.updateApplicantScore(applicantId,APTITUDE_TEST_SCORE, PASS);
				break;
			case "Technical Test":
				applicantScoreService.updateApplicantScore(applicantId, TECHNICAL_TEST_SCORE, PASS);
				break;
			default:
				throw new IllegalArgumentException("Unknown test name: " + testName);
		}
	}
}
