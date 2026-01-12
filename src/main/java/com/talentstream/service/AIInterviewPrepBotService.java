package com.talentstream.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.talentstream.dto.AIInterviewPrepBotDTO;
import com.talentstream.dto.AIPrepChatDTO;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantSkills;
import com.talentstream.repository.AIPrepChatRepository;
import com.talentstream.repository.ApplicantProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestClientException;

import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class AIInterviewPrepBotService {
	private static final Logger logger = LoggerFactory.getLogger(AIInterviewPrepBotService.class);
	@Autowired

	private RestTemplate restTemplate;
	@Autowired

	private AIPrepChatService aiPrepChatService;
	@Autowired
	private ApplicantProfileRepository applicantProfileRepository;

	@Autowired
	private AIPrepChatRepository aiPrepChatRepository;

	private String groqApiKey = "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";

	private String groqApiUrl = "https://api.groq.com/openai/v1/chat/completions";

	private String groqModel = "llama-3.1-8b-instant";

	private String generateAITitle(String userMessage, Long applicantId) {
		logger.debug("Generating AI title for message: {}", userMessage);
		try {
			// Fetch existing chat titles to avoid duplication
			List<String> existingTitles = aiPrepChatRepository.findLatest20ChatTitlesByApplicantId(applicantId);
			logger.debug("Found {} existing titles for uniqueness check", existingTitles.size());
			
			String systemPrompt = "You generate short, fun, and human-friendly chat titles.\n"
					+ "For greetings (hello, hi, hey, etc.), generate a neutral, playful title each time.\n"
					+ "Do NOT include time references (morning, evening, night) or assume context.\n"
					+ "Do NOT reuse the same greeting title twice in a row.\n"
					+ "For questions, problems, or statements, generate a clear, concise, and engaging title.\n"
					+ "Always be creative and natural.\n\n" + "Examples:\n" + "User: \"hello\"\n"
					+ "Title: A Friendly Wave\n\n" + "User: \"hi\"\n" + "Title: Quick Hello\n\n" + "User: \"hey\"\n"
					+ "Title: Warm Greeting\n\n" + "User: \"My React page overflows on small screens\"\n"
					+ "Title: Fix React Layout\n\n" + "User: \"How do I delete my last git commit\"\n"
					+ "Title: Undo Git Commit\n\n" + "Rules:\n" + "- Return ONLY ONE title\n"
					+ "- Make it short, engaging, and human-friendly\n" + "- Max 30 characters\n"
					+ "- No punctuation at the end\n" + "- No quotes or formatting\n"
					+ "- Title should reflect the essence of the user's input\n"
					+ "- For greetings, always invent a neutral playful title each time without time references"
					+ "\n\nIMPORTANT: Do NOT use any of these existing titles: " + String.join(", ", existingTitles);
			

			JsonArray messages = new JsonArray();

			JsonObject systemMsg = new JsonObject();
			systemMsg.addProperty("role", "system");
			systemMsg.addProperty("content", systemPrompt);
			messages.add(systemMsg);

			JsonObject userMsg = new JsonObject();
			userMsg.addProperty("role", "user");
			userMsg.addProperty("content", "Generate a title for: " + userMessage);
			messages.add(userMsg);

			JsonObject requestBody = new JsonObject();
			requestBody.addProperty("model", groqModel);
			requestBody.add("messages", messages);
			requestBody.addProperty("temperature", 0.3);
			requestBody.addProperty("max_tokens", 20);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + groqApiKey);

			HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
			ResponseEntity<String> response = restTemplate.exchange(groqApiUrl, HttpMethod.POST, entity, String.class);

			JsonObject jsonResponse = JsonParser.parseString(response.getBody()).getAsJsonObject();
			String title = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message")
					.get("content").getAsString().trim();

			if (title.length() > 30) {
				title = title.substring(0, 27) + "...";
			}

			return title;

		} catch (Exception e) {
			String fallbackTitle = userMessage.length() > 30 ? userMessage.substring(0, 27) + "..." : userMessage;
			logger.warn("Failed to generate AI title, using fallback: {}", fallbackTitle);
			return fallbackTitle;
		}
	}

	public JsonObject answer(AIInterviewPrepBotDTO dto) {
		logger.info("Processing AI interview prep request for applicant: {}, chat: {}", dto.getApplicantId(),
				dto.getChatId());
		try {

			Long chatId = dto.getChatId();

			Long applicantId = dto.getApplicantId();

			String userMessage = dto.getRequest();

			String title = null;
			if (chatId == null) {
				logger.debug("Generating title for new chat");
				title = generateAITitle(userMessage, applicantId);
				logger.debug("Generated title: {}", title);
			}

			AIPrepChatDTO chatDTO = aiPrepChatService.saveUserMessage(chatId, applicantId, userMessage, title);

			chatId = chatDTO.getChatId();

			List<Map<String, String>> history = aiPrepChatService.getChatHistory(chatId, applicantId);
			List<ApplicantProfile> profiles =

					applicantProfileRepository.findByApplicantIdIn(Collections.singletonList(applicantId));
			String extractedSkills = "No skills found";
			if (!profiles.isEmpty()) {
				Set<ApplicantSkills> skills = profiles.get(0).getSkillsRequired();
				if (skills != null && !skills.isEmpty()) {
					extractedSkills = skills.stream().map(ApplicantSkills::getSkillName)
							.collect(Collectors.joining(", "));
					logger.debug("Extracted skills for applicant {}: {}", applicantId, extractedSkills);
				} else {
					logger.debug("No skills found for applicant: {}", applicantId);
				}
			} else {
				logger.warn("No profile found for applicant: {}", applicantId);
			}

			JsonArray messages = new JsonArray();
			JsonObject systemMsg = new JsonObject();

			systemMsg.addProperty("role", "system");

			systemMsg.addProperty(

					"content",
					"You are an AI assistance bot called Ask Newton.\n" + "The applicant has the following skills: "
							+ extractedSkills + ".\n" + "Use these skills while answering any skill-related question.\n"
							+ "\n" + "⚠️ IMPORTANT — RESPONSE FORMAT RULES ⚠️\n"
							+ "You must ALWAYS respond ONLY in valid JSON format.\n"
							+ "Never send plain text outside JSON. Never wrap JSON in backticks.\n" + "\n"
							+ "Your response MUST follow EXACTLY this structure:\n" + "{\n"
							+ "   \"response\": \"your main answer here\",\n"
							+ "   \"followup\": [\"question 1\", \"question 2\", \"question 3\"]\n" + "}\n" + "\n"
							+ "Rules:\n" + "1. The value of 'response' MUST be a STRING.\n"
							+ "2. The value of 'followup' MUST be an ARRAY of 2–4 strings.\n"
							+ "3. Do NOT escape the word response or followup.\n"
							+ "4. Do NOT create nested JSON like {\"response\": { ... }}.\n"
							+ "5. Do NOT include markdown, backticks, or code fences.\n"
							+ "6. If code is included, put it inside the string of 'response'. Example:\n"
							+ "   { \"response\": \"Here is code:\\npublic class A { }\", \"followup\": [\"...\"] }\n"
							+ "7. Never add other fields except 'response' and 'followup'.\n"
							+ "8. FOLLOWUP RULE (STRICT):\n"
							+ "- The \"followup\" array MUST contain only statements written as if the USER is speaking to the bot.\n"
							+ "- Followups must represent possible next messages the user would send.\n"
							+ "- Do NOT ask the user anything.\n"
							+ "- Do NOT use questions, question marks, or assistant language.\n"
							+ "- Use first-person intent-driven phrasing.\n" + "\n" + "Correct examples:\n"
							+ "- \"want to change the topic\"\n" + "- \"want to learn this in depth\"\n"
							+ "- \"want to practice advanced interview questions\"\n"
							+ "- \"want to move to system design\"\n" + "\n" + "Incorrect examples:\n"
							+ "- \"Do you want to change the topic?\"\n" + "- \"Would you like to learn more?\"\n"
							+ "- \"Ask another question\"\n" + "- \"Choose an option\"\n"
							+ "If the user's question cannot be answered in JSON, still return:\n"
							+ "{ \"response\": \"I cannot answer that.\", \"followup\": [\"Ask another question?\"] }"

			);

			messages.add(systemMsg);

			for (Map<String, String> entry : history) {

				JsonObject msg = new JsonObject();

				msg.addProperty("role", entry.get("role"));

				msg.addProperty("content", entry.get("message"));

				messages.add(msg);

			}

			JsonObject requestBody = new JsonObject();

			requestBody.addProperty("model", groqModel);

			requestBody.add("messages", messages);

			requestBody.addProperty("temperature", 0.5);

			requestBody.addProperty("max_tokens", 800);
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_JSON);

			headers.set("Authorization", "Bearer " + groqApiKey);
			HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

			logger.debug("Sending request to Groq API");
			ResponseEntity<String> response = restTemplate.exchange(groqApiUrl, HttpMethod.POST, entity, String.class);
			String responseBody = response.getBody();
			logger.debug("Received response from Groq API");

			JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

			JsonArray choices = jsonResponse.getAsJsonArray("choices");
			if (choices != null && choices.size() > 0) {

				JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");

				String content = message.get("content").getAsString();
				JsonObject finalResponse;
				try {

					finalResponse = JsonParser.parseString(content).getAsJsonObject();

				} catch (Exception ex) {

					finalResponse = new JsonObject();

					finalResponse.addProperty("response", content);

				}
				if (!finalResponse.has("response")) {

					finalResponse.addProperty("response", content);

				}

				aiPrepChatService.saveBotMessage(chatId, applicantId,

						finalResponse.get("response").getAsString()

				);
				finalResponse.addProperty("chatId", chatId);

				return finalResponse;

			}
			// Fallback

			JsonObject fallback = new JsonObject();

			fallback.addProperty("response", "No valid response received.");

			fallback.addProperty("chatId", chatId);

			return fallback;
		} catch (RestClientException e) {
			logger.error("Error connecting to Groq API: {}", e.getMessage(), e);
			JsonObject error = new JsonObject();
			error.addProperty("response", "Connection error: " + e.getMessage());
			return error;
		} catch (Exception e) {
			logger.error("Unexpected error in answer method: {}", e.getMessage(), e);
			JsonObject error = new JsonObject();
			error.addProperty("response", "An unexpected error occurred. Please try again later.");
			return error;
		}

	}

	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void cleanupOldChats() {
		logger.info("Starting cleanup of old chats");
		try {
			LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
			logger.debug("Deleting chats older than: {}", cutoffDate);

			int deletedCount = aiPrepChatRepository.deleteOlderThan(cutoffDate);

			if (deletedCount > 0) {
				logger.info("Successfully deleted {} chats older than {}", deletedCount, cutoffDate);
			} else {
				logger.debug("No chats found older than {}", cutoffDate);
			}

		} catch (Exception e) {
			logger.error("Error during chat cleanup: {}", e.getMessage(), e);
			throw e;
		} finally {
			logger.info("Completed chat cleanup process");
		}
	}

}