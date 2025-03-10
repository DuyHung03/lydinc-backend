package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {
    private Integer courseId;
    private String title;
    private LocalDate enrollmentDate;
    private String status;
    private String privacy;
    private String lecturerId;
    private String lecturerName;
    private String lecturerEmail;
    private String lecturerPhoto;
    private String practiceLink;
    private String description;
    private String thumbnail;
    private List<EnrollmentDto> enrollments;
}
