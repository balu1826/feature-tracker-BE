package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Names are stored directly (entered by student)
    @Column(nullable = false)
    private String collegeName;

    @Column(nullable = false)
    private String mentorName;

    @Column(nullable = false)
    private String sessionTitle;

    private int ratingOverall;
    private int ratingDelivery;
    private int ratingContent;
    private int ratingClarity;

    @Column(length = 1000)
    private String comments;

    private String ipHash;
    private LocalDateTime submittedAt = LocalDateTime.now();

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getSessionTitle() { return sessionTitle; }
    public void setSessionTitle(String sessionTitle) { this.sessionTitle = sessionTitle; }

    public int getRatingOverall() { return ratingOverall; }
    public void setRatingOverall(int ratingOverall) { this.ratingOverall = ratingOverall; }

    public int getRatingDelivery() { return ratingDelivery; }
    public void setRatingDelivery(int ratingDelivery) { this.ratingDelivery = ratingDelivery; }

    public int getRatingContent() { return ratingContent; }
    public void setRatingContent(int ratingContent) { this.ratingContent = ratingContent; }

    public int getRatingClarity() { return ratingClarity; }
    public void setRatingClarity(int ratingClarity) { this.ratingClarity = ratingClarity; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getIpHash() { return ipHash; }
    public void setIpHash(String ipHash) { this.ipHash = ipHash; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
