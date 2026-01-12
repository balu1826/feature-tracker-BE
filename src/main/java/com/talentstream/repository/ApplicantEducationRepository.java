package com.talentstream.repository;

import com.talentstream.entity.ApplicantEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantEducationRepository extends JpaRepository<ApplicantEducation, Long> {
    ApplicantEducation findByApplicantId(Long applicantId);
}
