package com.talentstream.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_metadata")
public class VideoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "video_id")
    private Long videoId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING) // store enum as STRING
    @Column(nullable = false)
    private VideoLevel level;

    @Column(name = "s3url", nullable = false)
    private String s3Url;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "skill_tag", nullable = false)
    private String skillTag; 
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "view_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer viewCount = 0;

    // Constructors
    public VideoMetadata() {}

    public VideoMetadata(String title, VideoLevel level, String s3Url, String thumbnailUrl, String skillTag) {
        this.title = title;
        this.level = level;
        this.s3Url = s3Url;
        this.thumbnailUrl = thumbnailUrl;
        this.skillTag = skillTag;
    }

    public VideoMetadata(String title, VideoLevel level, String s3Url, String skillTag) {
        this.title = title;
        this.level = level;
        this.s3Url = s3Url;
        this.skillTag = skillTag;
    }

    // Getters and Setters
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public VideoLevel getLevel() {
        return level;
    }

    public void setLevel(VideoLevel level) {
        this.level = level;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSkillTag() {
        return skillTag;
    }

    public void setSkillTag(String skillTag) {
        this.skillTag = skillTag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
}
