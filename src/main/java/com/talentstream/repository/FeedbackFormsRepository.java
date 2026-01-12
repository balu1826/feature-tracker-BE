package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talentstream.dto.FeedbackFormsResponseDTO;
import com.talentstream.entity.FeedbackForms;

@Repository
public interface FeedbackFormsRepository extends JpaRepository<FeedbackForms, Long> {

    @Query("SELECT new com.talentstream.dto.FeedbackFormsResponseDTO(f.mentorName, f.collegeName, f.title,f.isActive) FROM FeedbackForms f order by f.createdAt desc")
    List<FeedbackFormsResponseDTO> getAllFeedbackForms();

    @Query("SELECT f FROM FeedbackForms f WHERE f.id = :formId AND f.recruiter.recruiterId = :recruiterId")
    FeedbackForms findByIdAndRecruiterId(Long formId, Long recruiterId);
    
    @Query("SELECT f FROM FeedbackForms f WHERE f.recruiter.recruiterId = :recruiterId")
    List<FeedbackForms> findByRecruiterId(Long recruiterId);
    
    @Query("SELECT new com.talentstream.dto.FeedbackFormsResponseDTO(f.mentorName, f.collegeName, f.title,f.isActive) FROM FeedbackForms f WHERE f.isActive = true order by f.createdAt desc")
    List<FeedbackFormsResponseDTO> findAllByIsActiveTrue();

	Optional<FeedbackForms> findByIdAndIsActiveTrue(Long formId);

}
            