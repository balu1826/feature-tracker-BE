package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantSkills;

@Repository
public interface ApplicantSkillsRepository extends JpaRepository<ApplicantSkills, Long> {

	 List<ApplicantSkills> findBySkillName(String skillName);
	 List<ApplicantSkills> findBySkillNameIgnoreCaseIn(List<String> skillNames);
}
