package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDto {

    private String moduleId;
    private String moduleTitle;
    private String status;
    private Integer index;
    private Integer level;
    private String parentModuleId;
    private Integer courseId;

}
