// src/main/java/com/talentstream/entity/BasicDetails.java
package com.talentstream.entity;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class BasicDetails {
    @NotBlank(message = "Firstname is required.")
    @Pattern(regexp = "^[a-zA-Z ]{3,19}$", message = "invalid username")
    @Size(min = 3, message = "First name must be at least 3 characters long.")
    private String firstName;

    @NotBlank(message = "Lastname is required.")
    @Pattern(regexp = "^[a-zA-Z ]{3,19}$", message = "invalid username")
    @Size(min = 3, message = "Last name must be at least 3 characters long.")
    private String lastName;

    // NEW
    private String gender; // Male / Female / Other

    private String dateOfBirth;
    
    @NotEmpty(message = "address cannot be empty")
    private String address;
    private String city;
    private String state;
    private String pincode;

    // Secondary email & phone (your existing names)
    @NotBlank
    @Email(message = "invalid email address")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$", message = "invalid mobile number")
    private String alternatePhoneNumber;

    // NEW
    @ElementCollection
    private List<String> knownLanguages = new ArrayList<>();
    
    @Column(name = "pass_out_year")
    @Min(value = 2000, message = "Pass-out year must be 2000 or later")
    @Max(value = 2030, message = "Pass-out year must not exceed 2030")
    private Integer passOutYear;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAlternatePhoneNumber() {
		return alternatePhoneNumber;
	}

	public void setAlternatePhoneNumber(String alternatePhoneNumber) {
		this.alternatePhoneNumber = alternatePhoneNumber;
	}

	public List<String> getKnownLanguages() {
		return knownLanguages;
	}

	public void setKnownLanguages(List<String> knownLanguages) {
		this.knownLanguages = knownLanguages;
	}
	
	 public Integer getPassOutYear() {
	        return passOutYear;
	    }

	    public void setPassOutYear(Integer passOutYear) {
	        this.passOutYear = passOutYear;
	    }

   
}
