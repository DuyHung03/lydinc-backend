package com.duyhung.lydinc_backend.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniversityDto {

    private Integer universityId;
    private String shortName;
    private String fullName;
    private String logo;
    private String location;
    private List<UserDto> students;

}
