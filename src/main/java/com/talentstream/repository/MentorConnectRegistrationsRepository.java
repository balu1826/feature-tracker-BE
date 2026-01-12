package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.MentorConnectRegistration;

@Repository
public interface MentorConnectRegistrationsRepository extends JpaRepository<MentorConnectRegistration, Long> {

   MentorConnectRegistration findByMentorConnectIdAndApplicantId(Long mentorConnectId, Long applicantId);

   @Query("SELECT mcr.mentorConnectId FROM MentorConnectRegistration mcr WHERE mcr.applicantId = :applicantId")
    List<Long> findMentorConnectIdsByApplicantId(Long applicantId);
}
