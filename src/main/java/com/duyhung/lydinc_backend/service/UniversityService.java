package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.University;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.UniversityDto;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.repository.UniversityRepository;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(UniversityService.class);

    private final UniversityRepository universityRepository;
    private final CourseService courseService;
    private final UserRepository userRepository;

    /**
     * Creates a new university and saves it to the database.
     *
     * @param shortName the short name of the university
     * @param fullName  the full name of the university
     * @param logo      the university logo URL
     * @param location  the location of the university
     * @return the created University entity
     */
    public University createNewUniversity(String shortName, String fullName, String logo, String location) {
        logger.info("Creating new university: shortName={}, fullName={}", shortName, fullName);

        University university = University.builder()
                .shortName(shortName)
                .fullName(fullName)
                .logo(logo)
                .location(location)
                .build();

        University savedUniversity = universityRepository.save(university);
        logger.info("University created successfully with ID: {}", savedUniversity.getUniversityId());

        return savedUniversity;
    }

    /**
     * Retrieves all universities from the database.
     *
     * @return a list of UniversityDto objects containing university details
     */
    public List<UniversityDto> getAllUniversities() {
        logger.info("Fetching all universities from the database...");

        List<University> universities = universityRepository.findAllUniversities();

        if (universities.isEmpty()) {
            logger.warn("No universities found in the database.");
        } else {
            logger.info("Retrieved {} universities.", universities.size());
        }

        return universities.stream()
                .map(this::mapToUniversityDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all students associated with a specific university.
     *
     * @param universityId the ID of the university
     * @return a list of UserDto objects representing students of the university
     */
    public List<UserDto> getStudentsOfUniversity(Integer universityId) {
        logger.info("Fetching students for universityId: {}", universityId);

        List<User> users = userRepository.findByUniversityUniversityId(universityId)
                .orElseThrow(() -> {
                    logger.error("University with ID {} not found.", universityId);
                    return new RuntimeException("University Not Found");
                });

        logger.info("Found {} students for universityId: {}", users.size(), universityId);

        return users.stream()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }
}
