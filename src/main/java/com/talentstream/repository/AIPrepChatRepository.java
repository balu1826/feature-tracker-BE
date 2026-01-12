package com.talentstream.repository;

import com.talentstream.entity.AIPrepChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public interface AIPrepChatRepository extends JpaRepository<AIPrepChat, Long> {

    // Find all chats by applicant ID
    List<AIPrepChat> findByApplicantIdOrderByCreatedAtDesc(Long applicantId);

    // Find chat by ID and applicant ID (for security)
    Optional<AIPrepChat> findByChatIdAndApplicantId(Long chatId, Long applicantId);

    // Delete by chat ID and applicant ID (for security)
    void deleteByChatIdAndApplicantId(Long chatId, Long applicantId);

    // Custom query to get only titles
    @Query("SELECT new com.talentstream.dto.ChatTitleDTO(c.chatId, c.title, c.createdAt) " +
           "FROM AIPrepChat c WHERE c.applicantId = :applicantId ORDER BY c.createdAt DESC")
    List<com.talentstream.dto.ChatTitleDTO> findChatTitlesByApplicantId(@Param("applicantId") Long applicantId);
    
    // Custom query to get only the latest 20 chat titles for uniqueness check
    @Query(value = "SELECT c.title FROM ai_prep_chats c WHERE c.applicant_id = :applicantId ORDER BY c.created_at DESC LIMIT 20", nativeQuery = true)
    List<String> findLatest20ChatTitlesByApplicantId(@Param("applicantId") Long applicantId);
    
    @Modifying
    @Query("DELETE FROM AIPrepChat c WHERE c.updatedAt < :cutoffDate")
    @Transactional
    int deleteOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
