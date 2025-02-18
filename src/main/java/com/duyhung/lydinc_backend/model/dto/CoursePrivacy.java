package com.duyhung.lydinc_backend.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CoursePrivacy {
    Integer courseId;
    String privacy;
    List<Integer> universityIds;
    List<String> userIds;
}
