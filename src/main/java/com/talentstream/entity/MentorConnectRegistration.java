package com.talentstream.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mentor_connect_registrations")
public class MentorConnectRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mentor_connect_id")
    private Long mentorConnectId;

    @Column(name = "applicant_id")
    private Long applicantId;


    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    public MentorConnectRegistration() {
    }

    public MentorConnectRegistration(Long mentorConnectId, Long applicantId, LocalDateTime registeredAt) {
        this.mentorConnectId = mentorConnectId;
        this.applicantId = applicantId;
        this.registeredAt = registeredAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMentorConnectId() {
        return mentorConnectId;
    }

    public void setMentorConnectId(Long mentorConnectId) {
        this.mentorConnectId = mentorConnectId;
    }

    public Long getApplicationId() {
        return applicantId;
    }

    public void setApplicationId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

}
