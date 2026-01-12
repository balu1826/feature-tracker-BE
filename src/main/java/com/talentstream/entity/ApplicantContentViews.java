package com.talentstream.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonType;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonType.class)
public class ApplicantContentViews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Reference to Applicant entity
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_id", nullable = false)
	private Applicant applicant;

	@Type(type = "jsonb")
	@Column(name = "seen_techvibe_ids", columnDefinition = "jsonb")
	private List<Long> seenTechVibeIds;

	@Type(type = "jsonb")
	@Column(name = "seen_techbuzzshort_ids", columnDefinition = "jsonb")
	private List<Long> seenTechBuzzShortIds;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();

	// Constructors
	public ApplicantContentViews() {
	}

	public ApplicantContentViews(Long id, Applicant applicant, List<Long> seenTechVibeIds,
			List<Long> seenTechBuzzShortIds, LocalDateTime updatedAt) {
		this.id = id;
		this.applicant = applicant;
		this.seenTechVibeIds = seenTechVibeIds;
		this.seenTechBuzzShortIds = seenTechBuzzShortIds;
		this.updatedAt = updatedAt;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public List<Long> getSeenTechVibeIds() {
		return seenTechVibeIds;
	}

	public void setSeenTechVibeIds(List<Long> seenTechVibeIds) {
		this.seenTechVibeIds = seenTechVibeIds;
	}

	public List<Long> getSeenTechBuzzShortIds() {
		return seenTechBuzzShortIds;
	}

	public void setSeenTechBuzzShortIds(List<Long> seenTechBuzzShortIds) {
		this.seenTechBuzzShortIds = seenTechBuzzShortIds;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
