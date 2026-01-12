package com.talentstream.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackFormsResponseDTO{

    private Long formId;
    private Long recruiterId;
    private String mentorName;
    private String collegeName;
    private String formName;
    private String description;
    private Boolean isActive;
    private String questions; // JSON string

    public FeedbackFormsResponseDTO() {
        super();
    }

    // Constructor used for JPA constructor projection
    public FeedbackFormsResponseDTO(String mentorName, String collegeName, String formName, Boolean isActive) {
        this.mentorName = mentorName;
        this.collegeName = collegeName;
        this.formName = formName;
        this.isActive = isActive;
    }
    // Getters and Setters

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

   

}
