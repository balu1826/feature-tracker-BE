// src/main/java/com/talentstream/repository/ApplicantProjectRepository.java
package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantProject;

//ApplicantProjectRepository.java
@Repository
public interface ApplicantProjectRepository extends JpaRepository<ApplicantProject, Long> {

 @Query("SELECT a FROM ApplicantProject a WHERE a.applicant.id = :applicantId ORDER BY a.id ASC")
 List<ApplicantProject> findByApplicantId(Long applicantId);

 // NEW: grab the latest project to update
 Optional<ApplicantProject> findTopByApplicantIdOrderByUpdatedAtDesc(Long applicantId);

 // (optional) for sorted lists
 List<ApplicantProject> findByApplicantIdOrderByUpdatedAtDesc(Long applicantId);

 Integer countByApplicantId(Long applicantId);

 Optional<ApplicantProject> findByIdAndApplicantId(Long projectId, Long applicantId);
}
