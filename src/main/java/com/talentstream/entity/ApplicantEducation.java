package com.talentstream.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicant_education")
public class ApplicantEducation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// link to Applicant
	@OneToOne
	@JoinColumn(name = "applicant_id", nullable = false, unique = true)
	private Applicant applicant;

	// ---- Graduation ----
	@Column(name = "grad_degree")
	private String gradDegree;

	@Column(name = "grad_university")
	private String gradUniversity;

	@Column(name = "grad_specialization")
	private String gradSpecialization;

	@Column(name = "grad_course_type")
	private String gradCourseType;

	@Column(name = "grad_start_year")
	private Integer gradStartYear;

	@Column(name = "grad_end_year")
	private Integer gradEndYear;

	@Column(name = "grad_marks_percent")
	private Double gradMarksPercent;
	// ---- Class XII ----
	@Column(name = "xii_board")
	private String xiiBoard;

	@Column(name = "xii_passing_year")
	private Integer xiiPassingYear;

	@Column(name = "xii_marks_percent")
	private Double xiiMarksPercent;

	// ---- Class X ----
	@Column(name = "x_board")
	private String xBoard;

	@Column(name = "x_passing_year")
	private Integer xPassingYear;

	@Column(name = "x_marks_percent")
	private Double xMarksPercent;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

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

	public String getGradDegree() {
		return gradDegree;
	}

	public void setGradDegree(String gradDegree) {
		this.gradDegree = gradDegree;
	}

	public String getGradUniversity() {
		return gradUniversity;
	}

	public void setGradUniversity(String gradUniversity) {
		this.gradUniversity = gradUniversity;
	}

	public String getGradSpecialization() {
		return gradSpecialization;
	}

	public void setGradSpecialization(String gradSpecialization) {
		this.gradSpecialization = gradSpecialization;
	}

	public String getGradCourseType() {
		return gradCourseType;
	}

	public void setGradCourseType(String gradCourseType) {
		this.gradCourseType = gradCourseType;
	}

	public Integer getGradStartYear() {
		return gradStartYear;
	}

	public void setGradStartYear(Integer gradStartYear) {
		this.gradStartYear = gradStartYear;
	}

	public Integer getGradEndYear() {
		return gradEndYear;
	}

	public void setGradEndYear(Integer gradEndYear) {
		this.gradEndYear = gradEndYear;
	}

	public Double getGradMarksPercent() {
		return gradMarksPercent;
	}

	public void setGradMarksPercent(Double gradMarksPercent) {
		this.gradMarksPercent = gradMarksPercent;
	}

	public String getXiiBoard() {
		return xiiBoard;
	}

	public void setXiiBoard(String xiiBoard) {
		this.xiiBoard = xiiBoard;
	}

	public Integer getXiiPassingYear() {
		return xiiPassingYear;
	}

	public void setXiiPassingYear(Integer xiiPassingYear) {
		this.xiiPassingYear = xiiPassingYear;
	}

	public Double getXiiMarksPercent() {
		return xiiMarksPercent;
	}

	public void setXiiMarksPercent(Double xiiMarksPercent) {
		this.xiiMarksPercent = xiiMarksPercent;
	}

	public String getxBoard() {
		return xBoard;
	}

	public void setxBoard(String xBoard) {
		this.xBoard = xBoard;
	}

	public Integer getxPassingYear() {
		return xPassingYear;
	}

	public void setxPassingYear(Integer xPassingYear) {
		this.xPassingYear = xPassingYear;
	}

	public Double getxMarksPercent() {
		return xMarksPercent;
	}

	public void setxMarksPercent(Double xMarksPercent) {
		this.xMarksPercent = xMarksPercent;
	}
}
