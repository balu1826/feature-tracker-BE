package com.talentstream.service;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.VIDEO_WATCH_SCORE;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.WATCHED;

import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantVideoWatchHistory;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.ApplicantVideoWatchHistoryRepository;
import com.talentstream.util.ContentType;

@Service
public class ApplicantVideoWatchService {

    @Autowired
    private ApplicantVideoWatchHistoryRepository repository;
    
    @Autowired
	private ApplicantScoreService applicantScoreService;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ApplicantContentViewsService applicantContentViewsService;

    public void saveOrUpdateWatchHistory(Integer applicantId, Long videoId) {

        Applicant applicant = applicantRepository.findById(applicantId.longValue())
                .orElseThrow(() -> new CustomException("Applicant not found with ID: " + applicantId, HttpStatus.NOT_FOUND));
        applicantContentViewsService.recordContentView(applicant, videoId, ContentType.TECH_BUZZ_SHORTS);
        Optional<ApplicantVideoWatchHistory> optionalHistory = repository.findByApplicantIdAndVideoId(applicantId, videoId);
        
        if (optionalHistory.isPresent()) {
            ApplicantVideoWatchHistory history = optionalHistory.get();
            Integer currentCount = history.getViewCount() != null ? history.getViewCount() : 0;
            history.setViewCount(currentCount + 1);
            history.setWatchedAt(LocalDateTime.now());
            repository.save(history);
        } else {
            ApplicantVideoWatchHistory newHistory = new ApplicantVideoWatchHistory();
            newHistory.setApplicantId(applicantId);
            newHistory.setVideoId(videoId);
            newHistory.setWatchedAt(LocalDateTime.now());
            newHistory.setViewCount(1);
            repository.save(newHistory);
            applicantScoreService.updateApplicantScore(newHistory.getApplicantId().longValue(), VIDEO_WATCH_SCORE,WATCHED);
        }
    }
}
