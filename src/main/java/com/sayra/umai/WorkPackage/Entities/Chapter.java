package com.sayra.umai.WorkPackage.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chapterTitle;
    @Column(nullable = false)
    private Integer chapterNumber;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name="work_id", nullable = false)
    @JsonBackReference
    private Work work;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "chapter",  cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Chunk> chunks = new HashSet<>();


}
