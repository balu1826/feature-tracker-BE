package com.talentstream.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import com.talentstream.exception.CustomException;
import com.talentstream.exception.UnsupportedFileTypeException;
import com.talentstream.service.ApplicantResumeService;

@RestController
@RequestMapping("/applicant-pdf")
public class ApplicantResumeController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantResumeController.class);

	@Autowired
	private ApplicantResumeService applicantResumeService;

	@PostMapping("/{applicantId}/upload")
	public String fileUpload(@PathVariable Long applicantId, @RequestParam("resume") MultipartFile resume) {
		logger.info("Received request to upload resume for applicant ID: {}", applicantId);

		if (resume == null || resume.isEmpty()) {
			logger.warn("Empty or null resume file received for applicant ID: {}", applicantId);
			return "Resume file is required";
		}

		logger.debug("File details - Name: {}, Size: {} bytes, Content Type: {}", resume.getOriginalFilename(),
				resume.getSize(), resume.getContentType());

		try {
			String filename = this.applicantResumeService.UploadPdf(applicantId, resume);
			logger.info("Successfully uploaded resume for applicant ID: {}. Filename: {}", applicantId, filename);
			return "Resume uploaded successfully. Filename: " + filename;
		} catch (CustomException ce) {
			logger.error("Custom exception while uploading resume for applicant ID: {}. Error: {}", applicantId,
					ce.getMessage(), ce);
			return ce.getMessage();
		} catch (UnsupportedFileTypeException e) {
			logger.warn("Unsupported file type attempted by applicant ID: {}. File type: {}", applicantId,
					resume.getContentType());
			return "Only PDF files are allowed.";
		} catch (MaxUploadSizeExceededException e) {
			logger.warn("File size exceeded limit for applicant ID: {}. File size: {} bytes", applicantId,
					resume.getSize());
			return "File size should be less than 5MB.";
		} catch (Exception e) {
			logger.error("Unexpected error while uploading resume for applicant ID: {}", applicantId, e);
			return "Resume not uploaded successfully due to an internal error";
		}
	}

	@GetMapping("/getresume/{applicantId}")
	public ResponseEntity<Resource> getResume(@PathVariable long applicantId) throws IOException {
		logger.info("Request to download resume for applicant ID: {}", applicantId);
		try {
			ResponseEntity<Resource> response = applicantResumeService.getResumeByApplicantId(applicantId);
			logger.debug("Successfully retrieved resume for applicant ID: {}", applicantId);
			return response;
		} catch (Exception e) {
			logger.error("Error retrieving resume for applicant ID: {}", applicantId, e);
			throw e;
		}
	}
}