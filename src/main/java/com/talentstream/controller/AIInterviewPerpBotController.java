package com.talentstream.controller;

import com.google.gson.JsonObject;
import com.talentstream.dto.AIInterviewPrepBotDTO;
import com.talentstream.service.AIInterviewPrepBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aiPrepModel")
public class AIInterviewPerpBotController {
	private static final Logger logger = LoggerFactory.getLogger(AIInterviewPerpBotController.class);

	@Autowired
	private AIInterviewPrepBotService aiInterviewPrepBotService;

	@PostMapping("/postQuery")
	public ResponseEntity<?> query(@Valid @RequestBody AIInterviewPrepBotDTO request, BindingResult bindingResult) {
		logger.info("[{}] Received request: {}", request);

		if (request == null) {
			logger.warn("[{}] Request body is null");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Request body cannot be null\"}");
		}

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getFieldErrors().stream()
					.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.toList());
			logger.warn("[{}] Validation errors: {}", errors);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}

		try {
			logger.debug("[{}] Processing request with service");
			JsonObject jsonResponse = aiInterviewPrepBotService.answer(request);
			logger.info("[{}] Request processed successfully");
			return ResponseEntity.ok(jsonResponse.toString());
		} catch (Exception e) {
			logger.error("Error processing request", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"Internal server error\"}");
		}
	}
}
