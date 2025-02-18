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
public class EditPrivacyRequest {
    private String privacy;
    private Integer courseId;
    private List<Integer> universityIds;
    private List<Integer> deleteUniversityIds;
    private List<String> userIds;
}

