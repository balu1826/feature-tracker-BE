package com.talentstream.dto;

public class BadgeScoreDTO {

    private String badge;
    private Integer points;

    public BadgeScoreDTO(String badge, Integer points) {
        this.badge = badge;
        this.points = points;
    }

    public String getBadge() {
        return badge;
    }

    public Integer getPoints() {
        return points;
    }
}
