package com.talentstream.dto;


public class VideoMetadataDto {
    private Long videoId;
    private String s3url;
    private String level;
    private String title;
    private String thumbnail_url;
    private String skillTag;

    public VideoMetadataDto(Long videoId, String s3url, String tags, String title,String thumbnail_url, String skillTag) {
        this.videoId = videoId;
        this.s3url = s3url;
        this.level = tags;
        this.title = title;
        this.setThumbnail_url(thumbnail_url);
        this.skillTag = skillTag;
    }

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public String getS3url() {
		return s3url;
	}

	public void setS3url(String s3url) {
		this.s3url = s3url;
	}

	public String getTags() {
		return level;
	}

	public void setTags(String tags) {
		this.level = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail_url() {
		return thumbnail_url;
	}

	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}

	public String getSkillTag() {
		return skillTag;
	}

	public void setSkillTag(String skillTag) {
		this.skillTag = skillTag;
	}

	
}
