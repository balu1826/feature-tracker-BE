package com.talentstream.controller;

import com.talentstream.dto.AIPrepChatDTO;
import com.talentstream.dto.ChatTitleDTO;
import com.talentstream.service.AIPrepChatService;
import com.talentstream.repository.AIPrepChatRepository;
import com.talentstream.repository.RegisterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/aiPrepChat")
public class AIPrepChatController {
	private static final Logger logger = LoggerFactory.getLogger(AIPrepChatController.class);

	@Autowired
	private AIPrepChatService aiPrepChatService;

	@Autowired
	private RegisterRepository registerRepository;

	@Autowired
	private AIPrepChatRepository aiPrepChatRepository;

	@PostMapping("/saveChat")
	public ResponseEntity<?> saveChat(@RequestBody AIPrepChatDTO chatDTO) {
		logger.info("Saving chat for applicant: {}", chatDTO != null ? chatDTO.getApplicantId() : "null");
		try {
			if (chatDTO == null) {
				logger.warn("Received null chat DTO");
				return ResponseEntity.badRequest().body("Request body cannot be null");
			}
			if (chatDTO.getApplicantId() == null) {
				logger.warn("Missing applicantId in chat save request");
				return ResponseEntity.badRequest().body("applicantId is required");
			}
			if (!registerRepository.existsById(chatDTO.getApplicantId())) {
				logger.warn("Applicant not found with id: {}", chatDTO.getApplicantId());
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}
			if (chatDTO.getTitle() == null || chatDTO.getTitle().trim().isEmpty()) {
				logger.warn("Missing title in chat save request for applicant: {}", chatDTO.getApplicantId());
				return ResponseEntity.badRequest().body("title is required");
			}
			if (chatDTO.getSavedChat() == null || chatDTO.getSavedChat().trim().isEmpty()) {
				logger.warn("Empty chat content for applicant: {}", chatDTO.getApplicantId());
				return ResponseEntity.badRequest().body("chat is required");
			}

			AIPrepChatDTO savedChat = aiPrepChatService.saveChat(chatDTO);
			logger.info("Successfully saved chat with id: {} for applicant: {}", savedChat.getChatId(),
					savedChat.getApplicantId());
			return ResponseEntity.status(HttpStatus.CREATED).body(savedChat);

		} catch (Exception e) {
			logger.error("Error in saveChat: {}", e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getAllChatTitles/{applicantId}")
	public ResponseEntity<?> getChatTitles(@PathVariable Long applicantId) {
		logger.info("Fetching chat titles for applicant: {}", applicantId);
		try {
			if (applicantId == null) {
				logger.warn("Missing applicantId in getChatTitles request");
				return ResponseEntity.badRequest().body("applicantId is required");
			}
			if (!registerRepository.existsById(applicantId)) {
				logger.warn("Applicant not found with id: {}", applicantId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}

			List<ChatTitleDTO> titles = aiPrepChatService.getChatTitlesByApplicantId(applicantId);
			logger.debug("Found {} chat titles for applicant: {}", titles != null ? titles.size() : 0, applicantId);

			if (titles == null || titles.isEmpty()) {
				logger.info("No chat titles found for applicant: {}", applicantId);
				return ResponseEntity.ok("There are no saved chats");
			}
			return ResponseEntity.ok(titles);

		} catch (Exception e) {
			logger.error("Error fetching chat titles for applicant {}: {}", applicantId, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chat titles");
		}
	}

	@GetMapping("/{chatId}/getChatDetailsById/{applicantId}")
	public ResponseEntity<?> getChatById(@PathVariable Long chatId, @PathVariable Long applicantId) {
		logger.info("Fetching chat details for chatId: {}, applicantId: {}", chatId, applicantId);
		try {
			if (applicantId == null) {
				logger.warn("Missing applicantId in getChatById request");
				return ResponseEntity.badRequest().body("applicantId is required");
			}
			if (chatId == null) {
				logger.warn("Missing chatId in getChatById request");
				return ResponseEntity.badRequest().body("chatId is required");
			}
			if (!registerRepository.existsById(applicantId)) {
				logger.warn("Applicant not found with id: {}", applicantId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}
			if (!aiPrepChatRepository.existsById(chatId)) {
				logger.warn("Chat not found with id: {}", chatId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");
			}

			Optional<AIPrepChatDTO> chat = aiPrepChatService.getChatById(chatId, applicantId);
			if (chat.isPresent()) {
				logger.debug("Successfully retrieved chat with id: {} for applicant: {}", chatId, applicantId);
				return ResponseEntity.ok(chat.get());
			} else {
				logger.warn("Access denied - Chat {} does not belong to applicant {}", chatId, applicantId);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");
			}

		} catch (Exception e) {
			logger.error("Error fetching chat {} for applicant {}: {}", chatId, applicantId, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chat");
		}
	}

	@PutMapping("/{chatId}/updateChatDetails/{applicantId}")
	public ResponseEntity<?> updateChat(@PathVariable Long chatId, @PathVariable Long applicantId,
			@RequestBody AIPrepChatDTO chatDTO) {
		logger.info("Updating chat with id: {} for applicant: {}", chatId, applicantId);
		try {
			if (applicantId == null) {
				logger.warn("Missing applicantId in updateChat request");
				return ResponseEntity.badRequest().body("applicantId is required");
			}
			if (chatId == null) {
				logger.warn("Missing chatId in updateChat request");
				return ResponseEntity.badRequest().body("chatId is required");
			}
			if (!registerRepository.existsById(applicantId)) {
				logger.warn("Applicant not found with id: {}", applicantId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}
			if (!aiPrepChatRepository.existsById(chatId)) {
				logger.warn("Chat not found with id: {}", chatId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");
			}

			Optional<AIPrepChatDTO> updatedChat = aiPrepChatService.updateChat(chatId, applicantId, chatDTO);
			if (updatedChat.isPresent()) {
				logger.info("Successfully updated chat with id: {}", chatId);
				return ResponseEntity.ok(updatedChat.get());
			} else {
				logger.warn("Update failed - Chat {} does not belong to applicant {}", chatId, applicantId);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");
			}

		} catch (Exception e) {
			logger.error("Error updating chat {} for applicant {}: {}", chatId, applicantId, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update chat");
		}
	}

	@DeleteMapping("/{chatId}/deleteChat/{applicantId}")
	public ResponseEntity<?> deleteChat(@PathVariable Long chatId, @PathVariable Long applicantId) {
		logger.info("Deleting chat with id: {} for applicant: {}", chatId, applicantId);
		try {
			if (chatId == null || applicantId == null) {
				logger.warn("Missing parameters in deleteChat request - chatId: {}, applicantId: {}", chatId,
						applicantId);
				return ResponseEntity.badRequest().body("chatId and applicantId are required");
			}
			if (!registerRepository.existsById(applicantId)) {
				logger.warn("Applicant not found with id: {}", applicantId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}
			if (!aiPrepChatRepository.existsById(chatId)) {
				logger.warn("Chat not found with id: {}", chatId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat is not present");
			}

			boolean isDeleted = aiPrepChatService.deleteChat(chatId, applicantId);
			if (isDeleted) {
				logger.info("Successfully deleted chat with id: {}", chatId);
				return ResponseEntity.ok("Chat deleted successfully");
			} else {
				logger.warn("Delete failed - Chat {} does not belong to applicant {}", chatId, applicantId);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Chat does not belong to the applicant");
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting chat: " + e.getMessage());
		}
	}

	@GetMapping("/getAllChats/{applicantId}")
	public ResponseEntity<?> getAllChats(@PathVariable Long applicantId) {
		logger.info("Fetching all chats for applicant: {}", applicantId);
		try {
			if (applicantId == null) {
				logger.warn("Missing applicantId in getAllChats request");
				return ResponseEntity.badRequest().body("applicantId is required");
			}
			if (!registerRepository.existsById(applicantId)) {
				logger.warn("Applicant not found with id: {}", applicantId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No applicant is present with the given id");
			}

			List<AIPrepChatDTO> chats = aiPrepChatService.getAllChatsByApplicantId(applicantId);
			logger.debug("Found {} chats for applicant: {}", chats != null ? chats.size() : 0, applicantId);
			return ResponseEntity.ok(chats);

		} catch (Exception e) {
			logger.error("Error fetching all chats for applicant {}: {}", applicantId, e.getMessage(), e);
			logger.debug("Error details: {}", e.getStackTrace());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch chats");
		}
	}

	@PostMapping("/sendMessage")
	public ResponseEntity<?> sendMessage(@RequestParam(required = false) Long chatId, @RequestParam Long applicantId,
			@RequestBody Map<String, String> body) {
		logger.info("Processing message for chatId: {}, applicantId: {}", chatId, applicantId);
		try {
			String message = body.get("message");

			if (message == null || message.trim().isEmpty()) {
				logger.warn("Empty message received from applicant: {}", applicantId);
				return ResponseEntity.badRequest().body("Message cannot be empty");
			}

			logger.debug("Saving user message for chatId: {}", chatId);
			AIPrepChatDTO chat = aiPrepChatService.saveUserMessage(chatId, applicantId, message, "");

			// Get AI response (will integrate Groq later)
			logger.debug("Generating AI response for chatId: {}", chat.getChatId());
			String aiReply = "AI reply placeholder";

			// save bot reply
			logger.debug("Saving bot response for chatId: {}", chat.getChatId());
			aiPrepChatService.saveBotMessage(chat.getChatId(), applicantId, aiReply);

			Map<String, Object> response = new HashMap<>();
			response.put("chatId", chat.getChatId());
			response.put("reply", aiReply);

			logger.info("Successfully processed message for chatId: {}", chat.getChatId());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("Error in sendMessage for chatId: {}, applicantId: {} - {}", chatId, applicantId,
					e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/{chatId}/history/{applicantId}")
	public ResponseEntity<?> getHistory(@PathVariable Long chatId, @PathVariable Long applicantId) {
		logger.info("Fetching chat history for chatId: {}, applicantId: {}", chatId, applicantId);
		try {
			Object history = aiPrepChatService.getChatHistory(chatId, applicantId);
			logger.debug("Retrieved history for chatId: {} - Found {} items", chatId, history != null ? "some" : "no");
			return ResponseEntity.ok(history);
		} catch (Exception e) {
			logger.error("Error fetching history for chatId: {}, applicantId: {} - {}", chatId, applicantId,
					e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching history: " + e.getMessage());
		}
	}

	@GetMapping("/{chatId}/lastQuestion/{applicantId}")
	public ResponseEntity<?> getLastQuestion(@PathVariable Long chatId, @PathVariable Long applicantId) {
		logger.info("Fetching last question for chatId: {}, applicantId: {}", chatId, applicantId);
		try {
			Object lastQuestion = aiPrepChatService.getLastUserQuestion(chatId, applicantId);
			logger.debug("Retrieved last question for chatId: {} - {}", chatId,
					lastQuestion != null ? "found" : "not found");
			return ResponseEntity.ok(lastQuestion);
		} catch (Exception e) {
			logger.error("Error fetching last question for chatId: {}, applicantId: {} - {}", chatId, applicantId,
					e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching last question: " + e.getMessage());
		}
	}
}
