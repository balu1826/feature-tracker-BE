package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentstream.dto.PersonalDetailsDTO;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.BasicDetails;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;

@Service
public class ApplicantPersonalService {
    private final ApplicantProfileRepository profileRepo;

    public ApplicantPersonalService(ApplicantProfileRepository profileRepo) {
        this.profileRepo = profileRepo;
    }

    @Transactional(readOnly = true)
    public PersonalDetailsDTO getApplicantPersonalDetails(long applicantId) {
        ApplicantProfile profile = profileRepo.findByApplicantId(applicantId);
        if (profile == null || profile.getBasicDetails() == null) {
            throw new CustomException("Personal details not found for applicant", HttpStatus.NOT_FOUND);
        }
        BasicDetails bd = profile.getBasicDetails();

        PersonalDetailsDTO dto = new PersonalDetailsDTO();
        dto.setName(bd.getFirstName());
        dto.setGender(bd.getGender() == null ? "" : bd.getGender());
        dto.setEmail(bd.getEmail());
        dto.setPhone(bd.getAlternatePhoneNumber());
        dto.setDateOfBirth(bd.getDateOfBirth());
        dto.setPincode(bd.getPincode());
        dto.setAddress(bd.getAddress());
        dto.setKnownLanguages(bd.getKnownLanguages());
        return dto;
    }

    @Transactional
    public String updateApplicantPersonalDetails(long applicantId, PersonalDetailsDTO req) {

        ApplicantProfile profile = profileRepo.findByApplicantId(applicantId);
        if (profile == null) {
            throw new CustomException("Profile not found", HttpStatus.NOT_FOUND);
        }

        BasicDetails bd = profile.getBasicDetails();
        if (bd == null) {
            throw new CustomException("Basic details not initialized", HttpStatus.BAD_REQUEST);
        }

        bd.setFirstName(req.getName());
        bd.setGender(req.getGender());
        bd.setAlternatePhoneNumber(req.getPhone());
        bd.setDateOfBirth(req.getDateOfBirth());
        bd.setPincode(req.getPincode());
        bd.setAddress(req.getAddress());

        if (bd.getKnownLanguages() == null) {
            bd.setKnownLanguages(new ArrayList<>());
        } else {
            bd.getKnownLanguages().clear();
        }

        if (req.getKnownLanguages() != null) {
            bd.getKnownLanguages().addAll(req.getKnownLanguages());
        }

        profile.setUpdatedAt(LocalDateTime.now());

        return "Personal details updated successfully";
    }

}
