package com.talentstream.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantContentViews;

@Repository
public interface ApplicantContentViewsRepository extends JpaRepository<ApplicantContentViews, Long> {

    Optional<ApplicantContentViews> findByApplicantId(Long applicantId);



}
