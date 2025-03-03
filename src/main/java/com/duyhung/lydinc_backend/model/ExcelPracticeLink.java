package com.duyhung.lydinc_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "excel_practice_link")
public class ExcelPracticeLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String link;
    private String studentId;
    private Integer courseId;
    private String moduleId;
    private String lessonId;
    private Integer moduleIndex;
    private Integer lessonIndex;
}
