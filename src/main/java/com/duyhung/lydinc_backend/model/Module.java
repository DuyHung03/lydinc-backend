package com.duyhung.lydinc_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "module")
public class Module {

    @Id
    private String moduleId;
    private String moduleTitle;
    private String status;
    private Integer level;
    private Integer index;
    private String parentModuleId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Lesson> lessons;

}
