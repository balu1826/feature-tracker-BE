package com.talentstream.entity;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hackathons")
public class Hackathon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long recruiterId;
	
	@Column(length = 100)
	private String title;
	
	@Column(length = 1000)
	private String description;
	
	private String bannerUrl;
	private LocalDate startAt;
	private LocalDate endAt;
	private LocalDateTime createdAt;
	
	private String company;
	private Long winner;
	
	@Column(length = 2000)
	private String instructions;
	
	private Long registrationCount;
	private Long submissionCount;
	
	private String eligibility;
	private String allowedTechnologies;
	 @Enumerated(EnumType.STRING)  
	    private HackathonStatus status;

	public Hackathon() {
	}
	@PrePersist
    @PreUpdate
    public void updateStatus() {
        LocalDate now = LocalDate.now();
        if (startAt != null && endAt != null) {
            if (now.isBefore(startAt)) {
                this.status = HackathonStatus.UPCOMING;
            } else if ((now.isEqual(startAt) || now.isAfter(startAt)) && now.isBefore(endAt)) {
                this.status = HackathonStatus.ACTIVE;
            } else if (now.isAfter(endAt)) {
                this.status = HackathonStatus.COMPLETED;
            }
        }
        
        if (this.registrationCount == null) this.registrationCount = 0L;
		if (this.submissionCount == null) this.submissionCount = 0L;
    }
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRecruiterId() {
		return recruiterId;
	}
	public void setRecruiterId(Long recruiterId) {
		this.recruiterId = recruiterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public LocalDate getStartAt() {
		return startAt;
	}

	public void setStartAt(LocalDate startAt) {
		this.startAt = startAt;
	}

	public LocalDate getEndAt() {
		return endAt;
	}

	public void setEndAt(LocalDate endAt) {
		this.endAt = endAt;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getEligibility() {
		return eligibility;
	}

	public void setEligibility(String eligibility) {
		this.eligibility = eligibility;
	}

	public String getAllowedTechnologies() {
		return allowedTechnologies;
	}

	public void setAllowedTechnologies(String allowedTechnologies) {
		this.allowedTechnologies = allowedTechnologies;
	}


	public HackathonStatus getStatus() {
		return status;
	}

	public void setStatus(HackathonStatus status) {
		this.status = status;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	public Long getWinner() {
		return winner;
	}
	public void setWinner(Long winner) {
		this.winner = winner;
	}
	
	public Long getRegistrationCount() {
		return registrationCount;
	}

	public void setRegistrationCount(Long registrationCount) {
		this.registrationCount = registrationCount;
	}

	public Long getSubmissionCount() {
		return submissionCount;
	}

	public void setSubmissionCount(Long submissionCount) {
		this.submissionCount = submissionCount;
	}
}
