package com.talentstream.dto;
 
import java.util.List;

import java.util.Set;
 
import com.talentstream.entity.ApplicantSkills;

import com.talentstream.entity.ExperienceDetails;

import com.talentstream.entity.BasicDetails;

import javax.validation.constraints.NotBlank;
 
public class AIInterviewPrepBotDTO {
 
    private Long chatId;          // used for memory

    private Long applicantId;     // required to identify owner
 
    @NotBlank(message = "request is required")

    private String request;
 
    private BasicDetails basicDetails;

    private Set<ApplicantSkills> skillsRequired;

    private List<ExperienceDetails> experienceDetails;
 
    private String experience;

    private String qualification;

    private String specialization;

    private Set<String> preferredJobLocations;

    private String roles;
 
 
    public Long getChatId() {

        return chatId;

    }

    public void setChatId(Long chatId) {

        this.chatId = chatId;

    }
 
    public Long getApplicantId() {

        return applicantId;

    }

    public void setApplicantId(Long applicantId) {

        this.applicantId = applicantId;

    }
 
    public String getRequest() {

        return request;

    }

    public void setRequest(String request) {

        this.request = request;

    }
 
    public BasicDetails getBasicDetails() {

        return basicDetails;

    }

    public void setBasicDetails(BasicDetails basicDetails) {

        this.basicDetails = basicDetails;

    }
 
    public Set<ApplicantSkills> getSkillsRequired() {

        return skillsRequired;

    }

    public void setSkillsRequired(Set<ApplicantSkills> skillsRequired) {

        this.skillsRequired = skillsRequired;

    }
 
    public List<ExperienceDetails> getExperienceDetails() {

        return experienceDetails;

    }

    public void setExperienceDetails(List<ExperienceDetails> experienceDetails) {

        this.experienceDetails = experienceDetails;

    }
 
    public String getExperience() {

        return experience;

    }

    public void setExperience(String experience) {

        this.experience = experience;

    }
 
    public String getQualification() {

        return qualification;

    }

    public void setQualification(String qualification) {

        this.qualification = qualification;

    }
 
    public String getSpecialization() {

        return specialization;

    }

    public void setSpecialization(String specialization) {

        this.specialization = specialization;

    }
 
    public Set<String> getPreferredJobLocations() {

        return preferredJobLocations;

    }

    public void setPreferredJobLocations(Set<String> preferredJobLocations) {

        this.preferredJobLocations = preferredJobLocations;

    }
 
    public String getRoles() {

        return roles;

    }

    public void setRoles(String roles) {

        this.roles = roles;

    }

}

 