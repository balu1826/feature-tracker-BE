package com.talentstream.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.NotificationDto;
import com.talentstream.entity.UserFcmTokens;
import com.talentstream.exception.CustomException;
import com.talentstream.service.FirebaseMessagingService;

@RestController
@RequestMapping("/notification")
public class FireBaseController {

	@Autowired
	private FirebaseMessagingService firebaseMessagingService;

	private static final Logger logger = LoggerFactory.getLogger(FireBaseController.class);

	@PostMapping("/saveFcmToken/{id}")
	public ResponseEntity<?> saveFcmToken(@Valid @RequestBody UserFcmTokens fcmTokens, BindingResult bindingResult,
			@PathVariable Long id) {
		logger.debug("saveFcmToken - entry userId={}", id);
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(err -> {
				errors.put(err.getField(), err.getDefaultMessage());
			});
			logger.warn("saveFcmToken - validation failed userId={}: {}", id, errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		try {

			firebaseMessagingService.saveFcmToken(fcmTokens, id);
			logger.info("saveFcmToken - FCM token saved successfully userId={}", id);
			return ResponseEntity.status(HttpStatus.OK).body("Fcm Token Saved Successfully");
		} catch (CustomException e) {
			logger.error("saveFcmToken - CustomException userId={}: {}", id, e.getMessage());
			return ResponseEntity.status(e.getStatus()).body(e.getMessage());
		} catch (Exception e) {
			logger.error("saveFcmToken - unexpected error userId={}: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@PostMapping("/send/{id}")
	public ResponseEntity<?> sendNotification(@Valid @RequestBody NotificationDto dto, BindingResult bindingResult,
			@PathVariable Long id) {
		logger.debug("sendNotification - entry userId={}, title={}", id, dto == null ? null : dto.getTitle());
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(err -> {
				errors.put(err.getField(), err.getDefaultMessage());
			});
			logger.warn("sendNotification - validation failed userId={}: {}", id, errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}

		try {
			String sendNotification = firebaseMessagingService.sendNotification(id, dto.getTitle(), dto.getBody());
			logger.debug("sendNotification - response: {}", sendNotification);
			if (sendNotification.isBlank()) {
				logger.warn("sendNotification - token invalid or expired userId={}", id);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Token is not valid or expired Please try again with correct FCM Token");
			}
			logger.info("sendNotification - notification sent successfully userId={}", id);
			return ResponseEntity.status(HttpStatus.OK).body("Notification sent succefully");
		} catch (CustomException e) {
			logger.error("sendNotification - CustomException userId={}: {}", id, e.getMessage());
			return ResponseEntity.status(e.getStatus()).body(e.getMessage());
		} catch (Exception e) {
			logger.error("sendNotification - unexpected error userId={}: {}", id, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@PostMapping("/sendToAll")
	public ResponseEntity<?> sendNotificationToAll(@Valid @RequestBody NotificationDto dto,
	                                               BindingResult bindingResult) {
	    logger.debug("sendNotificationToAll - entry title={}", dto == null ? null : dto.getTitle());
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        bindingResult.getFieldErrors().forEach(err ->
	            errors.put(err.getField(), err.getDefaultMessage())
	        );
	        logger.warn("sendNotificationToAll - validation failed: {}", errors);
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	    }

	    try {
	        // Fire-and-forget async call
	        firebaseMessagingService.sendNotificationToAll(dto.getTitle(), dto.getBody());
	        logger.info("sendNotificationToAll - notification request queued for async processing");

	        return ResponseEntity.ok("Notification request accepted and processing asynchronously");

	    } catch (Exception e) {
	        logger.error("sendNotificationToAll - error: {}", e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Internal Server Error");
	    }
	}

	@GetMapping("/getFcmTokenDetails/{fcmToken}")
	public ResponseEntity<?> getFcmToken(@PathVariable String fcmToken) {
		logger.debug("getFcmToken - entry fcmToken={}", fcmToken);
		try {
			UserFcmTokens token = firebaseMessagingService.getToken(fcmToken);
			logger.debug("getFcmToken - token found for fcmToken={}", fcmToken);
			return ResponseEntity.ok(token);

		} catch (NoSuchElementException e) {
			logger.warn("getFcmToken - FCM token not found: {}", fcmToken);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FCM token not found");
		} catch (Exception e) {
			logger.error("getFcmToken - error fetching FCM token {}: {}", fcmToken, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while fetching FCM token");
		}
	}

	@PutMapping("/mute/{fcmToken}")
	public ResponseEntity<?> mute(@PathVariable String fcmToken) {
		logger.debug("mute - entry fcmToken={}", fcmToken);
		try {
			String message = firebaseMessagingService.muteToken(fcmToken);
			logger.info("mute - token muted fcmToken={}", fcmToken);
			return ResponseEntity.ok(message);

		} catch (IllegalStateException ex) {
			logger.warn("mute - illegal state fcmToken={}: {}", fcmToken, ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

		} catch (RuntimeException ex) {
			logger.warn("mute - runtime error fcmToken={}: {}", fcmToken, ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

		} catch (Exception ex) {
			logger.error("mute - error muting token fcmToken={}: {}", fcmToken, ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while muting the token");
		}
	}

	@PutMapping("/unmute/{fcmToken}")
	public ResponseEntity<?> unmute(@PathVariable String fcmToken) {
		logger.debug("unmute - entry fcmToken={}", fcmToken);
		try {
			String message = firebaseMessagingService.unmuteToken(fcmToken);
			logger.info("unmute - token unmuted fcmToken={}", fcmToken);
			return ResponseEntity.ok(message);

		} catch (IllegalStateException ex) {
			logger.warn("unmute - illegal state fcmToken={}: {}", fcmToken, ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

		} catch (RuntimeException ex) {
			logger.warn("unmute - runtime error fcmToken={}: {}", fcmToken, ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());

		} catch (Exception ex) {
			logger.error("unmute - error unmuting token fcmToken={}: {}", fcmToken, ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Something went wrong while unmuting the token");
		}
	}

}