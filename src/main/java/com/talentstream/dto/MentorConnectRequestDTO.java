package com.talentstream.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MentorConnectRequestDTO {

	@NotNull(message = "Banner image URL is required")
	@Size(min = 10, max = 255, message = "Banner image URL must be between 10 and 255 characters")
	@Pattern(regexp = "^(https?://).*$", message = "Banner image URL must be a valid URL")
	public String bannerImageUrl;

	@NotNull(message = "Date is required")
	@FutureOrPresent(message = "Date cannot be in the past")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public LocalDate date;

	@Size(min = 10, max = 255, message = "Description must be between 10 and 255 characters")
	public String description;

	@NotNull(message = "Duration is required")
	@Min(value = 15, message = "Duration must be at least 15 minute")
	public Long duration;

	@NotNull(message = "Meeting link is required")
	@Size(min = 10, max = 255, message = "Meeting link must be between 10 and 255 characters")
	@Pattern(regexp = "^(https?://).*$", message = "Meeting link must be a valid URL")
	public String meetLink;

	@NotBlank(message = "Mentor name is required")
	@Size(min = 3, max = 255, message = "Mentor name must be between 3 and 255 characters")
	public String mentorName;

	@Size(min = 10, max = 255, message = "Mentor profile URL must be between 10 and 255 characters")
	@Pattern(regexp = "^(https?://).*$", message = "Mentor profile URL must be a valid URL")
	public String mentorProfileUrl; // NOT required

	@NotNull(message = "Start time is required")
	@JsonFormat(pattern = "HH:mm")
	public LocalTime startTime;

	@NotBlank(message = "Title is required")
	@Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
	public String title;

	public String getBannerImageUrl() {
		return bannerImageUrl;
	}

	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getMeetLink() {
		return meetLink;
	}

	public void setMeetLink(String meetLink) {
		this.meetLink = meetLink;
	}

	public String getMentorName() {
		return mentorName;
	}

	public void setMentorName(String mentorName) {
		this.mentorName = mentorName;
	}

	public String getMentorProfileUrl() {
		return mentorProfileUrl;
	}

	public void setMentorProfileUrl(String mentorProfileUrl) {
		this.mentorProfileUrl = mentorProfileUrl;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
