package com.talentstream.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.talentstream.entity.MentorConnect;

public class MentorConnectDTO {

    private Long meetingId;
    private String mentorName;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private Long durationMinutes;
    private String meetLink;
    private LocalDateTime createdAt;
    private String bannerImageUrl;
    private String mentorProfileUrl;
    private String status;

    // ✅ Constructor using entity
    public MentorConnectDTO(MentorConnect m) {
        this.setMeetingId(m.getMeetingId());
        this.mentorName = m.getMentorName();
        this.title = m.getTitle();
        this.description = m.getDescription();
        this.date = m.getDate();
        this.startTime = m.getStartTime();
        this.durationMinutes = m.getDurationMinutes();
        this.meetLink = m.getMeetLink();
        this.createdAt = m.getCreatedAt();
        this.bannerImageUrl = m.getBannerImageUrl();
        this.mentorProfileUrl = m.getMentorProfileUrl();
        this.status = m.getStatus();
    }


    public String getMentorName() { return mentorName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public Long getDurationMinutes() { return durationMinutes; }
    public String getMeetLink() { return meetLink; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public String getMentorProfileUrl() { return mentorProfileUrl; }
    public String getStatus() { return status; }


	public Long getMeetingId() {
		return meetingId;
	}


	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}
}
