package com.talentstream.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@Entity
@Table(name = "feedback_forms")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class FeedbackForms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "recruiter_id", nullable = false)
	private JobRecruiter recruiter;

	@Column(name = "mentor_name", nullable = false)
	private String mentorName;

	@Column(name = "college_name", nullable = false)
	private String collegeName;

	@Column(nullable = false)
	private String title;

	@Column
	private String description;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", nullable = false)
	private String questions;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JobRecruiter getRecruiter() {
		return recruiter;
	}

	public void setRecruiter(JobRecruiter recruiter) {
		this.recruiter = recruiter;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// (No manual JSON conversion needed; field is mapped as jsonb)
}
