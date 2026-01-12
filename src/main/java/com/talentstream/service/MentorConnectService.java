package com.talentstream.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.talentstream.dto.MentorConnectDTO;
import com.talentstream.dto.MentorConnectRequestDTO;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.MentorConnect;
import com.talentstream.entity.MentorConnectRegistration;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.MentorConnectRegistrationsRepository;
import com.talentstream.repository.MentorConnectRepository;

@Service
public class MentorConnectService {

	private static final Logger logger = LoggerFactory.getLogger(MentorConnectService.class);

	private final MentorConnectRepository repository;

	private final NotificationMessageService notificationMessageService;

	private final FirebaseMessagingService firebaseMessagingService;

	private final MentorConnectRegistrationsRepository mentorConnectregistrationsRepository;

	private final ApplicantProfileRepository applicantProfileRepository;

	private final EmailService emailService;

	public MentorConnectService(MentorConnectRepository repository,
			NotificationMessageService notificationMessageService, FirebaseMessagingService firebaseMessagingService,
			MentorConnectRegistrationsRepository mentorConnectregistrationsRepository,
			ApplicantProfileRepository applicantProfileRepository, EmailService emailService) {
		this.repository = repository;
		this.notificationMessageService = notificationMessageService;
		this.firebaseMessagingService = firebaseMessagingService;
		this.mentorConnectregistrationsRepository = mentorConnectregistrationsRepository;
		this.applicantProfileRepository = applicantProfileRepository;
		this.emailService = emailService;
	}

	public Map<String, List<MentorConnectDTO>> getOngoingAndUpcomingMeetings() {
		logger.info("Service - getOngoingAndUpcomingMeetings called");
		try {
			List<MentorConnect> meetings = repository.findMeetingByCreatedAtDate();
			List<MentorConnect> activeMeetings = meetings.stream()
					.filter(m -> !m.getStatus().equalsIgnoreCase("Expired")).collect(Collectors.toList());

			if (activeMeetings.isEmpty()) {
				logger.info("service - getOngoingAndUpcomingMeetings - no active meetings found");
				throw new CustomException("No ongoing or upcoming meetings found", HttpStatus.NOT_FOUND);
			}

			logger.info("service - getOngoingAndUpcomingMeetings - found {} active meetings", activeMeetings.size());
			List<MentorConnectDTO> meetingDTOs = activeMeetings.stream()
					.map(MentorConnectDTO::new)
					.collect(Collectors.toList());

			return Map.of("items", meetingDTOs);
		} catch (Exception e) {
			logger.error("service - getOngoingAndUpcomingMeetings - error fetching meetings", e);
			throw new CustomException("Error fetching meetings: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public MentorConnectDTO getMeetingById(Long mentorConnectId) {
		logger.info("service - getMeetingById - called with id={}", mentorConnectId);
		try {
			MentorConnect mentorConnect = repository.findById(mentorConnectId).orElse(null);
			if (mentorConnect == null) {
				logger.warn("service - getMeetingById - meeting not found id={}", mentorConnectId);
				throw new CustomException("Meeting not found with ID: " + mentorConnectId, HttpStatus.NOT_FOUND);
			}
			logger.info("service - getMeetingById - returning meeting id={}", mentorConnectId);
			return new MentorConnectDTO(mentorConnect);
		} catch (Exception e) {
			logger.error("service - getMeetingById - error fetching meeting id={}", mentorConnectId, e);
			throw new CustomException("Error fetching meeting: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String updateAssets(Long mentorConnectId, String bannerUrl, String profileUrl) {
		logger.info("service - updateAssets - id={}, banner={}, profile={}", mentorConnectId, bannerUrl, profileUrl);
		try {
			MentorConnect mentorConnect = repository.findById(mentorConnectId).orElse(null);
			if (mentorConnect == null) {
				logger.warn("service - updateAssets - meeting not found id={}", mentorConnectId);
				throw new CustomException("Meeting not found with ID: " + mentorConnectId, HttpStatus.NOT_FOUND);
			}

			if (bannerUrl != null && !bannerUrl.isBlank())
				mentorConnect.setBannerImageUrl(bannerUrl.trim());
			if (profileUrl != null && !profileUrl.isBlank())
				mentorConnect.setMentorProfileUrl(profileUrl.trim());
			logger.info("service - updateAssets - updated assets for id={}", mentorConnectId);

			repository.save(mentorConnect);
			return "Assets updated successfully for Mentor Connect ID: " + mentorConnectId;
		} catch (Exception e) {
			logger.error("service - updateAssets - error updating assets id={}", mentorConnectId, e);
			throw new CustomException("Error updating assets: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String createMentorConnect(MentorConnectRequestDTO req) {
		logger.info("Service - createMentorConnect called");

		MentorConnect savedMentorConnect;
		try {
			logger.info("service - createMentorConnect - request title='{}' mentor='{}'", req.title, req.mentorName);
			MentorConnect mc = new MentorConnect();

			mc.setBannerImageUrl(req.bannerImageUrl);
			mc.setDate(req.date);
			mc.setDescription(req.description);
			mc.setDurationMinutes(req.duration);
			mc.setMeetLink(req.meetLink);
			mc.setMentorName(req.mentorName);
			mc.setMentorProfileUrl(req.mentorProfileUrl);
			mc.setStartTime(req.startTime);
			mc.setCreatedAt(LocalDateTime.now().plusMinutes(330));
			mc.setTitle(req.title);

			savedMentorConnect = repository.save(mc);

		} catch (Exception e) {
			logger.error("service - createMentorConnect - error creating Mentor Connect", e);
			throw new CustomException("Error creating Mentor Connect: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		logger.info("service - createMentorConnect - sending broadcast notification for id={}",
				savedMentorConnect.getMeetingId());
		firebaseMessagingService.sendNotificationToAll("New Mentor Connect: " + savedMentorConnect.getTitle(),
				"Grow your skills—don't miss this inspiring session.");

		List<Long> applicantIds = applicantProfileRepository.findApplicantsByApplicantId();
		if (applicantIds.isEmpty()) {
			logger.warn("service - createMentorConnect - no applicants found to notify");
			throw new CustomException("No applicants found to notify", HttpStatus.NOT_FOUND);
		}

		logger.info("service - createMentorConnect - sending applicant notifications to {} applicants",
				applicantIds.size());
		notificationMessageService.sendNotificationToApplicants(
				"A new Mentor Connect has been posted: " + savedMentorConnect.getTitle(), "Mentor Connect",
				savedMentorConnect.getMeetingId(), applicantIds);

		logger.info("service - createMentorConnect - completed for id={}", savedMentorConnect.getMeetingId());
		return "Mentor Connect created successfully with ID: " + savedMentorConnect.getMeetingId();
	}

	public String registerMentorConnect(Long mentorConnectId, Long applicantId) {
		logger.info("Service - registerMentorConnect called with mentorConnectId={} and applicantId={}",
				mentorConnectId, applicantId);

		MentorConnect mentorConnect = repository.findById(mentorConnectId).orElseThrow(() -> {
			logger.warn("service - registerMentorConnect - Mentor Connect not found with ID={}", mentorConnectId);
			return new CustomException("Mentor Connect not found with ID: " + mentorConnectId, HttpStatus.NOT_FOUND);
		});

		ApplicantProfile applicant = applicantProfileRepository.findByApplicantId(applicantId);
		if (applicant == null) {
			logger.warn("service - registerMentorConnect - Applicant not found with ID={}", applicantId);
			throw new CustomException("Applicant not found with ID: " + applicantId, HttpStatus.NOT_FOUND);
		}
		MentorConnectRegistration existingRegistration = mentorConnectregistrationsRepository
				.findByMentorConnectIdAndApplicantId(mentorConnectId, applicantId);
		if (existingRegistration != null) {
			logger.warn(
					"service - registerMentorConnect - Applicant with ID={} is already registered for Mentor Connect ID={}",
					applicantId, mentorConnectId);
			throw new CustomException("Applicant with ID: " + applicantId
					+ " is already registered for Mentor Connect ID: " + mentorConnectId, HttpStatus.CONFLICT);
		}
		MentorConnectRegistration registration = new MentorConnectRegistration(mentorConnectId, applicantId,
				LocalDateTime.now());
		mentorConnectregistrationsRepository.save(registration);

		mentorConnect.setRegistrationsCount(mentorConnect.getRegistrationsCount() + 1);
		repository.save(mentorConnect);

		String emailContent = "Dear " + applicant.getBasicDetails().getFirstName() + ",\n\n" +
				"We are pleased to confirm that you have successfully registered for the Mentor Connect session titled \"" 
				+ mentorConnect.getTitle() + "\".\n\n" +
				"Session Details:\n" +
				"Title: " + mentorConnect.getTitle() + "\n" +
				"Date: " + mentorConnect.getDate() + "\n" +
				"Time: " + mentorConnect.getStartTime() + "\n" +
				"Duration: " + mentorConnect.getDurationMinutes() + " minutes\n" +
				"Mentor: " + mentorConnect.getMentorName() + "\n" +
				"Meeting Link: " + mentorConnect.getMeetLink() + "\n\n" +
				"We kindly request you to join the session at the scheduled time using the meeting link provided above. " +
				"Please ensure that you have a stable internet connection and access to the meeting platform prior to the session.\n\n" +
				"Our team will be happy to assist you.\n\n" +
				"Warm regards,\n" +
				"The bitLabs Team";
		
		// send email notification to applicant
		emailService.sendMentorConnectRegistrationEmailToApplicant(mentorConnect.getTitle(), emailContent, applicant.getBasicDetails().getEmail());

		logger.info("service - registerMentorConnect - applicantId={} registered for mentorConnectId={}", applicantId,
				mentorConnectId);
		return "Applicant with ID: " + applicantId + " successfully registered for Mentor Connect ID: "
				+ mentorConnectId;
	}

	// get all registrations for a mentor connect
	// get all mentorConnectIds for an applicant
	public List<Long> getAllRegisteredMentorConnects(Long applicantId) {
		logger.info("Service - getAllRegisteredMentorConnects called with applicantId={}", applicantId);
		try {
			List<Long> mentorConnectIds = mentorConnectregistrationsRepository
					.findMentorConnectIdsByApplicantId(applicantId);

			if (mentorConnectIds.isEmpty()) {
				logger.warn("Service - no registrations found for Applicant ID={}", applicantId);
				throw new CustomException(
						"No registrations found for Applicant ID: " + applicantId,
						HttpStatus.NOT_FOUND);
			}

			logger.info("Service - found {} mentorConnectIds for Applicant ID={}",
					mentorConnectIds.size(), applicantId);

			return mentorConnectIds;
		} catch (Exception e) {
			logger.error("Service - error fetching mentorConnectIds for Applicant ID={}", applicantId, e);
			throw new CustomException(
					"Error fetching mentorConnectIds: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
