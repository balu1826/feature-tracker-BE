package com.talentstream.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "mentor_connect", schema = "public")
public class MentorConnect {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long meetingId;

	@Column(name = "mentor_name", nullable = false)
	private String mentorName;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description")
	private String description;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "duration", nullable = false)
	private Long durationMinutes;

	@Column(name = "meet_link")
	private String meetLink;

	@Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime createdAt;

	@Column(name = "banner_image_url")
	private String bannerImageUrl;

	@Column(name = "mentor_profile_url")
	private String mentorProfileUrl;

	@Column(name = "registrations_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private Integer registrationsCount = 0;

	public Long getMeetingId() {
		return meetingId;
	}
 
	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public String getMentorName() {
		return mentorName;
	}

	public void setMentorName(String mentorName) {
		this.mentorName = mentorName;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public Long getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Long durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public String getMeetLink() {
		return meetLink;
	}

	public void setMeetLink(String meetLink) {
		this.meetLink = meetLink;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getBannerImageUrl() {
		return bannerImageUrl;
	}

	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}

	public String getMentorProfileUrl() {
		return mentorProfileUrl;
	}

	public void setMentorProfileUrl(String mentorProfileUrl) {
		this.mentorProfileUrl = mentorProfileUrl;
	}

	public Integer getRegistrationsCount() {
		return registrationsCount;
	}

	public void setRegistrationsCount(Integer registrationsCount) {
		this.registrationsCount = registrationsCount;
	}

	@Transient
	public String getStatus() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
		LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

		if (now.isBefore(startDateTime))
			return "Upcoming";
		else if (now.isAfter(endDateTime))
			return "Expired";
		else
			return "Active";
	}
}
