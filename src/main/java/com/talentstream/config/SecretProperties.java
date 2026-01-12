package com.talentstream.config;

public class SecretProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
    private String cloudfrontDomain;
    private boolean useCloudFront;
    private String s3Domain;
    private String cloudfrontUrl;
    private String smtpUsername;
    private String smtpPassword;

    // NEW: folder names
    private String s3FolderVideos;
    private String s3FolderThumbnails;

    // getters & setters

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getCloudfrontDomain() { return cloudfrontDomain; }
    public void setCloudfrontDomain(String cloudfrontDomain) { this.cloudfrontDomain = cloudfrontDomain; }

    public boolean isUseCloudFront() { return useCloudFront; }
    public void setUseCloudFront(boolean useCloudFront) { this.useCloudFront = useCloudFront; }

    public String getS3Domain() { return s3Domain; }
    public void setS3Domain(String s3Domain) { this.s3Domain = s3Domain; }

    public String getCloudfrontUrl() { return cloudfrontUrl; }
    public void setCloudfrontUrl(String cloudfrontUrl) { this.cloudfrontUrl = cloudfrontUrl; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    // new folder getters/setters
    public String getS3FolderVideos() { return s3FolderVideos; }
    public void setS3FolderVideos(String s3FolderVideos) { this.s3FolderVideos = s3FolderVideos; }

    public String getS3FolderThumbnails() { return s3FolderThumbnails; }
    public void setS3FolderThumbnails(String s3FolderThumbnails) { this.s3FolderThumbnails = s3FolderThumbnails; }
}
