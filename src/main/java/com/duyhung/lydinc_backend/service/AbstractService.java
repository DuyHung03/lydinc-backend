package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Module;
import com.duyhung.lydinc_backend.model.*;
import com.duyhung.lydinc_backend.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class AbstractService {
    public CourseDto mapToCourseDto(Enrollment enrollment) {
        return CourseDto.builder().courseId(enrollment.getCourse().getCourseId()).title(enrollment.getCourse().getTitle()).status(enrollment.getCourse().getStatus()).enrollmentDate(enrollment.getEnrollmentDate()).lecturerId(enrollment.getCourse().getLecturerId()).lecturerEmail(enrollment.getCourse().getLecturerEmail()).lecturerName(enrollment.getCourse().getLecturerName()).lecturerPhoto(enrollment.getCourse().getLecturerPhoto()).build();
    }


    public List<EnrollmentDto> mapEnrollmentsToDtos(List<Enrollment> enrollments) {
        return enrollments.stream().map(this::mapEnrollmentToDto).collect(Collectors.toList());
    }

    public EnrollmentDto mapEnrollmentToDto(Enrollment enrollment) {
        return EnrollmentDto.builder().enrollmentId(enrollment.getEnrollmentId()).enrollmentDate(enrollment.getEnrollmentDate()).university(mapToUniversityDto(enrollment.getUniversity()))
                .build();
    }

    public UniversityDto mapToUniversityDto(University university) {
        return UniversityDto.builder()
                .universityId(university.getUniversityId())
                .shortName(university.getShortName())
                .fullName(university.getFullName())
                .logo(university.getLogo())
                .location(university.getLocation())
                .studentCount(university.getStudents().size())
                .build();
    }

    public UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .photoUrl(user.getPhotoUrl())
                .name(user.getName())
                .isPasswordFirstChanged(user.getIsPasswordFirstChanged())
                .isAccountGranted(user.getIsAccountGranted())
                .university(UniversityDto.builder()
                        .universityId(user.getUniversity().getUniversityId())
                        .shortName(user.getUniversity().getShortName())
                        .fullName(user.getUniversity().getFullName())
                        .build()
                )
                .build();
    }

    public LessonDto mapToLessonDto(Lesson lesson, Module module) {
        return LessonDto.builder()
                .lessonId(lesson.getLessonId())
                .lessonTitle(lesson.getLessonTitle())
                .lessonContent(lesson.getLessonContent())
                .module(ModuleDto.builder()
                        .moduleId(module.getModuleId())
                        .level(module.getLevel())
                        .index(module.getIndex())
                        .parentModuleId(module.getParentModuleId())
                        .status(module.getStatus())
                        .build())
                .build();
    }

}
