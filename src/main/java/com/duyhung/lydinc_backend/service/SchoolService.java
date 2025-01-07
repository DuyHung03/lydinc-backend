package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.School;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.SchoolDto;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.SchoolRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    public School createNewSchool(String name) {
        return schoolRepository.save(School.builder().schoolName(name).build());
    }

    public List<SchoolDto> getAllSchools() {
        List<School> schools = schoolRepository.findAll();

        return schools.stream().map(school -> SchoolDto.builder()
                        .schoolId(school.getSchoolId())
                        .schoolName(school.getSchoolName())
                        .students(school.getStudents().stream().map(courseService::mapUserToDto)
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    public List<UserDto> getStudentsOfSchool(Integer schoolId) {
        List<User> users = userRepository.findBySchoolSchoolId(schoolId)
                .orElseThrow(() -> new RuntimeException("School Not Found"));

        return users.stream().map(courseService::mapUserToDto).collect(Collectors.toList());
    }

}
