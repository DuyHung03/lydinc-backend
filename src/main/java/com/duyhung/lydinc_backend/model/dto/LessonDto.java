package com.duyhung.lydinc_backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonDto {

    private String lessonId;
    private String lessonTitle;
    private String lessonContent;
    private ModuleDto module;

}
