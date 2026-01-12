package com.talentstream.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class VideoUploadRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotNull(message = "Level is required")
    private String level;

    @NotNull(message = "Video file is required")
    private MultipartFile file;

    @NotNull(message = "Thumbnail is required")
    private MultipartFile thumbnail;

    @NotBlank(message = "Skill tag is required")
    @Pattern(regexp = "^[a-zA-Z ,]+$", message = "Skill tag must contain only letters and spaces")
    private String skillTag;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public MultipartFile getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(MultipartFile thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSkillTag() {
        return skillTag;
    }

    public void setSkillTag(String skillTag) {
        this.skillTag = skillTag;
    }
}
