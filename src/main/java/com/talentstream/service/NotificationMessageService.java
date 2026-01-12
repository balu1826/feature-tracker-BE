package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.talentstream.entity.NotificationMessage;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.NotificationMessageRepository;

@Service
public class NotificationMessageService {

	@Autowired
	private NotificationMessageRepository repo;

	@Autowired
	private ApplicantRepository appRepo;

    private static final Logger logger = LoggerFactory.getLogger(NotificationMessageService.class);

	public List<NotificationMessage> getNotifications(Long applicantId) {

        logger.debug("getNotifications - applicantId={}", applicantId);

        if (!appRepo.existsById(applicantId)) {
            logger.warn("getNotifications - applicant not found id={}", applicantId);
            throw new EntityNotFoundException("applicant not found with id : " + applicantId);
        }

        List<NotificationMessage> notifications = repo.findByApplicantOrSeenApplicant("[" + applicantId + "]");
        logger.debug("getNotifications - returning {} notifications for applicantId={}", notifications == null ? 0 : notifications.size(), applicantId);
        return notifications;
	}

	public boolean removeApplicantFromNotification(Long notificationId, Long applicantId) {

        logger.debug("removeApplicantFromNotification - notificationId={}, applicantId={}", notificationId, applicantId);

        java.util.Optional<NotificationMessage> opt = repo.findById(notificationId);
        if (opt.isEmpty()) {
            logger.warn("removeApplicantFromNotification - notification not found id={}", notificationId);
            throw new RuntimeException("Notification not found");
        }
        NotificationMessage notif = opt.get();

        List<Long> applicants = notif.getApplicantId();
        List<Long> seenApplicants = notif.getSeenApplicantId();

        boolean foundInApplicants = applicants != null && applicants.contains(applicantId);
        boolean foundInSeenApplicants = seenApplicants != null && seenApplicants.contains(applicantId);

        if (!foundInApplicants && !foundInSeenApplicants) {
            logger.warn("removeApplicantFromNotification - applicantId={} not found in notificationId={}", applicantId, notificationId);
            throw new RuntimeException("Applicant ID not found in this notification");
        }

        if (foundInApplicants) {
            applicants.remove(applicantId);
            notif.setApplicantId(applicants);
            logger.debug("removeApplicantFromNotification - removed from applicants list notificationId={}, applicantId={}", notificationId, applicantId);
        }

        if (foundInSeenApplicants) {
            seenApplicants.remove(applicantId);
            notif.setSeenApplicantId(seenApplicants);
            logger.debug("removeApplicantFromNotification - removed from seen applicants notificationId={}, applicantId={}", notificationId, applicantId);
        }

        boolean isApplicantsEmpty = applicants == null || applicants.isEmpty();
        boolean isSeenApplicantsEmpty = seenApplicants == null || seenApplicants.isEmpty();

        if (isApplicantsEmpty && isSeenApplicantsEmpty) {
            repo.delete(notif);
            logger.info("removeApplicantFromNotification - deleted notificationId={} since no applicants remain", notificationId);
        } else {
            repo.save(notif);
            logger.info("removeApplicantFromNotification - updated notificationId={} after removing applicantId={}", notificationId, applicantId);
        }

        return true;
	}

	public void removeApplicantFromAllNotifications(Long applicantId) {
        logger.debug("removeApplicantFromAllNotifications - applicantId={}", applicantId);

        List<NotificationMessage> allNotifications = repo.findAll();

        boolean found = false;
        int saved = 0;
        int deleted = 0;

        for (NotificationMessage notif : allNotifications) {

            boolean changed = false;

            if (notif.getApplicantId() != null && notif.getApplicantId().contains(applicantId)) {
                notif.getApplicantId().remove(applicantId);
                changed = true;
                found = true;
            }

            if (notif.getSeenApplicantId() != null && notif.getSeenApplicantId().contains(applicantId)) {
                notif.getSeenApplicantId().remove(applicantId);
                changed = true;
                found = true;
            }

            if (changed) {
                boolean applicantEmpty = notif.getApplicantId() == null || notif.getApplicantId().isEmpty();
                boolean seenEmpty = notif.getSeenApplicantId() == null || notif.getSeenApplicantId().isEmpty();

                if (applicantEmpty && seenEmpty) {
                    repo.delete(notif);
                    deleted++;
                } else {
                    repo.save(notif);
                    saved++;
                }
            }
        }

        if (!found) {
            logger.warn("removeApplicantFromAllNotifications - applicantId={} not found in any notification", applicantId);
            throw new EntityNotFoundException("Applicant ID not found in any notification");
        }

        logger.info("removeApplicantFromAllNotifications - applicantId={} removed from notifications, saved={}, deleted={}", applicantId, saved, deleted);
    }
	
	public void moveApplicantToSeen(Long notificationId, Long applicantId) {
        logger.debug("moveApplicantToSeen - notificationId={}, applicantId={}", notificationId, applicantId);

        NotificationMessage notif = repo.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        List<Long> applicantList = notif.getApplicantId();
        List<Long> seenList = notif.getSeenApplicantId();

        if (seenList == null) {
            seenList = new ArrayList<>();
        }

        if (seenList.contains(applicantId)) {
            logger.warn("moveApplicantToSeen - applicantId={} already in seen list for notificationId={}", applicantId, notificationId);
            throw new IllegalArgumentException("Applicant alreday seen the notification");
        }

        if (applicantList == null || !applicantList.contains(applicantId)) {
            logger.warn("moveApplicantToSeen - applicantId={} not found in applicants for notificationId={}", applicantId, notificationId);
            throw new EntityNotFoundException("No notification found for the applicant");
        }
        applicantList.remove(applicantId);

        if (!seenList.contains(applicantId)) {
            seenList.add(applicantId);
        }

        notif.setApplicantId(applicantList);
        notif.setSeenApplicantId(seenList);

        repo.save(notif);
        logger.info("moveApplicantToSeen - moved applicantId={} to seen for notificationId={}", applicantId, notificationId);
    }

    public void moveApplicantToSeenEverywhere(Long applicantId) {
        logger.debug("moveApplicantToSeenEverywhere - applicantId={}", applicantId);

        List<NotificationMessage> notifications = repo.findAll();

        boolean found = false;
        int modifiedCount = 0;

        for (NotificationMessage notif : notifications) {

            List<Long> applicantList = notif.getApplicantId();
            List<Long> seenList = notif.getSeenApplicantId();

            boolean modified = false;

            if (applicantList != null && applicantList.contains(applicantId)) {
                applicantList.remove(applicantId);
                modified = true;
                found = true;

                if (seenList == null) {
                    seenList = new ArrayList<>();
                }
                if (!seenList.contains(applicantId)) {
                    seenList.add(applicantId);
                }

                notif.setApplicantId(applicantList);
                notif.setSeenApplicantId(seenList);
            }

            if (modified) {
                repo.save(notif);
                modifiedCount++;
            }
        }

        if (!found) {
            logger.warn("moveApplicantToSeenEverywhere - applicantId={} not found in any notification", applicantId);
            throw new EntityNotFoundException("Applicant not found in any notification");
        }

        logger.info("moveApplicantToSeenEverywhere - applicantId={} moved to seen in {} notifications", applicantId, modifiedCount);
    }
    
    @Async
    public void sendNotificationToApplicants(String message,
                                             String feature,
                                             Long featureId,
                                             List<Long> applicantIds) {

        logger.debug("Sending In-App notifications - entry :  feature={}, featureId={}, applicantCount={}",
                     feature, featureId, applicantIds == null ? 0 : applicantIds.size());

        NotificationMessage notification = new NotificationMessage();
        notification.setCreatedTime(LocalDateTime.now().plusMinutes(330));
        notification.setMessage(message);
        notification.setFeature(feature);
        notification.setApplicantId(applicantIds);
        notification.setFeatureId(featureId);

        repo.save(notification);

        logger.info("Sending In-App notifications - saved notification for featureId={} to {} applicants",
                    featureId, applicantIds.size());
    }


}
