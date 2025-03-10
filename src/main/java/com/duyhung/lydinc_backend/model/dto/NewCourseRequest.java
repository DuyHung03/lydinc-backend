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
public class NewCourseRequest {
    private String title;
    private String description;
    private String thumbnail;
    private List<ModuleDto> modules;
}
