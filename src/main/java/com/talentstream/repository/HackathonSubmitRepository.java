package com.talentstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.HackathonSubmit;

import java.util.List;
import java.util.Optional;

public interface HackathonSubmitRepository extends JpaRepository<HackathonSubmit, Long> {
    List<HackathonSubmit> findByHackathonId(Long hackathonId);
    Optional<HackathonSubmit> findByHackathonIdAndUserId(Long hackathonId, Long userId);

}
