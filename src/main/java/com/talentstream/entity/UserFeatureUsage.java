package com.talentstream.entity;

import javax.persistence.*;

@Entity
@Table(name = "feature_tracker")
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
    
    @Column(name = "ask_newton_count", nullable = false)
    private int askNewton = 0;
    
    
    /* =====================
       CONSTRUCTORS
       ===================== */

    public int getAskNewton() {
		return askNewton;
	}

	public void setAskNewton(int askNewton) {
		this.askNewton = askNewton;
	}

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
    
    public void incrementAskNewton() {
        this.askNewton++;
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