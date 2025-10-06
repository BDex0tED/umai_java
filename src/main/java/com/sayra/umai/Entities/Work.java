package com.sayra.umai.Entities;

import com.sayra.umai.Other.WorkStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

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
    private String author;

    private String genre;

    @Column(columnDefinition = "text")
    private String description;

    private String filepath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status = WorkStatus.PENDING;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created_at = LocalDateTime.now();

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters;

    public void setFilePath(String filepath) {
        this.filepath = filepath;
    }
}
