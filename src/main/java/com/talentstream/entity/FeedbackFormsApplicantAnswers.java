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
@Table(name = "feedbackforms_applicant_answers")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class FeedbackFormsApplicantAnswers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "form_id", nullable = false)
	private FeedbackForms form;

	@ManyToOne(optional = false)
	@JoinColumn(name = "applicant_id", nullable = false)
	private Applicant applicant;

	// Stores the answers JSON array (as in the example) using jsonb
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", nullable = false)
	private String answers;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public FeedbackFormsApplicantAnswers() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FeedbackForms getForm() {
		return form;
	}

	public void setForm(FeedbackForms form) {
		this.form = form;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public String getAnswers() {
		return answers;
	}

	public void setAnswers(String answers) {
		this.answers = answers;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
