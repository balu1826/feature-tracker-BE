package com.talentstream.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class FeedbackFormsDto {

    private Long feedbackFormId;

    @NotBlank(message = "mentorName is required")
    @Size(min=3, max = 255, message = "mentorName must be between 3 and 255 characters")
    private String mentorName;

    @NotBlank(message = "collegeName is required")
    @Size(min=3, max = 255, message = "collegeName must be between 3 and 255 characters")
    private String collegeName;

    @NotBlank(message = "formName is required")
    @Size(min = 3, max = 255, message = "formName must be between 3 and 255 characters")
    private String formName;

    @Size(min = 10, max = 1000, message = "description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "isActive must be provided")
    private Boolean isActive = Boolean.FALSE;

    @NotNull(message = "questions are required")
    @Size(min = 1, message = "at least one question is required")
    @Valid
    private List<FeedbackQuestionData> questions;

    public Long getFeedbackFormId() {
        return feedbackFormId;
    }

    public void setFeedbackFormId(Long feedbackFormId) {
        this.feedbackFormId = feedbackFormId;
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

    public List<FeedbackQuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FeedbackQuestionData> questions) {
        this.questions = questions;
    }

   
}