package com.talentstream.dto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class ApplicantSkillBadgeRequestDTO {

    @NotNull(message = "Applicant ID cannot be null")
    private Long applicantId;

    @NotBlank(message = "Skill badge name cannot be empty")
    private String skillBadgeName;

    @NotBlank(message = "Status cannot be empty")
    @Pattern(
        regexp = "PASSED|FAILED",
        message = "Status must be either PASSED or FAILED (in uppercase)"
    )
    private String status;

    // Getters & Setters
    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getSkillBadgeName() {
        return skillBadgeName;
    }

    public void setSkillBadgeName(String skillBadgeName) {
        this.skillBadgeName = skillBadgeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
