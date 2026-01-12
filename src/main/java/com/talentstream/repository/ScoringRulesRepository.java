package com.talentstream.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.dto.BadgeScoreDTO;
import com.talentstream.entity.ScoringRules;

@Repository
public interface ScoringRulesRepository extends JpaRepository<ScoringRules, UUID> {

	   @Query("SELECT sr.points FROM ScoringRules sr WHERE sr.activityName = :activityName AND sr.activityDetail = :activityDetail")
	    Integer findPointsByActivityNameAndDetail(@Param("activityName") String activityName, 
	                                                       @Param("activityDetail") String activityDetail);

	   @Query("SELECT new com.talentstream.dto.BadgeScoreDTO(sr.activityDetail, sr.points) " +
	           "FROM ScoringRules sr " +
	           "WHERE sr.activityName = 'badge_allotment_score'")
	    List<BadgeScoreDTO> findAllBadgeScores();
}