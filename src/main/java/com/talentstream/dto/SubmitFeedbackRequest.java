package com.talentstream.dto;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class SubmitFeedbackRequest {
    @NotNull
    private Map<String, Object> answers;

    public Map<String, Object> getAnswers() { return answers; }
    public void setAnswers(Map<String, Object> answers) { this.answers = answers; }
}
