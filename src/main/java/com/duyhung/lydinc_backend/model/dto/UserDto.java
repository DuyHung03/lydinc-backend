package com.duyhung.lydinc_backend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private String phone;
    private String photoUrl;
    private String name;
    private Integer isPasswordChanged;
    private Integer isAccountGranted;
    private Set<String> roles;
    private Integer universityId;
    private String universityName;

    public UserDto(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
