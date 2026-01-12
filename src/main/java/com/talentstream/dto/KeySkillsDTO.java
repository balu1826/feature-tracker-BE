package com.talentstream.dto;
import java.util.List;
import javax.validation.constraints.NotEmpty;

public class KeySkillsDTO {
    @NotEmpty(message = "At least one skill is required")
    private List<String> skills; // plain names

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
