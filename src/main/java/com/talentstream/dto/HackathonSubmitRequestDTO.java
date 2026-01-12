package com.talentstream.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class HackathonSubmitRequestDTO {

    @NotNull
    private Long registrationId;
    
    @NotNull
    private Long userId;

    
	@NotBlank(message = "Project title cannot be empty")
    @Size(min = 5, max = 255, message = "Project title must be between 5 and 255 characters")
    private String projectTitle;

    @NotBlank(message = "Project summary cannot be empty")
    @Size(min = 5, max = 4000, message = "Project summary must be between 5 and 4000 characters")
    private String projectSummary;

    @NotBlank(message = "Technologies used cannot be empty")
    @Size(min = 1, max = 2000, message = "Technologies used must be between 1 and 2000 characters")
    private String technologiesUsed;

    @NotBlank(message = "GitHub repository link is required")
    private String githubLink;

    private String demoLink;

    public Long getRegistrationId() {
        return registrationId;
    }
    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }
    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectSummary() {
        return projectSummary;
    }
    public void setProjectSummary(String projectSummary) {
        this.projectSummary = projectSummary;
    }

    public String getTechnologiesUsed() {
        return technologiesUsed;
    }
    public void setTechnologiesUsed(String technologiesUsed) {
        this.technologiesUsed = technologiesUsed;
    }

    public String getGithubLink() {
        return githubLink;
    }
    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }

    public String getDemoLink() {
        return demoLink;
    }
    public void setDemoLink(String demoLink) {
        this.demoLink = demoLink;
    }
    
    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
