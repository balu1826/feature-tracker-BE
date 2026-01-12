package com.talentstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.UserFeatureUsage;

import java.util.Optional;

public interface UserFeatureUsageRepository
        extends JpaRepository<UserFeatureUsage, Long> {

    Optional<UserFeatureUsage> findByUserId(Long userId);
}
