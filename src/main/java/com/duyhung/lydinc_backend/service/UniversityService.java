package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.UniversityDto;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService extends AbstractService {

    private final UniversityRepository universityRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    public University createNewUniversity(String shortName, String fullName, String logo, String location) {
        return universityRepository.save(University.builder()
                .shortName(shortName)
                .fullName(fullName)
                .logo(logo)
                .location(location)
                .build());
    }

    public List<UniversityDto> getAllUniversities() {
        List<University> universities = universityRepository.findAllUniversities();

        return universities.stream().map(this::mapToUniversityDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getStudentsOfUniversity(Integer universityId) {
        List<User> users = userRepository.findByUniversityUniversityId(universityId)
                .orElseThrow(() -> new RuntimeException("University Not Found"));

        return users.stream().map(this::mapUserToDto).collect(Collectors.toList());
    }
}
