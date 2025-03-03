package com.duyhung.lydinc_backend.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long studentCount;
}
