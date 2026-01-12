package com.talentstream.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talentstream.dto.VideoMetadataDto;
import com.talentstream.dto.VideoUploadRequestDTO;
import com.talentstream.entity.VideoLevel;
import com.talentstream.entity.VideoMetadata;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.VideoMetadataRepository;
import com.talentstream.service.FirebaseMessagingService;
import com.talentstream.service.NotificationMessageService;
import com.talentstream.service.S3Service;
import com.talentstream.service.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoUploadController {

	@Autowired
	private S3Service s3Service;

	@Autowired
	private VideoMetadataRepository repo;

	@Autowired
	private VideoService videoservice;

	@Autowired
	private ApplicantProfileRepository applicantProfileRepository;

	@Autowired
	private FirebaseMessagingService firebaseMessagingService;

	@Autowired
	private NotificationMessageService notificationMessageService;

	private static final Logger logger = LoggerFactory.getLogger(VideoUploadController.class);

	@PostMapping("/uploadVideo")
	public ResponseEntity<?> uploadVideo(@Valid VideoUploadRequestDTO request, BindingResult result) {
		logger.debug("uploadVideo - title={}, level={}, skill={}", request == null ? null : request.getTitle(),
				request == null ? null : request.getLevel(), request == null ? null : request.getSkillTag());

		if (result.hasErrors()) {
			StringBuilder errors = new StringBuilder();
			result.getFieldErrors().forEach(err -> errors.append(err.getField()).append(" - ")
					.append(err.getDefaultMessage()).append(System.lineSeparator()));
			return ResponseEntity.badRequest().body(errors.toString());
		}

		VideoLevel levelEnum;
		try {
			levelEnum = VideoLevel.valueOf(request.getLevel().toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(
					"Invalid level '" + request.getLevel() + "'. Allowed values: BEGINNER, INTERMEDIATE, ADVANCED");
		}

		ResponseEntity<?> validationResponse = validations(request.getFile(), request.getThumbnail());
		if (validationResponse != null)
			return validationResponse;

		try {
			String s3VideoUrl = s3Service.uploadVideo(request.getFile());
			String s3ThumbnailUrl = s3Service.uploadThumbnail(request.getThumbnail());

			String videoSkill = videoservice.resolveSkillOrDefault(request.getSkillTag());

			VideoMetadata video = new VideoMetadata(request.getTitle(), levelEnum, s3VideoUrl, s3ThumbnailUrl,
					videoSkill);

			VideoMetadata videometa = repo.save(video);

			firebaseMessagingService.sendNotificationToAll("Ready for a New Watch?",
					"Dive into your newly released video crafted just for you!");

			List<Long> applicantIds = applicantProfileRepository.findApplicantsBySkill(videoSkill);

			if (applicantIds.isEmpty()) {
				logger.warn("uploadVideo - no applicants found with skill={} so sending to all", videoSkill);
				applicantIds = applicantProfileRepository.findApplicantsByApplicantId();
			}

			if (applicantIds.isEmpty()) {
				logger.warn("uploadVideo - no applicants found to notify for video id={}", videometa.getVideoId());
				throw new CustomException("No applicants found to notify", HttpStatus.NOT_FOUND);
			}
			
			notificationMessageService.sendNotificationToApplicants(
					"A new video has been added for the concept: " + request.getTitle(), "Tech buzz shorts",
					videometa.getVideoId(), applicantIds);
			logger.info("uploadVideo - uploaded videoId={} title={}", videometa.getVideoId(),
					request == null ? null : request.getTitle());
			return ResponseEntity.ok("Video uploaded successfully");
		} catch (Exception e) {
			logger.error("uploadVideo - unexpected error: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
		}
	}

	private ResponseEntity<?> validations(MultipartFile file, MultipartFile thumbnail) {
		List<String> errors = new ArrayList<>();
		// File type validation
		if (!"video/mp4".equalsIgnoreCase(file.getContentType())) {
			errors.add("Only MP4 videos are allowed");
		}
		if (!("image/jpeg".equalsIgnoreCase(thumbnail.getContentType())
				|| "image/png".equalsIgnoreCase(thumbnail.getContentType()))) {
			errors.add("Thumbnail must be JPG or PNG");
		}

		if (!errors.isEmpty()) {
			logger.debug("validations - validation errors={}", errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}

		return null;
	}

	@GetMapping("/recommended/{applicantId}")
	public ResponseEntity<?> getRecommended(@PathVariable String applicantId) {
		logger.debug("getRecommended - applicantId={}", applicantId);
		try {
			Long id = Long.parseLong(applicantId);
			List<VideoMetadataDto> videos = videoservice.getRecommendedVideos(id);
			return ResponseEntity.ok(videos);
		} catch (NumberFormatException e) {
			logger.warn("getRecommended - invalid applicantId format: {}", applicantId);
			return ResponseEntity.badRequest().body("400 - Invalid Applicant Id :" + e.getMessage());
		} catch (Exception e) {
			logger.error("getRecommended - unexpected error for applicantId={}: {}", applicantId, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PutMapping("updateThumbnail/{videoId}")
	public ResponseEntity<String> uploadThumbnailForExistingVideo(@PathVariable Long videoId,
			@RequestPart("thumbnail") MultipartFile thumbnail) {
		logger.debug("uploadThumbnailForExistingVideo - videoId={}", videoId);
		if (thumbnail == null || thumbnail.isEmpty()) {
			return ResponseEntity.badRequest().body("Thumbnail is required.");
		}

		try {
			VideoMetadata video = repo.findById(videoId)
					.orElseThrow(() -> new RuntimeException("Video not found with ID: " + videoId));

			String oldThumbnailUrl = video.getThumbnailUrl();
			logger.debug("uploadThumbnailForExistingVideo - oldThumbnailUrl={}", oldThumbnailUrl);

			String s3ThumbnailUrl = s3Service.uploadThumbnail(thumbnail);

			video.setThumbnailUrl(s3ThumbnailUrl);
			repo.save(video);

			if (oldThumbnailUrl != null && !oldThumbnailUrl.isEmpty()) {
				logger.debug("uploadThumbnailForExistingVideo - deleting old thumbnail for videoId={}", videoId);
				s3Service.deleteFile(oldThumbnailUrl);
			}

			return ResponseEntity.ok("Thumbnail uploaded successfully. Thumbnail URL: " + s3ThumbnailUrl);
		} catch (Exception e) {
			logger.error("uploadThumbnailForExistingVideo - error for videoId={}: {}", videoId, e.getMessage(), e);
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchVideos(@RequestParam("q") String query) {
		logger.debug("searchVideos - query={}", query);
		try {
			String searchTerm = query.trim().replaceAll("\\s+", " ");

			if (searchTerm.length() < 2) {
				return ResponseEntity.badRequest()
						.body(Map.of("status", "error", "message", "Search term must be at least 2 characters long"));
			}

			List<VideoMetadata> videos = videoservice.searchVideos(searchTerm);

			return ResponseEntity.ok(Map.of("status", "success", "message", "Videos retrieved successfully", "count",
					videos.size(), "videos",
					videos.stream()
							.map(v -> Map.of("id", v.getVideoId(), "title", v.getTitle(), "thumbnailUrl",
									v.getThumbnailUrl(), "level", v.getLevel().name(), "skillTag", v.getSkillTag()))
							.collect(Collectors.toList())));

		} catch (CustomException e) {
			logger.warn("searchVideos - custom error: {}", e.getMessage());
			return ResponseEntity.status(e.getStatus()).body(Map.of("status", "error", "message", e.getMessage()));
		} catch (Exception e) {
			logger.error("searchVideos - unexpected error: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message",
					"An unexpected error occurred while searching videos", "error", e.getMessage()));
		}
	}
}
