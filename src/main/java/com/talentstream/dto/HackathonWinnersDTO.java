package com.talentstream.dto;

public class HackathonWinnersDTO {
    private Long applicantId;
    private String firstName;
    private String lastName;
    private String imageUrl;

    public HackathonWinnersDTO(Long applicantId, String firstName, String lastName, String imageUrl) {
        this.applicantId = applicantId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageUrl = imageUrl;
    }

    public Long getApplicantId() { return applicantId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getImageUrl() { return imageUrl; }
}
