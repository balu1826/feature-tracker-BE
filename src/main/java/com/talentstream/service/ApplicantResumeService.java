package com.talentstream.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantResume;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantResumeRepository;
import com.talentstream.repository.RegisterRepository;
import com.talentstream.AwsSecretsManagerUtil;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class ApplicantResumeService {
	private static final Logger logger = LoggerFactory.getLogger(ApplicantResumeService.class);

	@Autowired
	private AwsSecretsManagerUtil secretsManagerUtil;

	private String bucketName;

	@Autowired
	private ApplicantResumeRepository applicantResumeRepository;

	@Autowired
	private RegisterRepository applicantService;

	private AmazonS3 initializeS3Client() {
		try {
			logger.debug("Initializing S3 client");
			String secret = secretsManagerUtil.getSecret();
			JSONObject jsonObject = new JSONObject(secret);
			String accessKey = jsonObject.getString("AWS_ACCESS_KEY_ID");
			String secretKey = jsonObject.getString("AWS_SECRET_ACCESS_KEY");
			bucketName = jsonObject.getString("AWS_S3_BUCKET_NAME");
			String region = jsonObject.getString("AWS_REGION");

			if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
				logger.error("AWS credentials are missing or invalid");
				throw new CustomException("AWS credentials are not properly configured",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
			logger.debug("Successfully initialized S3 client for region: {}", region);
			return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
					.withRegion(Regions.fromName(region)).build();
		} catch (Exception e) {
			logger.error("Failed to initialize S3 client: {}", e.getMessage(), e);
			throw new CustomException("Failed to initialize S3 client: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ObjectMetadata createObjectMetadata(MultipartFile file) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());
		return metadata;
	}

	public String UploadPdf(long applicantId, MultipartFile pdfFile) throws IOException {
		logger.info("Starting PDF upload for applicant ID: {}", applicantId);

		if (pdfFile == null || pdfFile.isEmpty()) {
			logger.error("No file provided for applicant ID: {}", applicantId);
			throw new CustomException("No file provided", HttpStatus.BAD_REQUEST);
		}

		if (pdfFile.getSize() > 5 * 1024 * 1024) {
			logger.warn("File size {} bytes exceeds 5MB limit for applicant ID: {}", pdfFile.getSize(), applicantId);
			throw new CustomException("File size should be less than 5MB.", HttpStatus.BAD_REQUEST);
		}

		String contentType = pdfFile.getContentType();
		if (!"application/pdf".equals(contentType)) {
			logger.warn("Invalid file type: {} for applicant ID: {}", contentType, applicantId);
			throw new CustomException("Only PDF file types are allowed.", HttpStatus.BAD_REQUEST);
		}

		logger.debug("Validating applicant with ID: {}", applicantId);
		Applicant applicant = applicantService.getApplicantById(applicantId);
		if (applicant == null) {
			logger.error("Applicant not found with ID: {}", applicantId);
			throw new CustomException("Applicant not found for ID: " + applicantId, HttpStatus.NOT_FOUND);
		}

		try {
			logger.debug("Initializing S3 client for file upload");
			AmazonS3 s3Client = initializeS3Client();

			// Delete existing resumes for this applicant
			String prefix = "resumes/" + applicantId + "_";
			logger.debug("Checking for existing resumes with prefix: {}", prefix);

			ObjectListing list = s3Client.listObjects(bucketName, prefix);
			int deletedCount = list.getObjectSummaries().size();
			if (deletedCount > 0) {
				logger.info("Deleting {} existing resume(s) for applicant ID: {}", deletedCount, applicantId);
				list.getObjectSummaries().forEach(obj -> {
					logger.debug("Deleting old resume: {}", obj.getKey());
					s3Client.deleteObject(bucketName, obj.getKey());
				});
			}

			String objectKey = prefix + pdfFile.getOriginalFilename();
			logger.debug("Uploading new resume with key: {}", objectKey);

			s3Client.putObject(new PutObjectRequest(bucketName, objectKey, pdfFile.getInputStream(),
					createObjectMetadata(pdfFile)));
			logger.info("Successfully uploaded resume to S3: {}/{}", bucketName, objectKey);

			// Update or create resume record in database
			ApplicantResume existingResume = applicantResumeRepository.findByApplicant(applicant);
			if (existingResume != null) {
				logger.debug("Updating existing resume record for applicant ID: {}", applicantId);
				existingResume.setPdfname(objectKey);
				applicantResumeRepository.save(existingResume);
			} else {
				logger.debug("Creating new resume record for applicant ID: {}", applicantId);
				ApplicantResume newResume = new ApplicantResume();
				newResume.setPdfname(objectKey);
				newResume.setApplicant(applicant);
				applicantResumeRepository.save(newResume);
			}

			String originalFilename = pdfFile.getOriginalFilename();
			logger.info("Successfully processed resume upload for applicant ID: {}. Original filename: {}", applicantId,
					originalFilename);
			return originalFilename;

		} catch (AmazonServiceException e) {
			String errorMsg = String.format("AWS Service Error uploading file for applicant ID %d: %s", applicantId,
					e.getMessage());
			logger.error(errorMsg, e);
			throw new CustomException("Error uploading file to S3: " + e.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			String errorMsg = String.format("Unexpected error processing file for applicant ID %d", applicantId);
			logger.error(errorMsg, e);
			throw new CustomException("Error processing file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<org.springframework.core.io.Resource> getResumeByApplicantId(long applicantId) {
		logger.info("Retrieving resume for applicant ID: {}", applicantId);

		try {
			logger.debug("Looking up resume in database for applicant ID: {}", applicantId);
			ApplicantResume applicantResume = applicantResumeRepository.findByApplicantId(applicantId);
			if (applicantResume == null) {
				logger.warn("No resume found in database for applicant ID: {}", applicantId);
				throw new CustomException("Resume not found for applicant ID: " + applicantId, HttpStatus.NOT_FOUND);
			}

			String objectKey = applicantResume.getPdfname();
			logger.debug("Found resume in database. S3 object key: {}", objectKey);

			logger.debug("Initializing S3 client to retrieve file");
			AmazonS3 s3Client = initializeS3Client();

			logger.debug("Retrieving object from S3: {}/{}", bucketName, objectKey);
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));

			if (s3Object == null) {
				logger.error("S3 object is null for key: {}/{}", bucketName, objectKey);
				throw new CustomException("Failed to retrieve resume from storage", HttpStatus.INTERNAL_SERVER_ERROR);
			}

			S3ObjectInputStream inputStream = s3Object.getObjectContent();
			String filename = objectKey.substring(objectKey.lastIndexOf('/') + 1);
			logger.debug("Successfully retrieved resume. Filename: {}", filename);

			InputStreamResource resource = new InputStreamResource(inputStream);

			logger.info("Successfully prepared resume download for applicant ID: {} as {}", applicantId, filename);

			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.body(resource);

		} catch (AmazonServiceException e) {
			String errorMsg = String.format("AWS Service Error retrieving resume for applicant ID %d: %s", applicantId,
					e.getMessage());
			logger.error(errorMsg, e);
			throw new CustomException("Error retrieving file from S3: " + e.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (CustomException e) {
			// Re-throw CustomException as is, but log it first
			logger.error("Custom exception while retrieving resume for applicant ID {}: {}", applicantId,
					e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			String errorMsg = String.format("Unexpected error retrieving resume for applicant ID %d", applicantId);
			logger.error(errorMsg, e);
			throw new CustomException("Error processing resume: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}