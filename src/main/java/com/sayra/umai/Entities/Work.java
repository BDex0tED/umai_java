package com.sayra.umai.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sayra.umai.Other.WorkStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name="works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

//    @ManyToOne
//    @JoinColumn(name = "author_id", nullable = false)
//    private Author author; потом можно будет сделать словарь и выбирать в админке
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author", nullable = false)
    private Author author;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="work_genres",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres;

    @Column(columnDefinition = "text")
    private String description;

    private String filepath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status = WorkStatus.PENDING;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created_at = LocalDateTime.now();

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Chapter> chapters;

    public void setFilePath(String filepath) {
        this.filepath = filepath;
    }
}
