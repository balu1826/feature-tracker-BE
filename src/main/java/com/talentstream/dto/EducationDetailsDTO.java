package com.talentstream.dto;

import javax.validation.Valid;
import javax.validation.constraints.*;

public class EducationDetailsDTO {

    @Valid
    private GraduationDTO graduation = new GraduationDTO();

    @Valid
    private ClassXiiDTO classXii = new ClassXiiDTO();

    @Valid
    private ClassXDTO classX = new ClassXDTO();
    // ---- Graduation ----
    public static class GraduationDTO {

        @NotBlank(message = "Graduation/Diploma is required")
        @Size(min = 2, max = 100, message = "Graduation/Diploma must be between 2 and 100 characters")
        private String degree;

        @NotBlank(message = "University/Institute is required")
        @Size(min = 3, max = 100, message = "University/Institute name must be between 3 and 100 characters")
        private String university;

        @NotBlank(message = "Specialization is required")
        @Size(min = 2, max = 100, message = "Specialization must be between 2 and 100 characters")
        private String specialization;

        @NotBlank(message = "Course type is required")
        @Size(min = 2, max = 50, message = "Course type must be between 2 and 50 characters")
        private String courseType;

        @NotNull(message = "Course start year is required")
        @Min(1900) @Max(2100)
        private Integer startYear;

        @NotNull(message = "Course ending year is required")
        @Min(1900) @Max(2100)
        private Integer endYear;

        @NotNull(message = "Marks % is required")
        @DecimalMin("0.0") @DecimalMax("100.0")
        private Double marksPercent;

        // getters/setters
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }

        public String getUniversity() { return university; }
        public void setUniversity(String university) { this.university = university; }

        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }

        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }

        public Integer getStartYear() { return startYear; }
        public void setStartYear(Integer startYear) { this.startYear = startYear; }

        public Integer getEndYear() { return endYear; }
        public void setEndYear(Integer endYear) { this.endYear = endYear; }

        public Double getMarksPercent() { return marksPercent; }
        public void setMarksPercent(Double marksPercent) { this.marksPercent = marksPercent; }
    }

    // ---- Class XII ----
    public static class ClassXiiDTO {
        @NotBlank(message = "Board of education is required")
        private String board;

        @NotNull(message = "Passing out year is required")
        @Min(1900) @Max(2100)
        private Integer passingYear;

        @NotNull(message = "Marks % is required")
        @DecimalMin("0.0") @DecimalMax("100.0")
        private Double marksPercent;

        // getters/setters
        public String getBoard() { return board; }
        public void setBoard(String board) { this.board = board; }

        public Integer getPassingYear() { return passingYear; }
        public void setPassingYear(Integer passingYear) { this.passingYear = passingYear; }

        public Double getMarksPercent() { return marksPercent; }
        public void setMarksPercent(Double marksPercent) { this.marksPercent = marksPercent; }
    }

    // ---- Class X ----
    public static class ClassXDTO {
        @NotBlank(message = "Board of education is required")
        private String board;

        @NotNull(message = "Passing out year is required")
        @Min(1900) @Max(2100)
        private Integer passingYear;

        @NotNull(message = "Marks % is required")
        @DecimalMin("0.0") @DecimalMax("100.0")
        private Double marksPercent;

        // getters/setters
        public String getBoard() { return board; }
        public void setBoard(String board) { this.board = board; }

        public Integer getPassingYear() { return passingYear; }
        public void setPassingYear(Integer passingYear) { this.passingYear = passingYear; }

        public Double getMarksPercent() { return marksPercent; }
        public void setMarksPercent(Double marksPercent) { this.marksPercent = marksPercent; }
    }

    // parent getters/setters
    public GraduationDTO getGraduation() { return graduation; }
    public void setGraduation(GraduationDTO graduation) { this.graduation = graduation; }

    public ClassXiiDTO getClassXii() { return classXii; }
    public void setClassXii(ClassXiiDTO classXii) { this.classXii = classXii; }

    public ClassXDTO getClassX() { return classX; }
    public void setClassX(ClassXDTO classX) { this.classX = classX; }

}
