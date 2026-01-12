package com.talentstream.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonType;

@Entity
@TypeDef(name = "jsonb", typeClass = JsonType.class)
public class NotificationMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "created_time")
	private LocalDateTime createdTime;

	private String message;

	private String feature;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<Long> applicantId;
	
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<Long> seenApplicantId;

	private Long featureId;

	public NotificationMessage(Long id, LocalDateTime createdTime, String message, String feature,
			List<Long> applicantId, List<Long> seenApplicantId, Long featureId) {
		super();
		this.id = id;
		this.createdTime = createdTime;
		this.message = message;
		this.feature = feature;
		this.applicantId = applicantId;
		this.seenApplicantId = seenApplicantId;
		this.featureId = featureId;
	}

	public NotificationMessage() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public List<Long> getApplicantId() {
		return applicantId;
	}

	public void setApplicantId(List<Long> applicantId) {
		this.applicantId = applicantId;
	}

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}

	public List<Long> getSeenApplicantId() {
		return seenApplicantId;
	}

	public void setSeenApplicantId(List<Long> seenApplicantId) {
		this.seenApplicantId = seenApplicantId;
	}

	
}
