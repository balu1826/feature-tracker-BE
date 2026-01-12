package com.talentstream.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ApplicantSummaryDTO {

    @NotBlank(message = "Summary is required")
    @Size(min = 30, max = 2000, message = "Summary must be between 30 and 2000 characters")
    private String summary;

    public ApplicantSummaryDTO() { }
    public ApplicantSummaryDTO(String summary) { this.summary = summary; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
