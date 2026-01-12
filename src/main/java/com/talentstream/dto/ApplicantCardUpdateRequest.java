package com.talentstream.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class ApplicantCardUpdateRequest {

    @NotBlank
    @Size(max = 80)
    private String name;

    @Pattern(regexp = "^[0-9+\\-()\\s]{8,20}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Min(1900)
    @Max(2100)
    private Integer passYear;

    @Size(max = 80)
    private String city;

    @Size(max = 80)
    private String state;

    public ApplicantCardUpdateRequest() {}

    public ApplicantCardUpdateRequest(String name, String mobileNumber,
                                      Integer passYear, String city, String state) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.passYear = passYear;
        this.city = city;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getPassYear() {
        return passYear;
    }

    public void setPassYear(Integer passYear) {
        this.passYear = passYear;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
