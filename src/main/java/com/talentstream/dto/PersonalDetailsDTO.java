
package com.talentstream.dto;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalDetailsDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 80, message = "Name must be 3–80 characters")
    private String name;


	@NotBlank(message = "Gender is required")
    @Pattern(regexp = "Male|Female|Other", message = "Gender must be Male, Female, or Other. Must follow Case Sensitivity")
    private String gender;

    private String email; 

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6789]\\d{9}$", message = "Phone must be 10 digits starting with 6/7/8/9")
    private String phone; // BasicDetails.alternatePhoneNumber

    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Use format YYYY-MM-DD")
    private String dateOfBirth;

    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^\\d{6}$", message = "PIN must be 6 digits")
    private String pincode;

    @NotBlank(message = "Address is required")
    @Size(min = 2, max = 500, message = "Address must be between 2 and 500 characters")
    private String address;

    @NotNull
    @Size(min = 1, message = "At least one known language is required")
    private List<@NotBlank String> knownLanguages = new ArrayList<>();

	

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getAddress() {
		return address;
	}

	public String getName() {
			return name;
	}

	public void setName(String name) {
			this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public List<String> getKnownLanguages() {
		return knownLanguages;
	}

	public void setKnownLanguages(List<String> knownLanguages) {
		this.knownLanguages = knownLanguages;
	}

    
}
