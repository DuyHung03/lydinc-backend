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
@Table(name = "university")
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer universityId;
    String shortName;
    String fullName;
    String logo;
    String location;
    Long studentCount;

    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> students;

    public University(
            Integer universityId,
            String shortName,
            String fullName,
            String logo,
            String location,
            Long studentCount
    ) {
        this.universityId = universityId;
        this.shortName = shortName;
        this.fullName = fullName;
        this.logo = logo;
        this.location = location;
        this.studentCount = studentCount;
    }
}
