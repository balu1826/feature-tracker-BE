package com.talentstream.dto;

import java.util.List;

public class ApplicantScoringDetailsResponseDTO {

    private Integer total_score;
    private String level;
    private String current_badge;
    private List<BadgeScoreDTO> badgeScores;

    public ApplicantScoringDetailsResponseDTO(Integer total_score, String level, String current_badge,
                                              List<BadgeScoreDTO> badgeScores) {
        super();
        this.total_score = total_score;
        this.level = level;
        this.current_badge = current_badge;
        this.badgeScores = badgeScores;
    }

    public ApplicantScoringDetailsResponseDTO(Integer total_score, String level, String current_badge) {
        super();
        this.total_score = total_score;
        this.level = level;
        this.current_badge = current_badge;
    }

    public Integer getTotal_score() {
        return total_score;
    }

    public void setTotal_score(Integer total_score) {
        this.total_score = total_score;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCurrent_badge() {
        return current_badge;
    }

    public void setCurrent_badge(String current_badge) {
        this.current_badge = current_badge;
    }

    public List<BadgeScoreDTO> getBadgeScores() {
        return badgeScores;
    }

    public void setBadgeScores(List<BadgeScoreDTO> badgeScores) {
        this.badgeScores = badgeScores;
    }
}
