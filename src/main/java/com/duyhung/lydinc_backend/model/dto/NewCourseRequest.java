package com.duyhung.lydinc_backend.model.dto;

import com.duyhung.lydinc_backend.model.dto.ModuleDto;
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
    private String lecturerId;
    private List<ModuleDto> modules;
}
