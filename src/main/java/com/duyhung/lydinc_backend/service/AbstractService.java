package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Enrollment;
import com.duyhung.lydinc_backend.model.Lesson;
import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class AbstractService {
    public UniversityDto mapToUniversityDto(University university) {
        return UniversityDto.builder()
                .universityId(university.getUniversityId())
                .shortName(university.getShortName())
                .fullName(university.getFullName())
                .logo(university.getLogo())
                .location(university.getLocation())
                .studentCount(university.getStudentCount())
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
                .isPasswordChanged(user.getIsPasswordChanged())
                .isAccountGranted(user.getIsAccountGranted())
                .universityId(user.getUniversity() != null ? user.getUniversity().getUniversityId() : null)
                .build();
    }

    public LessonDto mapToLessonDto(Lesson lesson) {
        return LessonDto.builder()
                .lessonId(lesson.getLessonId())
                .index(lesson.getIndex())
                .type(lesson.getType())
                .text(lesson.getText())
                .url(lesson.getUrl())
                .fileName(lesson.getFileName())
                .build();
    }
}
