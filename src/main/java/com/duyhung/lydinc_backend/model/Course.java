package com.duyhung.lydinc_backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "course")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String thumbnail;
    private LocalDate enrollmentDate;
    private String status = "ACTIVE";
    private String privacy;
    private String lecturerId;
    private String lecturerName;
    private String lecturerEmail;
    private String lecturerPhoto;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserCourse> userCourses;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;

    public Course(
            Integer courseId,
            String title,
            String description,
            String thumbnail,
            String status
    ) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.status = status;
    }
}
