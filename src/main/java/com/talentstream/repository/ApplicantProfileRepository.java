	package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantProfile;

@Repository
public interface ApplicantProfileRepository extends JpaRepository<ApplicantProfile, Integer> {

ApplicantProfile findByApplicantId(long applicantid);

@Query("SELECT a FROM ApplicantProfile a JOIN FETCH a.skillsRequired WHERE a.applicant.id = :applicantId")
Optional<ApplicantProfile> findByApplicantIdWithSkills(@Param("applicantId") long applicantId);
List<ApplicantProfile> findByApplicantIdIn(List<Long> applicantIds);

@Query("SELECT ap.applicant.id " +
        "FROM ApplicantProfile ap " +
        "JOIN ap.skillsRequired s " +
        "WHERE LOWER(s.skillName) = LOWER(:skill)")
 List<Long> findApplicantsBySkill(@Param("skill") String skill);

@Query("SELECT DISTINCT ap.applicant.id " +
	       "FROM ApplicantProfile ap " +
	       "JOIN ap.skillsRequired s " +
	       "WHERE LOWER(s.skillName) IN :skills")
	List<Long> findApplicantsByAnySkill(@Param("skills") List<String> skills);

@Query("SELECT ap.applicant.id FROM ApplicantProfile ap")
List<Long> findApplicantsByApplicantId();

}