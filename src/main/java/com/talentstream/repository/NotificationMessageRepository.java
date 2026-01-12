package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talentstream.entity.NotificationMessage;

public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, Long> {

	@Query(value = "SELECT * FROM notification_message " + "WHERE applicant_id @> CAST(:json AS jsonb) "
			+ "OR seen_applicant_id @> CAST(:json AS jsonb) "
			+ "ORDER BY created_time DESC", nativeQuery = true)
	List<NotificationMessage> findByApplicantOrSeenApplicant(@Param("json") String json);

}
