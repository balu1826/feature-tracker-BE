package com.talentstream.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantContentViews;
import com.talentstream.repository.ApplicantContentViewsRepository;
import com.talentstream.repository.BlogRepository;
import com.talentstream.repository.VideoMetadataRepository;
import com.talentstream.util.ContentType;

@Service
public class ApplicantContentViewsService {

    private final BlogRepository blogRepository;
    private final ApplicantContentViewsRepository applicantContentViewsRepository;
    private final VideoMetadataRepository videoMetadataRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(ApplicantContentViewsService.class);

    public ApplicantContentViewsService(
            ApplicantContentViewsRepository applicantContentViewsRepository,
            BlogRepository blogRepository, VideoMetadataRepository videoMetadataRepository) {
        this.applicantContentViewsRepository = applicantContentViewsRepository;
        this.blogRepository = blogRepository;
        this.videoMetadataRepository = videoMetadataRepository;
    }

    @Async
    public void recordContentView(Applicant applicant, Long contentId, ContentType contentType) {

        // Fetch or create row for applicant
        ApplicantContentViews views =
                applicantContentViewsRepository
                        .findByApplicantId(applicant.getId())
                        .orElseGet(() -> {
                            ApplicantContentViews acv = new ApplicantContentViews();
                            acv.setApplicant(applicant);
                            return acv;
                        });

        switch (contentType) {
            case TECH_VIBES:
                updateTechVibesViews(views, contentId, applicant);
                break;
            case TECH_BUZZ_SHORTS:
                updateTechBuzzShortsViews(views, contentId, applicant);
                break;
        
            default:
                break;
        }      

        // Save updated JSON
        applicantContentViewsRepository.save(views);
    }
private void updateTechBuzzShortsViews(ApplicantContentViews views, Long techBuzzShortId, Applicant applicant) {
        List<Long> seenTechBuzzShorts = views.getSeenTechBuzzShortIds();
        if (seenTechBuzzShorts == null) {
            seenTechBuzzShorts = new ArrayList<>();
        }
        //  If already viewed → do nothing
        if (seenTechBuzzShorts.contains(techBuzzShortId)) {
            logger.info("Applicant {} already viewed TechBuzzShort {}", applicant.getId(), techBuzzShortId);
            return;
        }

        //  Add blog ID
        seenTechBuzzShorts.add(techBuzzShortId);
        views.setSeenTechBuzzShortIds(seenTechBuzzShorts);

        //  Increment TechBuzzShort view count only once per applicant
        videoMetadataRepository.findById(techBuzzShortId).ifPresent(video -> {
            video.setViewCount(video.getViewCount() + 1);
            videoMetadataRepository.save(video);
        });

      
}

public void updateTechVibesViews(ApplicantContentViews views, Long techVibeId, Applicant applicant) {
    List<Long> seenTechVibes = views.getSeenTechVibeIds();
            if (seenTechVibes == null) {
                seenTechVibes = new ArrayList<>();
            }
            //  If already viewed → do nothing
            if (seenTechVibes.contains(techVibeId)) {
                logger.info("Applicant {} already viewed TechVibe {}", applicant.getId(), techVibeId);
                return;
            }

            //  Add blog ID
            seenTechVibes.add(techVibeId);
            views.setSeenTechVibeIds(seenTechVibes);

            //  Increment blog view count only once per applicant
            blogRepository.findById(techVibeId).ifPresent(blog -> {
                blog.setViewCount(blog.getViewCount() + 1);
                blogRepository.save(blog);
            });
}
}