package com.talentstream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedBackAnswerDto {

    @JsonProperty("questionNo")
    private Integer questionNo;

    @JsonProperty("answer")
    private Object answer;

    public Integer getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(Integer questionNo) {
        this.questionNo = questionNo;
    }

    public Object getAnswer() {
        return answer;
    }

    public void setAnswer(Object answer) {
        this.answer = answer;
    }
}
