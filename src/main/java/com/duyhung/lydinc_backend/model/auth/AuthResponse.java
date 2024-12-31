package com.duyhung.lydinc_backend.model.auth;

import com.duyhung.lydinc_backend.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Integer code;
    private UserDto user;
}
