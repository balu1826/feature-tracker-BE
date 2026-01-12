package com.talentstream.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.*;

public class ApplicantCardViewDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    private String name;
    
    @NotBlank(message = "Role is required")
    @Size(min = 2, max = 100, message = "Role must be between 2 and 80 characters")
    private String role;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6789]\\d{9}$", message = "Invalid mobile number. Must be 10 digits starting with 6,7,8 or 9")
    private String mobileNumber;
    
    private String email;
    
    @Min(value = 1900, message = "Pass-out year must be after 1900")
    @Max(value = 2100, message = "Pass-out year must be before 2100")
    @NotNull(message = "Pass-out year is required")
    private Integer passOutyear;
    
    @NotBlank(message = "Address is required")
    @Size(min = 2, max = 500, message = "Address must be between 2 and 500 characters")
    private String address;
    
    private LocalDateTime lastUpdated;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public Integer getPassOutyear() { return passOutyear; }
    public void setPassOutyear(Integer passOutyear) { this.passOutyear = passOutyear; }
}