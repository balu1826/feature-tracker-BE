package com.talentstream.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.talentstream.dto.AnalyticsEventRequest;
import com.talentstream.entity.UserFeatureUsage;
import com.talentstream.repository.UserFeatureUsageRepository;

@Service
public class FeatureUsageService {

    private final UserFeatureUsageRepository repo;

    public FeatureUsageService(UserFeatureUsageRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void recordFeatureEvent(AnalyticsEventRequest request) {

        long userId = request.getUserId();
        String feature = request.getFeature();

        UserFeatureUsage usage = repo.findByUserId(userId).orElse(null);

        // 🆕 USER DOES NOT EXIST → CREATE NEW ROW
        if (usage == null) {
            usage = new UserFeatureUsage(userId);

            switch (feature) {
            case "BLOGS":
                usage.incrementBlogs();
                break;
            case "SHORTS":
                usage.incrementShorts();
                break;
            case "HACKATHONS":
                usage.incrementHackathons();
                break;
            case "ASK NEWTON":
            	usage.incrementAskNewton();
            	break;
            default:
                throw new IllegalArgumentException(
                    "Invalid feature: " + feature
                );
        }


            repo.save(usage);
            return;
        }

        // 👤 USER EXISTS → JUST INCREMENT
        switch (feature) {
        case "BLOGS":
            usage.incrementBlogs();
            break;
        case "SHORTS":
            usage.incrementShorts();
            break;
        case "HACKATHONS":
            usage.incrementHackathons();
            break;
        default:
            throw new IllegalArgumentException(
                "Invalid feature: " + feature
            );
    }

    
    }
}
