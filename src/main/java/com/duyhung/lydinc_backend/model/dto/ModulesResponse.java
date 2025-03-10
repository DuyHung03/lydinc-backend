package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModulesResponse {
    private Integer courseId;
    private String courseTitle;
    private String description;
    private String thumbnail;
    private List<ModuleDto> modules;
}
