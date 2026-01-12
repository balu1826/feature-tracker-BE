package com.talentstream.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantScore;

@Repository
public interface ApplicantScoreRepository extends JpaRepository<ApplicantScore, UUID> {

	@Query("SELECT a.total_score FROM ApplicantScore a WHERE a.applicant.id = :applicantId")
	Integer findTotalScoreByApplicantId(@Param("applicantId") Long applicantId);

	Optional<ApplicantScore> findByApplicantId(Long applicant_id);

	@Modifying
	@Query("UPDATE ApplicantScore a SET a.total_score = a.total_score + :points, a.lastUpdated = CURRENT_TIMESTAMP WHERE a.applicant.id = :applicantId")
	void updateTotalScore(@Param("applicantId") Long applicantId, @Param("points") Integer points);

}
