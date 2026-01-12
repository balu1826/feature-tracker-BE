package com.talentstream.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.talentstream.validation.ValidQuestion;

@ValidQuestion
@JsonPropertyOrder({"questionType","questionText","options","isRequired"})
public class FeedbackQuestionData {
	
	private Integer questionNo;

	@JsonProperty("questionText")
	@NotBlank(message = "questionText is required")
	@Size(min = 3, max = 500, message = "questionText must be between 3 and 500 characters")
	private String questionText;

	@JsonProperty("questionType")
	@NotBlank(message = "questionType is required.Refer to allowed enum values: RADIO, CHECKBOX, REVIEW, NUMBER, TEXTAREA, EMAIL, TEXT, PHONE")
	@Pattern(
	  regexp = "RADIO|CHECKBOX|REVIEW|NUMBER|TEXTAREA|EMAIL|TEXT|PHONE",
	  message = "Invalid questionType. Refer to allowed enum values: RADIO, CHECKBOX, REVIEW, NUMBER, TEXTAREA, EMAIL, TEXT, PHONE"
	)
	private String questionType;

	@JsonProperty("options")
	@Size(max = 20, message = "options must contain at most 20 entries")
	private List<@NotBlank(message = "option value cannot be blank") @Size(max = 255, message = "option must be at most 255 characters") String> options;

	@JsonProperty("isRequired")
	@NotNull(message = "isRequired must be provided")
	private Boolean isRequired;




	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}
	
	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

    public Integer getQuestionNo() {
		return questionNo;
	}
	public void setQuestionNo(Integer questionNo) {
		this.questionNo = questionNo;
	}

}