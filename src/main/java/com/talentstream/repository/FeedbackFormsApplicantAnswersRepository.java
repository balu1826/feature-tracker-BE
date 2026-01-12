package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.FeedbackFormsApplicantAnswers;

@Repository
public interface FeedbackFormsApplicantAnswersRepository extends JpaRepository<FeedbackFormsApplicantAnswers, Long> {
	// use nested property traversal (form.id, applicant.id)
	List<FeedbackFormsApplicantAnswers> findByForm_Id(Long formId);

	Optional<FeedbackFormsApplicantAnswers> findByForm_IdAndApplicant_Id(Long formId, Long applicantId);

	List<FeedbackFormsApplicantAnswers> findByApplicant_Id(Long applicantId);

	Optional<FeedbackFormsApplicantAnswers> findByFormIdAndApplicantId(Long formId, Long applicantId);

	@Modifying
	@Query("DELETE FROM FeedbackFormsApplicantAnswers a WHERE a.form.id = :formId")
	void deleteByFormId(@Param("formId") Long formId);

}
