package com.talentstream.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;

@Service
public class ApplicantSummaryService {

    private final ApplicantRepository applicantRepository;
    private final ApplicantProfileRepository applicantProfileRepository;

    public ApplicantSummaryService(ApplicantRepository applicantRepository,
                                   ApplicantProfileRepository applicantProfileRepository) {
        this.applicantRepository = applicantRepository;
        this.applicantProfileRepository = applicantProfileRepository;
    }

    @Transactional(readOnly = true)
    public String getApplicantSummary(long applicantId) {
        try {
            Applicant applicant = applicantRepository.findById(applicantId)
                    .orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

            return applicant.getSummary();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException("Failed to fetch summary", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateApplicantSummary(long applicantId, String summaryText) {
        try {
            Applicant applicant = applicantRepository.findById(applicantId)
                    .orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

            ApplicantProfile profile = applicantProfileRepository.findByApplicantId(applicantId);
            if (profile == null) {
                throw new CustomException("Applicant profile not found", HttpStatus.NOT_FOUND);
            }

            String cleanedSummary = (summaryText == null) ? "" : summaryText.trim();

            if (cleanedSummary.length() < 30 || cleanedSummary.length() > 2000) {
                throw new CustomException("Summary must be between 30 and 2000 characters", HttpStatus.BAD_REQUEST);
            }

            applicant.setSummary(cleanedSummary);
            profile.setUpdatedAt(LocalDateTime.now());

            applicantRepository.save(applicant);

        } catch (CustomException e) {
            throw e; 
        } catch (Exception e) {
            throw new CustomException("Failed to update summary", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
