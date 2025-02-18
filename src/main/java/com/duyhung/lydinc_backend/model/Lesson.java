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
    private String text;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
