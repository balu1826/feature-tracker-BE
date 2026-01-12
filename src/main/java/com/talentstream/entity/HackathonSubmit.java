package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "submissions",
    indexes = {
        @Index(columnList = "hackathonId"),
        @Index(columnList = "registrationId")
    }
)
public class HackathonSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long hackathonId;
    private Long registrationId;
    private Long userId;

   
	private String projectTitle;

    @Column(length = 4000)
    private String projectSummary;

    @Column(length = 2000)
    private String technologiesUsed;

    private String githubLink;
    private String demoLink;

    private LocalDateTime submissionDate;

    public HackathonSubmit() {}

    @PrePersist
    protected void onCreate() {
    	if (this.submissionDate == null) {
            this.submissionDate = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getHackathonId() {
        return hackathonId;
    }
    public void setHackathonId(Long hackathonId) {
        this.hackathonId = hackathonId;
    }

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

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
