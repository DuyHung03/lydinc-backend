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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String lessonId;
    private String lessonTitle;

    private String lessonContent;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
