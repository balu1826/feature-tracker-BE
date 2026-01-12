package com.talentstream.service;

import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.ApplicantCardViewDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.BasicDetails;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;

@Service
public class ApplicantCardService {

    private final ApplicantRepository applicantRepository;
    private final ApplicantProfileRepository applicantProfileRepository;

    public ApplicantCardService(ApplicantRepository applicantRepository,
                                ApplicantProfileRepository applicantProfileRepository) {
        this.applicantRepository = applicantRepository;
        this.applicantProfileRepository = applicantProfileRepository;
    }
    
    public ApplicantCardViewDTO getApplicantCardView(long applicantId) {

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));
        
        ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);
        if (applicantProfile == null) {
            throw new CustomException("Applicant profile not found", HttpStatus.NOT_FOUND);
        }
        
        BasicDetails basicDetails = applicantProfile.getBasicDetails();
        if (basicDetails == null) {
            throw new CustomException("Basic details not found", HttpStatus.NOT_FOUND);
        }

        ApplicantCardViewDTO cardView = new ApplicantCardViewDTO();
        if (basicDetails != null) {
            cardView.setName(basicDetails.getFirstName());
            cardView.setEmail(basicDetails.getEmail());
            cardView.setMobileNumber(basicDetails.getAlternatePhoneNumber());
            cardView.setPassOutyear(
                    basicDetails.getPassOutYear() != null ? basicDetails.getPassOutYear() : 0
            );
            cardView.setAddress(basicDetails.getAddress());
        }

        cardView.setRole(applicant.getTitle());
        cardView.setLastUpdated(applicantProfile != null ? applicantProfile.getUpdatedAt() : null);

        return cardView;
    }

    

    public String updateApplicantCardView(long applicantId, @Valid ApplicantCardViewDTO req) {

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new CustomException("Applicant not found", HttpStatus.NOT_FOUND));

        ApplicantProfile profile = applicantProfileRepository.findByApplicantId(applicantId);
        if (profile == null) {
            throw new CustomException("Applicant profile not found", HttpStatus.NOT_FOUND);
        }

        BasicDetails bd = profile.getBasicDetails();
        if (bd == null) {
            throw new CustomException("Basic details not initialized", HttpStatus.BAD_REQUEST);
        }

        try {
            bd.setFirstName(req.getName());
            bd.setAlternatePhoneNumber(req.getMobileNumber());
            bd.setPassOutYear(req.getPassOutyear());
            bd.setAddress(req.getAddress());
            applicant.setTitle(req.getRole());
            profile.setUpdatedAt(LocalDateTime.now());
            
            applicantRepository.save(applicant);
            applicantProfileRepository.save(profile);

            return "Successfully updated applicant card view details";

        } catch (Exception e) {
            throw new CustomException(
                    "Failed to update applicant card view: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

   
}
