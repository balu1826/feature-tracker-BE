package com.talentstream.service;

import com.talentstream.dto.ProjectDetailsDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProject;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantProjectRepository;
import com.talentstream.repository.ApplicantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicantProjectService {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantProjectService.class);

	private final ApplicantRepository applicantRepository;
	private final ApplicantProjectRepository applicantProjectRepository;
	private final ApplicantProfileRepository profileRepo;

	public ApplicantProjectService(ApplicantRepository applicantRepository,
			ApplicantProjectRepository projectRepository, ApplicantProfileRepository profileRepo) {
		this.applicantRepository = applicantRepository;
		this.applicantProjectRepository = projectRepository;
		this.profileRepo = profileRepo;
	}

	@Transactional(readOnly = true)
	public List<ProjectDetailsDTO> getApplicantProjects(Long applicantId) {
		logger.info("Servive - Fetching projects for applicantId={}", applicantId);

		applicantRepository.findById(applicantId).orElseThrow(
				() -> new CustomException("Applicant not found with Id = " + applicantId, HttpStatus.NOT_FOUND));
		try {
			List<ApplicantProject> projects = applicantProjectRepository.findByApplicantId(applicantId);

			if (projects.isEmpty()) {
				logger.warn("Service - No projects found for applicantId={}", applicantId);
				throw new CustomException("No projects found for this applicant", HttpStatus.NOT_FOUND);
			}

			List<ProjectDetailsDTO> dtos = projects.stream().map(this::toDTO).collect(Collectors.toList());
			logger.debug("Service - Found {} projects for applicantId={}", dtos.size(), applicantId);
			return dtos;
		} catch (Exception ex) {
			logger.error("Service - Failed to fetch applicant projects for applicantId={}", applicantId, ex);
			throw new CustomException("Failed to fetch applicant projects", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ApplicantProjectService.java
	@Transactional
	public String saveApplicantProject(Long applicantId, ProjectDetailsDTO dto) {

		Applicant applicant = applicantRepository.findById(applicantId).orElseThrow(
				() -> new CustomException("Applicant not found with Id = " + applicantId, HttpStatus.NOT_FOUND));
		Integer count = applicantProjectRepository.countByApplicantId(applicantId);
		logger.debug("Service - Count of projects for applicantId={}: {}", applicantId, count);
		if (count >= 3) {
			throw new CustomException("Maximum of 3 projects allowed per applicant", HttpStatus.BAD_REQUEST);
		}
		try {
			ApplicantProject project = new ApplicantProject();
			project.setApplicant(applicant);
			project.setProjectTitle(dto.getProjectTitle());
			project.setSpecialization(dto.getSpecialization());
			project.setTechnologiesUsed(dto.getTechnologiesUsed());
			project.setTeamSize(dto.getTeamSize());
			project.setRoleInProject(dto.getRoleInProject());
			project.setRoleDescription(dto.getRoleDescription());
			project.setProjectDescription(dto.getProjectDescription());
			project.setUpdatedAt(LocalDateTime.now());
			profileRepo.findByApplicantId(applicantId).setUpdatedAt(LocalDateTime.now());
			logger.info("Service - Saving new project for applicantId={}", applicantId);
			applicantProjectRepository.save(project);
			logger.info("Service - Project saved for applicantId={}", applicantId);
			return "Project details updated successfully";
		} catch (Exception ex) {
			logger.error("Service - Failed to save applicant project for applicantId={}", applicantId, ex);
			throw new CustomException("Failed to save applicant project", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ProjectDetailsDTO toDTO(ApplicantProject project) {
		ProjectDetailsDTO dto = new ProjectDetailsDTO();
		dto.setId(project.getId());
		dto.setProjectTitle(project.getProjectTitle());
		dto.setSpecialization(project.getSpecialization());
		dto.setTechnologiesUsed(project.getTechnologiesUsed());
		dto.setTeamSize(project.getTeamSize());
		dto.setRoleInProject(project.getRoleInProject());
		dto.setRoleDescription(project.getRoleDescription());
		dto.setProjectDescription(project.getProjectDescription());
		return dto;
	}

	public String updateApplicantProject(Long applicantId, Long projectId, ProjectDetailsDTO dto) {
		applicantRepository.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found with Id = " + applicantId,
						HttpStatus.NOT_FOUND));

		ApplicantProject project = applicantProjectRepository.findByIdAndApplicantId(projectId, applicantId)
				.orElseThrow(() -> new CustomException(
						 "Project with Id :" + projectId + " not found for this applicant",
						HttpStatus.NOT_FOUND));
		try {
			logger.info("Service - Updating project applicantId={}, projectId={}", applicantId, projectId);
			project.setProjectTitle(dto.getProjectTitle());
			project.setSpecialization(dto.getSpecialization());
			project.setTechnologiesUsed(dto.getTechnologiesUsed());
			project.setTeamSize(dto.getTeamSize());
			project.setRoleInProject(dto.getRoleInProject());
			project.setRoleDescription(dto.getRoleDescription());
			project.setProjectDescription(dto.getProjectDescription());
			project.setUpdatedAt(LocalDateTime.now());
			profileRepo.findByApplicantId(applicantId).setUpdatedAt(LocalDateTime.now());

			applicantProjectRepository.save(project);
			logger.info("Service - Project updated for applicantId={}, projectId={}", applicantId, projectId);
			return "Project details updated successfully";

		} catch (Exception ex) {
			logger.error("Service - Failed to update applicant project applicantId={}, projectId={}", applicantId,
					projectId, ex);
			throw new CustomException("Failed to update applicant project", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String deleteApplicantProject(Long applicantId, Long projectId) {
		applicantRepository.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found with Id = " + applicantId,
						HttpStatus.NOT_FOUND));

		ApplicantProject project = applicantProjectRepository.findByIdAndApplicantId(projectId, applicantId)
				.orElseThrow(() -> new CustomException(
						"Project with Id :" + projectId + " not found for this applicant",
						HttpStatus.NOT_FOUND));
		try {
			logger.info("Service - Deleting project applicantId={}, projectId={}", applicantId, projectId);
			applicantProjectRepository.delete(project);
			profileRepo.findByApplicantId(applicantId).setUpdatedAt(LocalDateTime.now());
			logger.info("Service - Project deleted for applicantId={}, projectId={}", applicantId, projectId);
			return "Project deleted successfully";
		} catch (Exception ex) {
			logger.error("Service - Failed to delete applicant project applicantId={}, projectId={}", applicantId,
					projectId, ex);
			throw new CustomException("Failed to delete applicant project", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ProjectDetailsDTO getApplicantProjectById(Long applicantId, Long projectId) {
		logger.info("Service - Fetching project by id: applicantId={}, projectId={}", applicantId, projectId);
		applicantRepository.findById(applicantId)
				.orElseThrow(() -> new CustomException("Applicant not found with Id = " + applicantId,
						HttpStatus.NOT_FOUND));
		ApplicantProject project = applicantProjectRepository.findByIdAndApplicantId(projectId, applicantId)
				.orElseThrow(() -> new CustomException(
						"Project with Id :" + projectId + " not found for this applicant",
						HttpStatus.NOT_FOUND));
		logger.debug("Found project for applicantId={}, projectId={}", applicantId, projectId);
		return toDTO(project);
	}
}