package com.talentstream.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "applicant_score")
public class ApplicantScore {

	@Id
	@GeneratedValue
    private UUID id;

	@OneToOne
	@JoinColumn(name = "applicant_id")
	private Applicant applicant;

    private int hackathon_score = 0;
	private int mentor_connect_score = 0;
    private int skill_test_score = 0;
    private int video_watch_score = 0;
    private int technical_test_score = 0;
    private int aptitude_test_score = 0;
    private int total_score = 0;


    private String level;
    private String badge;

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public int getHackathon_score() {
		return hackathon_score;
	}

	public void setHackathon_score(int hackathon_score) {
		this.hackathon_score = hackathon_score;
	}

	public int getMentor_connect_score() {
		return mentor_connect_score;
	}

	public void setMentor_connect_score(int mentor_connect_score) {
		this.mentor_connect_score = mentor_connect_score;
	}	

	public int getSkill_test_score() {
		return skill_test_score;
	}

	public void setSkill_test_score(int skill_test_score) {
		this.skill_test_score = skill_test_score;
	}

	public int getVideo_watch_score() {
		return video_watch_score;
	}

	public void setVideo_watch_score(int video_watch_score) {
		this.video_watch_score = video_watch_score;
	}

	public int getTechnical_test_score() {
		return technical_test_score;
	}

	public void setTechnical_test_score(int technical_test_score) {
		this.technical_test_score = technical_test_score;
	}

	public int getAptitude_test_score() {
		return aptitude_test_score;
	}

	public void setAptitude_test_score(int aptitude_test_score) {
		this.aptitude_test_score = aptitude_test_score;
	}

	public int getTotal_Score() {
		return total_score;
	}

	public void setTotal_Score(int total_Score) {
		this.total_score = total_Score;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getTotal_score() {
		return total_score;
	}

	public void setTotal_score(int total_score) {
		this.total_score = total_score;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	private LocalDateTime lastUpdated = LocalDateTime.now();
}
