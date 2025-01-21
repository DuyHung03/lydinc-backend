package com.duyhung.lydinc_backend.service;

import com.duyhung.lydinc_backend.model.User;
import com.duyhung.lydinc_backend.model.dto.UserDto;
import com.duyhung.lydinc_backend.model.dto.UserListResponse;
import com.duyhung.lydinc_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CourseService courseService;


    public UserListResponse getAllAccounts(String adminId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> userPage = userRepository.findAllExceptCurrent(adminId, pageable);
        List<User> userList = userPage.getContent();
        List<UserDto> users = userList.stream().map(courseService::mapUserToDto).toList();
        return UserListResponse.builder().users(users).total(userPage.getTotalPages()).pageNo(pageNo + 1).pageSize(pageSize).build();
    }

}