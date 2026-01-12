package com.talentstream.dto;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HackathonUpdateDTO {

    private Long recruiterId;

    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @Size(min = 20, max = 1000, message = "Description must be between 20 and 1000 characters")
    private String description;

    @Size(min = 5, max = 100, message = "Company name must be between 5 and 100 characters")
    private String company;


    private String bannerUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endAt;

    private String instructions;
    private String eligibility;

    @Size(min = 1, max = 100, message = "Give at least one skill")
    private String allowedTechnologies;


    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public LocalDate getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDate startAt) {
        this.startAt = startAt;
    }

    public LocalDate getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDate endAt) {
        this.endAt = endAt;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getAllowedTechnologies() {
        return allowedTechnologies;
    }

    public void setAllowedTechnologies(String allowedTechnologies) {
        this.allowedTechnologies = allowedTechnologies;
    }
}
