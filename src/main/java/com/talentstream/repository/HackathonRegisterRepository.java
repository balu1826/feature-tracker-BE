package com.talentstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.talentstream.entity.HackathonRegister;

import java.util.List;
import java.util.Optional;

public interface HackathonRegisterRepository extends JpaRepository<HackathonRegister, Long> {
    List<HackathonRegister> findByHackathonId(Long hackathonId);
    Optional<HackathonRegister> findByHackathonIdAndUserId(Long hackathonId, Long userId);
    void deleteByHackathonId(Long hackathonId);
    List<HackathonRegister> findByUserId(Long userId);
    
    @Query("SELECT COUNT(hr) FROM HackathonRegister hr WHERE hr.userId = :userId")
    Long countByUserId(Long userId);
}
