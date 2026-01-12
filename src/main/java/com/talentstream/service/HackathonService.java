package com.talentstream.service;

import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.PARTICIPATION;
import static com.talentstream.util.ActivityConstantsUtils.ActivityDetail.WINNER;
import static com.talentstream.util.ActivityConstantsUtils.ActivityName.HACKATHON_SCORE;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.talentstream.dto.HackathonCreateRequestDTO;
import com.talentstream.dto.HackathonUpdateDTO;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.Hackathon;
import com.talentstream.entity.HackathonRegister;
import com.talentstream.entity.HackathonStatus;
import com.talentstream.entity.HackathonSubmit;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.HackathonRegisterRepository;
import com.talentstream.repository.HackathonRepository;
import com.talentstream.repository.JobRecruiterRepository;

@Service
public class HackathonService {

	@Autowired
	private JobRecruiterRepository recruiterRepo;

	@Autowired
	private ApplicantProfileRepository profileRepository;

	@Autowired
	private HackathonRegisterRepository registrationRepo;

	@Autowired
	private ApplicantKeySkillsService applicantKeySkillsService;

	@Autowired
	public ApplicantRepository appRepo;

	@Autowired
	private NotificationMessageService notificationMessageService;
	
	private final HackathonRepository repo;

	@Autowired
	private ApplicantScoreService applicantScoreService;

	@Autowired
	private HackathonSubmitService hackathonSubmitService;

	@Autowired
	private FirebaseMessagingService firebaseMessagingService;
	
    @Autowired
    private ApplicantProfileRepository applicantProfileRepository;

	private static final Logger logger = LoggerFactory.getLogger(HackathonService.class);

	public HackathonService(HackathonRepository repo) {
		this.repo = repo;
	}

	public List<Hackathon> getRecommendedHackathons(Long applicantId) {

				logger.debug("getRecommendedHackathons - applicantId={}", applicantId);

				ApplicantProfile profile = profileRepository.findByApplicantId(applicantId);
		if (profile == null) {
			throw new IllegalArgumentException("Applicant not found with id: " + applicantId);
		}
		List<String> applicantSkills = applicantKeySkillsService.getSkills(applicantId).stream()
				.map(s -> s.trim().toLowerCase()).collect(Collectors.toList());

		List<Hackathon> allHackathons = repo.findAll();

		List<Hackathon> recommended = allHackathons.stream()
				.filter(h -> h.getStatus() == HackathonStatus.ACTIVE || h.getStatus() == HackathonStatus.UPCOMING)
				.filter(h -> Arrays.stream(h.getAllowedTechnologies().split(",")).map(String::trim)
						.map(String::toLowerCase).anyMatch(applicantSkills::contains))
				.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());

		return recommended;
	}

	public List<Hackathon> getActiveHackathons() {
		logger.debug("getActiveHackathons - fetching active hackathons");
		List<Hackathon> allHackathons = repo.findAll();

		return allHackathons.stream().filter(h -> h.getStatus() == HackathonStatus.ACTIVE)
			.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	public List<Hackathon> getUpcomingHackathons() {
		logger.debug("getUpcomingHackathons - fetching upcoming hackathons");
		List<Hackathon> allHackathons = repo.findAll();

		return allHackathons.stream().filter(h -> h.getStatus() == HackathonStatus.UPCOMING)
			.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	public List<Hackathon> getCompletedHackathons() {
		logger.debug("getCompletedHackathons - fetching completed hackathons");
		List<Hackathon> allHackathons = repo.findAll();

		return allHackathons.stream().filter(h -> h.getStatus() == HackathonStatus.COMPLETED)
			.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	public Hackathon createHackathon(HackathonCreateRequestDTO r) {
		logger.debug("createHackathon - recruiterId={}, title={}", r == null ? null : r.getRecruiterId(), r == null ? null : r.getTitle());
		if (!recruiterRepo.existsById(r.getRecruiterId())) {
			logger.warn("createHackathon - recruiter not found id={}", r.getRecruiterId());
			throw new EntityNotFoundException("Recruiter not found with id: " + r.getRecruiterId());
		}

		LocalDate today = LocalDate.now();

		if (r.getStartAt().isBefore(today)) {
			throw new IllegalArgumentException("Start date must be greater than today's date");
		}
		if (!r.getEndAt().isAfter(r.getStartAt())) {
			throw new IllegalArgumentException("End date must be greater than start date");
		}

		Hackathon h = new Hackathon();
		h.setRecruiterId(r.getRecruiterId());
		h.setTitle(r.getTitle());
		h.setDescription(r.getDescription());
		h.setBannerUrl(r.getBannerUrl());
		h.setStartAt(r.getStartAt());
		h.setEndAt(r.getEndAt());
		h.setInstructions(r.getInstructions());
		h.setEligibility(r.getEligibility());
		h.setAllowedTechnologies(r.getAllowedTechnologies());
		h.setCompany(r.getCompany());
		h.setCreatedAt(LocalDateTime.now().plus(Duration.ofMinutes(330)));
		Hackathon hack = repo.save(h);

		logger.info("createHackathon - hackathon created id={}, title={}", hack.getId(), hack.getTitle());

		firebaseMessagingService.sendNotificationToAll("Challenge alert!",
				"A brand-new challenge is here — show the world what you can build!");

		List<Long> applicantIds = applicantProfileRepository.findApplicantsByApplicantId();
		if (applicantIds.isEmpty()) {
			logger.warn("createHackathon - no applicants found to notify for hackathon id={}", hack.getId());
			throw new CustomException("No applicants found to notify", HttpStatus.NOT_FOUND);
		}
		notificationMessageService.sendNotificationToApplicants("A new hackathon opportunity has been posted: " + h.getTitle(), "hackathon", hack.getId(), applicantIds);

		return hack;
	}

	public List<Hackathon> getAllByCreaterId(Long id) {
		logger.debug("getAllByCreaterId - recruiterId={}", id);
		return repo.findByRecruiterId(id).get();
	}

	public List<Hackathon> getAll() {
		logger.debug("getAll - fetching active/upcoming hackathons");
		List<Hackathon> allHackathons = repo.findAll();

		return allHackathons.stream()
			.filter(h -> h.getStatus() == HackathonStatus.ACTIVE || h.getStatus() == HackathonStatus.UPCOMING)
			.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	public Optional<Hackathon> get(Long hackId, Long userId) {
		logger.debug("get - hackId={}, userId={}", hackId, userId);
		boolean applicantExists = appRepo.existsById(userId);
		boolean recruiterExists = recruiterRepo.existsById(userId);

		if (!applicantExists && !recruiterExists) {
			logger.warn("get - user not found id={}", userId);
			throw new EntityNotFoundException("candidate or recruiter is not found with the id : " + userId);
		}

		return repo.findById(hackId);
	}

	public Hackathon updateHackathon(Long id, HackathonUpdateDTO r) {
		logger.debug("updateHackathon - id={}, recruiterId={}", id, r == null ? null : r.getRecruiterId());
		Hackathon existing = repo.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("No hackathon found with id: " + id));

		if (!recruiterRepo.existsById(r.getRecruiterId())) {
			throw new EntityNotFoundException("Recruiter not found with id: " + r.getRecruiterId());
		}

		LocalDate today = LocalDate.now();

		if (r.getStartAt() != null && r.getStartAt().isBefore(today)) {
			throw new IllegalArgumentException("Start date must be greater than today's date");
		}
		if (r.getEndAt() != null && !r.getEndAt().isAfter(r.getStartAt())) {
			throw new IllegalArgumentException("End date must be greater than start date");
		}

		if (r.getTitle() != null)
			existing.setTitle(r.getTitle());
		if (r.getDescription() != null)
			existing.setDescription(r.getDescription());
		if (r.getBannerUrl() != null)
			existing.setBannerUrl(r.getBannerUrl());
		if (r.getStartAt() != null)
			existing.setStartAt(r.getStartAt());
		if (r.getEndAt() != null)
			existing.setEndAt(r.getEndAt());
		if (r.getInstructions() != null)
			existing.setInstructions(r.getInstructions());
		if (r.getEligibility() != null)
			existing.setEligibility(r.getEligibility());
		if (r.getAllowedTechnologies() != null)
			existing.setAllowedTechnologies(r.getAllowedTechnologies());
		if (r.getCompany() != null)
			existing.setCompany(r.getCompany());

		Hackathon saved = repo.save(existing);
		logger.info("updateHackathon - updated hackathon id={}", saved.getId());
		return saved;
	}

	@Transactional
	public boolean delete(Long id) {
		logger.debug("delete - hackathonId={}", id);
		if (repo.existsById(id)) {
			registrationRepo.deleteByHackathonId(id);
			repo.deleteById(id);
			logger.info("delete - deleted hackathon id={}", id);
			return true;
		}
		logger.warn("delete - hackathon not found id={}", id);
		return false;
	}

	public Hackathon declareWinner(Long hackathonId, Long winnerId) {
		logger.debug("declareWinner - hackathonId={}, winnerId={}", hackathonId, winnerId);
		if (!appRepo.existsById(winnerId)) {
			logger.warn("declareWinner - winner not found id={}", winnerId);
			throw new EntityNotFoundException("Applicant not found with id: " + winnerId);
		}

		Hackathon hackathon = repo.findById(hackathonId)
				.orElseThrow(() -> new EntityNotFoundException("Hackathon not found with id: " + hackathonId));

		if (hackathon.getWinner() != null) {
			logger.warn("declareWinner - winner already declared for hackathonId={}", hackathonId);
			throw new IllegalArgumentException("Winner has already been declared for this hackathon");
		}

		HackathonRegister register = registrationRepo.findByHackathonIdAndUserId(hackathonId, winnerId)
				.orElseThrow(() -> new EntityNotFoundException(
						"The applicant did not register for the hackathon with id: " + hackathonId));

		if (register.isSubmitStatus() == false) {
			logger.warn("declareWinner - winner did not submit hackathonId={}, winnerId={}", hackathonId, winnerId);
			throw new IllegalArgumentException("Applicant did not submitted the response");
		}
		if (hackathon.getStatus() == HackathonStatus.COMPLETED) {
			hackathon.setWinner(winnerId);
		} else {
			logger.warn("declareWinner - hackathon not completed hackathonId={}", hackathonId);
			throw new IllegalArgumentException("Hackathon has not completed yet");
		}

		Hackathon saved = repo.save(hackathon);
		logger.info("declareWinner - declared winnerId={} for hackathonId={}", winnerId, saved.getId());
		return saved;
	}

	public List<Hackathon> getRegisteredHackathons(Long applicantId) {
		logger.debug("getRegisteredHackathons - applicantId={}", applicantId);
		if (!appRepo.existsById(applicantId)) {
			logger.warn("getRegisteredHackathons - applicant not found id={}", applicantId);
			throw new IllegalArgumentException("Applicant not found with id: " + applicantId);
		}

		List<HackathonRegister> registrations = registrationRepo.findByUserId(applicantId);

		return registrations.stream().map((HackathonRegister r) -> repo.findById(r.getHackathonId()))
				.filter((Optional<Hackathon> o) -> o.isPresent()).map((Optional<Hackathon> o) -> o.get())
				.sorted(Comparator.comparing(Hackathon::getCreatedAt).reversed()).collect(Collectors.toList());
	}

	public Long getWinCountByUser(Long winnerId) {
		logger.debug("getWinCountByUser - winnerId={}", winnerId);
		if (!appRepo.existsById(winnerId)) {
			logger.warn("getWinCountByUser - applicant not found id={}", winnerId);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found with id: " + winnerId);
		}
		try {
			Long count = repo.countByWinnerId(winnerId);
			logger.debug("getWinCountByUser - winnerId={}, count={}", winnerId, count);
			return count;
		} catch (Exception e) {
			logger.error("getWinCountByUser - error fetching count for winnerId={}: {}", winnerId, e.getMessage(), e);
			throw new RuntimeException("Error fetching win count for winnerId: " + winnerId, e);
		}
	}

	@Async
	public String addPointsToWinnerAndParticipants(Long hackathonId, Long winnerId) {
		logger.debug("addPointsToWinnerAndParticipants - hackathonId={}, winnerId={}", hackathonId, winnerId);
		try {
			List<HackathonSubmit> submissions = hackathonSubmitService.listByHackathon(hackathonId);
			for (HackathonSubmit reg : submissions) {
				if (reg.getUserId().equals(winnerId)) {
					applicantScoreService.updateApplicantScore(reg.getUserId(), HACKATHON_SCORE, WINNER);
				} else {
					applicantScoreService.updateApplicantScore(reg.getUserId(), HACKATHON_SCORE, PARTICIPATION);
				}
			}
			logger.info("addPointsToWinnerAndParticipants - points added for hackathonId={}", hackathonId);
			return "Points added successfully to winner and participants.";
		} catch (Exception e) {
			logger.error("addPointsToWinnerAndParticipants - error adding points hackathonId={}: {}", hackathonId, e.getMessage(), e);
			throw new RuntimeException("Error adding points to winner and participants: " + e.getMessage(), e);
		}
	}
}
