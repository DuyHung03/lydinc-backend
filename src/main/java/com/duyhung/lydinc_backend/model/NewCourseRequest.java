package com.duyhung.lydinc_backend.model;

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
    private List<String> userIds;


}
