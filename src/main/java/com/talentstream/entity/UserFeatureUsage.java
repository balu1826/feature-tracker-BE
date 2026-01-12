package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_feature_usage")
public class UserFeatureUsage {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "blogs_count", nullable = false)
    private int blogsCount = 0;

    @Column(name = "shorts_count", nullable = false)
    private int shortsCount = 0;

    @Column(name = "hackathons_count", nullable = false)
    private int hackathonsCount = 0;

    /* =====================
       CONSTRUCTORS
       ===================== */

    protected UserFeatureUsage() {
        // JPA requirement
    }

    public UserFeatureUsage(Long userId) {
        this.userId = userId;
    }

    /* =====================
       BUSINESS METHODS
       ===================== */

    public void incrementBlogs() {
        this.blogsCount++;
    }

    public void incrementShorts() {
        this.shortsCount++;
    }

    public void incrementHackathons() {
        this.hackathonsCount++;
    }

    /* =====================
       GETTERS (no setters for counters)
       ===================== */

    public Long getUserId() {
        return userId;
    }

    public int getBlogsCount() {
        return blogsCount;
    }

    public int getShortsCount() {
        return shortsCount;
    }

    public int getHackathonsCount() {
        return hackathonsCount;
    }
}