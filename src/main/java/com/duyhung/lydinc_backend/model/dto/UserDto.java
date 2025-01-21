package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String phone;
    private String photoUrl;
    private String name;
    private Integer isPasswordFirstChanged;
    private Integer isAccountGranted;
    private Set<String> roles;
    private UniversityDto university;

    public UserDto(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
