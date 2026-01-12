package com.talentstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.talentstream.entity.Hackathon;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
	
	public Optional<List<Hackathon>> findByRecruiterId(Long id);
	Optional<Hackathon> findById(Long id);
	
	 @Query("SELECT COUNT(h) FROM Hackathon h WHERE h.winner = :winnerId")
	 Long countByWinnerId(Long winnerId);
}
