package com.talentstream.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProjectDetailsDTO {

    private Long id;

    @NotBlank(message = "Project title is required")
    @Size(min = 3, max = 500, message = "Project title must be between 3 and 500 characters")
    private String projectTitle;

    @NotBlank(message = "Specialization on the project is required")
    @Size(min = 2, max = 500, message = "Specialization must be between 2 and 500 characters")
    private String specialization;

    @NotBlank(message = "Technologies used is required")
    @Size(min = 1, max = 500, message = "Technologies used must be between 2 and 500 characters")
    private String technologiesUsed;

    @NotNull(message = "Team size is required")
    @Min(value = 1, message = "Team size must be at least 1")
    private Integer teamSize;

    @NotBlank(message = "Your role in project is required")
    @Size(min = 3, max = 500, message = "Role in project must be between 3 and 500 characters")
    private String roleInProject;

    @NotBlank(message = "Role description is required")
    @Size(min = 10, max = 5000, message = "Role description must be between 10 and 5000 characters")
    private String roleDescription;

    @NotBlank(message = "Project description is required")
    @Size(min = 10, max = 5000, message = "Project description must be between 10 and 5000 characters")
    private String projectDescription;

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getTechnologiesUsed() { return technologiesUsed; }
    public void setTechnologiesUsed(String technologiesUsed) { this.technologiesUsed = technologiesUsed; }

    public Integer getTeamSize() { return teamSize; }
    public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }

    public String getRoleInProject() { return roleInProject; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

    public String getRoleDescription() { return roleDescription; }
    public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }

    public String getProjectDescription() { return projectDescription; }
    public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }
}
