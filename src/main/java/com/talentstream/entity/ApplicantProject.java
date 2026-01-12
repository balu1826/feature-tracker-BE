package com.talentstream.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applicant_projects")
public class ApplicantProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    private String projectTitle;
    private String specialization;
    private String technologiesUsed;
    private Integer teamSize;
    private String roleInProject;

    @Column(length = 1000)
    private String roleDescription;

    @Column(length = 2000)
    private String projectDescription;

    private LocalDateTime updatedAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Applicant getApplicant() { return applicant; }
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
