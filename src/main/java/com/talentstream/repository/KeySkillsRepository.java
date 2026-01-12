// src/main/java/com/talentstream/repository/KeySkillsRepository.java
package com.talentstream.repository;

import com.talentstream.entity.ApplicantSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeySkillsRepository extends JpaRepository<ApplicantSkills, Long> {
    Optional<ApplicantSkills> findBySkillNameIgnoreCase(String skillName);
}
