package com.duyhung.lydinc_backend.model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String faculty;
    private Integer universityId;
}
