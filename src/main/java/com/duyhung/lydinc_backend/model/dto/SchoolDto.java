package com.duyhung.lydinc_backend.model.dto;


import com.duyhung.lydinc_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDto {

    private Integer schoolId;
    private String schoolName;
    private List<UserDto> students;

}
