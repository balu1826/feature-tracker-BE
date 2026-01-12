package com.talentstream.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "registrations", indexes = { @Index(columnList = "hackathonId"), @Index(columnList = "userId") })
public class HackathonRegister {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long hackathonId;
	private Long userId; 
	private Boolean registrationStatus;
	private Boolean submitStatus;
	private LocalDateTime registeredAt;
	private String name;

	


	public HackathonRegister() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHackathonId() {
		return hackathonId;
	}

	public void setHackathonId(Long hackathonId) {
		this.hackathonId = hackathonId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Boolean isRegistaratinStatus() {
		return registrationStatus;
	}

	public void setRegistaratinStatus(Boolean registaratinStatus) {
		this.registrationStatus = registaratinStatus;
	}

	public Boolean isSubmitStatus() {
		return submitStatus;
	}

	public void setSubmitStatus(Boolean submitStatus) {
		this.submitStatus = submitStatus;
	}
	public LocalDateTime getRegisteredAt() {
		return registeredAt;
	}

	public void setRegisteredAt(LocalDateTime registeredAt) {
		this.registeredAt = registeredAt;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
