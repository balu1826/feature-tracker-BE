package com.talentstream.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talentstream.dto.EducationDetailsDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantEducation;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantEducationRepository;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;

@Service
public class ApplicantEducationService {

    private final ApplicantRepository applicantRepository;
    private final ApplicantEducationRepository educationRepository;
    private final ApplicantProfileRepository applicantProfileRepository;



    public ApplicantEducationService(ApplicantRepository applicantRepository,
                                     ApplicantEducationRepository educationRepository, ApplicantProfileRepository applicantProfileRepository) {
        this.applicantRepository = applicantRepository;
        this.educationRepository = educationRepository;
		this.applicantProfileRepository = applicantProfileRepository;
    }
    
    @Transactional(readOnly = true)
    public EducationDetailsDTO getApplicantEducationDetails(Long applicantId) {
    	applicantRepository.findById(applicantId)
      .orElseThrow(() -> new CustomException("Applicant not found: " + applicantId, HttpStatus.NOT_FOUND));
    	
        ApplicantEducation education = educationRepository.findByApplicantId(applicantId);
        try {
            EducationDetailsDTO dto = new EducationDetailsDTO();
            if (education == null) {
                return dto;
            }
            // ---- Graduation ----
            EducationDetailsDTO.GraduationDTO graduation = new EducationDetailsDTO.GraduationDTO();
            graduation.setDegree(n(education.getGradDegree()));
            graduation.setUniversity(n(education.getGradUniversity()));
            graduation.setSpecialization(n(education.getGradSpecialization()));
            graduation.setCourseType(n(education.getGradCourseType()));
            graduation.setStartYear(education.getGradStartYear());
            graduation.setEndYear(education.getGradEndYear());
            graduation.setMarksPercent(education.getGradMarksPercent());
            dto.setGraduation(graduation);

            // ---- Class XII ----
            EducationDetailsDTO.ClassXiiDTO classXii = new EducationDetailsDTO.ClassXiiDTO();
            classXii.setBoard(n(education.getXiiBoard()));
            classXii.setPassingYear(education.getXiiPassingYear());
            classXii.setMarksPercent(education.getXiiMarksPercent());
            dto.setClassXii(classXii);

            // ---- Class X ----
            EducationDetailsDTO.ClassXDTO classX = new EducationDetailsDTO.ClassXDTO();
            classX.setBoard(n(education.getxBoard()));
            classX.setPassingYear(education.getxPassingYear());
            classX.setMarksPercent(education.getxMarksPercent());
            dto.setClassX(classX);

            return dto;

        } catch (Exception e) {
            throw new CustomException(
                    "Failed to fetch education details: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Transactional
    public void updateApplicantEducationDetails(Long applicantId, EducationDetailsDTO dto) {

        if (dto == null) {
            throw new CustomException("Education details are required", HttpStatus.BAD_REQUEST);
        }

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() ->
                        new CustomException("Applicant not found: " + applicantId, HttpStatus.NOT_FOUND));
        
        ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);
    	if(applicantProfile== null) {
    		  throw new CustomException("Applicant profile not found", HttpStatus.NOT_FOUND);
    	}

        ApplicantEducation education = educationRepository.findByApplicantId(applicantId);
        if (education == null) {
            education = new ApplicantEducation();
            education.setApplicant(applicant);
        }

        try {
            // ------------------------ Graduation ------------------------
            EducationDetailsDTO.GraduationDTO graduation = dto.getGraduation();
            if (graduation != null) {
                education.setGradDegree(trim(graduation.getDegree()));
                education.setGradUniversity(trim(graduation.getUniversity()));
                education.setGradSpecialization(trim(graduation.getSpecialization()));
                education.setGradCourseType(trim(graduation.getCourseType()));
                education.setGradStartYear(graduation.getStartYear());
                education.setGradEndYear(graduation.getEndYear());
                education.setGradMarksPercent(graduation.getMarksPercent());
                applicantProfile.setQualification(n(education.getGradDegree()));
        		applicantProfile.setSpecialization(n(education.getGradSpecialization()));
        		applicantProfile.setUpdatedAt(LocalDateTime.now());
            }

            // ------------------------ Class XII ------------------------
            EducationDetailsDTO.ClassXiiDTO classXii = dto.getClassXii();
            if (classXii != null) {
                education.setXiiBoard(trim(classXii.getBoard()));
                education.setXiiPassingYear(classXii.getPassingYear());
                education.setXiiMarksPercent(classXii.getMarksPercent());
            }

            // ------------------------ Class X ------------------------
            EducationDetailsDTO.ClassXDTO classX = dto.getClassX();
            if (classX != null) {
                education.setxBoard(trim(classX.getBoard()));
                education.setxPassingYear(classX.getPassingYear());
                education.setxMarksPercent(classX.getMarksPercent());
            }
            educationRepository.save(education);

        } catch (Exception ex) {
            throw new CustomException(
                    "Failed to update education details: " + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
    
    private static String n(String s) { return s == null ? "" : s; }

}
