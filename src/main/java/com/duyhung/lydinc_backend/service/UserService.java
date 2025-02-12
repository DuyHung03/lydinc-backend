package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.Role;
import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.model.dto.UserListResponse;
import com.duyhung.lydinc_backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractService {

    private final UserRepository userRepository;
    private final CourseService courseService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserListResponse getAllAccounts(String adminId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.findAllExceptCurrent(adminId, pageable);
        List<User> userList = userPage.getContent();
        List<UserDto> users = userList.stream().map(this::mapUserToDto).toList();
        return UserListResponse.builder().users(users).total(userPage.getTotalPages()).pageNo(pageNo + 1).pageSize(pageSize).build();
    }

    public UserDto getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.builder()
                .userId(userId)
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toSet()))
                .isAccountGranted(user.getIsAccountGranted())
                .isPasswordFirstChanged(user.getIsPasswordFirstChanged())
                .build();
    }

    public String changePassword(String userId, String newPassword) throws MessagingException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the new password is the same as the old one
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new RuntimeException("New password should not be the same as the current password");
        }

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setIsPasswordFirstChanged(1);
        // Save the updated user
        userRepository.save(user);

        emailService.sendEmailChangePassword(user.getEmail(), user.getUsername());

        return "Password changed successfully!";
    }


}