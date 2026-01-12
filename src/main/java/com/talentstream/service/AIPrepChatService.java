package com.talentstream.service;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.talentstream.dto.AIPrepChatDTO;
import com.talentstream.dto.AIInterviewPrepBotDTO;
import com.talentstream.dto.ChatTitleDTO;
import com.talentstream.entity.AIPrepChat;
import com.talentstream.repository.AIPrepChatRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;

import java.time.LocalDateTime;

import java.util.*;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service

public class AIPrepChatService {
	private static final Logger logger = LoggerFactory.getLogger(AIPrepChatService.class);

	@Autowired

	private AIPrepChatRepository aiPrepChatRepository;

	private final Gson gson = new Gson();

	private final Type chatListType = new TypeToken<List<Map<String, String>>>() {
	}.getType();

	@Transactional
	public AIPrepChatDTO saveChat(AIPrepChatDTO chatDTO) {
		logger.info("Saving new chat for applicant: {}", chatDTO.getApplicantId());

		try {
			AIPrepChat chat = new AIPrepChat();
			chat.setApplicantId(chatDTO.getApplicantId());
			chat.setTitle(chatDTO.getTitle());
			chat.setSavedChat(chatDTO.getSavedChat());
			chat.setCreatedAt(LocalDateTime.now());

			AIPrepChat savedChat = aiPrepChatRepository.save(chat);
			logger.debug("Successfully saved chat with ID: {} for applicant: {}", savedChat.getChatId(),
					chatDTO.getApplicantId());

			return convertToDTO(savedChat);
		} catch (Exception e) {
			logger.error("Error saving chat for applicant: {}. Error: {}", chatDTO.getApplicantId(), e.getMessage(), e);
			throw e;
		}

	}

	public Optional<AIPrepChatDTO> getChatById(Long chatId, Long applicantId) {
		logger.debug("Fetching chat with ID: {} for applicant: {}", chatId, applicantId);

		try {
			Optional<AIPrepChatDTO> result = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId)
					.map(this::convertToDTO);

			if (result.isEmpty()) {
				logger.warn("No chat found with ID: {} for applicant: {}", chatId, applicantId);
			} else {
				logger.debug("Successfully retrieved chat with ID: {} for applicant: {}", chatId, applicantId);
			}

			return result;
		} catch (Exception e) {
			logger.error("Error fetching chat with ID: {} for applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	@Transactional
	public Optional<AIPrepChatDTO> updateChat(Long chatId, Long applicantId, AIPrepChatDTO chatDTO) {
		logger.info("Updating chat with ID: {} for applicant: {}", chatId, applicantId);

		try {
			Optional<AIPrepChat> existing = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId);

			if (existing.isPresent()) {
				AIPrepChat chat = existing.get();
				logger.debug("Found existing chat with ID: {}", chatId);

				if (chatDTO.getTitle() != null) {
					logger.debug("Updating title for chat ID: {}", chatId);
					chat.setTitle(chatDTO.getTitle());
				}

				if (chatDTO.getSavedChat() != null) {
					logger.debug("Updating saved chat content for chat ID: {}", chatId);
					chat.setSavedChat(chatDTO.getSavedChat());
				}

				chat.setUpdatedAt(LocalDateTime.now());
				AIPrepChat updatedChat = aiPrepChatRepository.save(chat);
				logger.info("Successfully updated chat with ID: {}", chatId);

				return Optional.of(convertToDTO(updatedChat));
			}

			logger.warn("No chat found with ID: {} for applicant: {} to update", chatId, applicantId);
			return Optional.empty();
		} catch (Exception e) {
			logger.error("Error updating chat with ID: {} for applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	@Transactional
	public AIPrepChatDTO saveUserMessage(Long chatId, Long applicantId, String userMessage, String title) {
		logger.info("Saving user message for chat ID: {}, applicant: {}", chatId, applicantId);

		try {
			AIPrepChat chat;

			if (chatId != null) {
				logger.debug("Fetching existing chat with ID: {}", chatId);
				chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId).orElseThrow(() -> {
					String errorMsg = String.format("Chat not found with ID: %d for applicant: %d", chatId,
							applicantId);
					logger.error(errorMsg);
					return new RuntimeException(errorMsg);
				});
			} else {
				logger.debug("Creating new chat for applicant: {}", applicantId);
				chat = new AIPrepChat(applicantId, "New Chat", "[]");
				chat = aiPrepChatRepository.save(chat);
				logger.info("Created new chat with ID: {} for applicant: {}", chat.getChatId(), applicantId);
			}

			logger.debug("Appending user message to chat ID: {}", chat.getChatId());
			appendMessage(chat, "user", userMessage);

			if (chat.getTitle().equals("New Chat") && title != null && !title.trim().isEmpty()) {
				logger.debug("Updating chat title to: {}", title);
				chat.setTitle(title);
			}

			AIPrepChat savedChat = aiPrepChatRepository.save(chat);
			logger.debug("Successfully saved user message to chat ID: {}", savedChat.getChatId());

			return convertToDTO(savedChat);
		} catch (Exception e) {
			logger.error("Error saving user message for chat ID: {}, applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	@Transactional
	public void saveBotMessage(Long chatId, Long applicantId, String botMessage) {
		logger.debug("Saving bot message for chat ID: {}, applicant: {}", chatId, applicantId);

		try {
			AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId).orElseThrow(() -> {
				String errorMsg = String.format("Chat not found with ID: %d for applicant: %d", chatId, applicantId);
				logger.error(errorMsg);
				return new RuntimeException(errorMsg);
			});

			logger.debug("Appending bot message to chat ID: {}", chatId);
			appendMessage(chat, "assistant", botMessage);

			aiPrepChatRepository.save(chat);
			logger.debug("Successfully saved bot message to chat ID: {}", chatId);
		} catch (Exception e) {
			logger.error("Error saving bot message for chat ID: {}, applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	private void appendMessage(AIPrepChat chat, String role, String content) {
		logger.trace("Appending {} message to chat ID: {}", role, chat.getChatId());

		try {
			List<Map<String, String>> history = getHistory(chat);

			Map<String, String> entry = new LinkedHashMap<>();
			entry.put("role", role);
			entry.put("message", content);
			entry.put("time", LocalDateTime.now().toString());

			history.add(entry);
			chat.setSavedChat(gson.toJson(history));

			logger.trace("Successfully appended message to chat ID: {}", chat.getChatId());
		} catch (Exception e) {
			logger.error("Error appending message to chat ID: {}. Error: {}", chat.getChatId(), e.getMessage(), e);
			throw e;
		}

	}

	private List<Map<String, String>> getHistory(AIPrepChat chat) {
		logger.trace("Getting chat history for chat ID: {}", chat.getChatId());

		try {
			if (chat.getSavedChat() == null || chat.getSavedChat().isEmpty()) {
				logger.trace("No saved chat history found for chat ID: {}, returning empty list", chat.getChatId());
				return new ArrayList<>();
			}

			List<Map<String, String>> history = gson.fromJson(chat.getSavedChat(), chatListType);
			logger.trace("Retrieved {} messages from chat history for chat ID: {}", history.size(), chat.getChatId());
			return history;
		} catch (Exception e) {
			logger.error("Error getting history for chat ID: {}. Error: {}", chat.getChatId(), e.getMessage(), e);
			throw e;
		}

	}

	public String getLastUserQuestion(Long chatId, Long applicantId) {
		logger.debug("Getting last user question for chat ID: {}, applicant: {}", chatId, applicantId);

		try {
			AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId).orElseThrow(() -> {
				String errorMsg = String.format("Chat not found with ID: %d for applicant: %d", chatId, applicantId);
				logger.error(errorMsg);
				return new RuntimeException(errorMsg);
			});

			List<Map<String, String>> history = getHistory(chat);
			logger.trace("Searching through {} messages for last user question", history.size());

			for (int i = history.size() - 1; i >= 0; i--) {
				if ("user".equals(history.get(i).get("role"))) {
					String message = history.get(i).get("message");
					logger.debug("Found last user question in chat ID: {}: {}", chatId,
							message.length() > 50 ? message.substring(0, 50) + "..." : message);
					return message;
				}
			}

			logger.debug("No user questions found in chat ID: {}", chatId);
			return null;
		} catch (Exception e) {
			logger.error("Error getting last user question for chat ID: {}, applicant: {}. Error: {}", chatId,
					applicantId, e.getMessage(), e);
			throw e;
		}

	}

	public List<Map<String, String>> getChatHistory(Long chatId, Long applicantId) {
		logger.debug("Getting chat history for chat ID: {}, applicant: {}", chatId, applicantId);

		try {
			AIPrepChat chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId).orElseThrow(() -> {
				String errorMsg = String.format("Chat not found with ID: %d for applicant: %d", chatId, applicantId);
				logger.error(errorMsg);
				return new RuntimeException(errorMsg);
			});

			List<Map<String, String>> history = getHistory(chat);
			logger.debug("Retrieved {} messages from chat history for chat ID: {}", history.size(), chatId);
			return history;
		} catch (Exception e) {
			logger.error("Error getting chat history for chat ID: {}, applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	public List<ChatTitleDTO> getChatTitlesByApplicantId(Long applicantId) {
		logger.debug("Getting chat titles for applicant: {}", applicantId);

		try {
			List<ChatTitleDTO> titles = aiPrepChatRepository.findChatTitlesByApplicantId(applicantId);
			logger.debug("Found {} chat titles for applicant: {}", titles.size(), applicantId);
			return titles;
		} catch (Exception e) {
			logger.error("Error getting chat titles for applicant: {}. Error: {}", applicantId, e.getMessage(), e);
			throw e;
		}

	}

	@Transactional
	public boolean deleteChat(Long chatId, Long applicantId) {
		logger.info("Deleting chat with ID: {} for applicant: {}", chatId, applicantId);

		try {
			Optional<AIPrepChat> chat = aiPrepChatRepository.findByChatIdAndApplicantId(chatId, applicantId);

			if (chat.isPresent()) {
				aiPrepChatRepository.deleteByChatIdAndApplicantId(chatId, applicantId);
				logger.info("Successfully deleted chat with ID: {} for applicant: {}", chatId, applicantId);
				return true;
			}

			logger.warn("No chat found with ID: {} for applicant: {} to delete", chatId, applicantId);
			return false;
		} catch (Exception e) {
			logger.error("Error deleting chat with ID: {} for applicant: {}. Error: {}", chatId, applicantId,
					e.getMessage(), e);
			throw e;
		}

	}

	public List<AIPrepChatDTO> getAllChatsByApplicantId(Long applicantId) {
		logger.debug("Getting all chats for applicant: {}", applicantId);

		try {
			List<AIPrepChatDTO> chats = aiPrepChatRepository.findByApplicantIdOrderByCreatedAtDesc(applicantId).stream()
					.map(this::convertToDTO).collect(Collectors.toList());

			logger.debug("Found {} chats for applicant: {}", chats.size(), applicantId);
			return chats;
		} catch (Exception e) {
			logger.error("Error getting all chats for applicant: {}. Error: {}", applicantId, e.getMessage(), e);
			throw e;
		}

	}

	private AIPrepChatDTO convertToDTO(AIPrepChat chat) {
		logger.trace("Converting AIPrepChat to DTO for chat ID: {}", chat.getChatId());

		try {
			AIPrepChatDTO dto = new AIPrepChatDTO(chat.getChatId(), chat.getApplicantId(), chat.getTitle(),
					chat.getSavedChat(), chat.getCreatedAt(), chat.getUpdatedAt());

			logger.trace("Successfully converted AIPrepChat to DTO for chat ID: {}", chat.getChatId());
			return dto;
		} catch (Exception e) {
			logger.error("Error converting AIPrepChat to DTO for chat ID: {}. Error: {}", chat.getChatId(),
					e.getMessage(), e);
			throw e;
		}
	}

	public Map<String, Object> getApplicantProfile(AIInterviewPrepBotDTO dto) {
		logger.debug("Building applicant profile from DTO");

		try {
			Map<String, Object> profile = new LinkedHashMap<>();

			logger.trace("Adding basic details to profile");
			profile.put("basicDetails", dto.getBasicDetails());
			profile.put("skillsRequired", dto.getSkillsRequired());
			profile.put("experience", dto.getExperience());
			profile.put("experienceDetails", dto.getExperienceDetails());
			profile.put("qualification", dto.getQualification());
			profile.put("specialization", dto.getSpecialization());
			profile.put("preferredJobLocations", dto.getPreferredJobLocations());
			profile.put("roles", dto.getRoles());

			logger.debug("Successfully built applicant profile with {} entries", profile.size());
			return profile;
		} catch (Exception e) {
			logger.error("Error building applicant profile. Error: {}", e.getMessage(), e);
			throw e;
		}

	}

}
