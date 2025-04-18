package com.duyhung.lydinc_backend.model;


import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    private String lessonId;
    private Integer index;
    private Integer type;
    private String url;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
